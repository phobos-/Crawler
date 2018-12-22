package crawler

import java.util.concurrent.TimeUnit

import akka.actor.{ ActorRef, ActorSystem }
import akka.pattern.ask
import akka.routing.RoundRobinPool
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import domain.Entry
import fetcher.Fetcher
import org.jsoup.nodes.Document
import parser.BashOrgParser
import timing.TimedResult
import writer.JsonWriter

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{ Await, Future }

object Crawler {

  private val entriesPerPage     = 20
  private val pageTimeoutSeconds = 10

  private val config     = ConfigFactory.load("config.conf")
  private val url        = config.getString("url")
  private val outputFile = config.getString("outputFile")

  private lazy val system: ActorSystem = ActorSystem("crawler")
  private lazy val fetcherPool: ActorRef =
    system.actorOf(Fetcher.props.withRouter(RoundRobinPool(Runtime.getRuntime.availableProcessors)), name = "pool")

  def crawlBashOrg(numEntries: Int): Unit = {
    val lastPage = Math.ceil(numEntries.toDouble / entriesPerPage).toInt
    val duration =
      new FiniteDuration(lastPage * pageTimeoutSeconds, TimeUnit.SECONDS)

    def addToQueue(url: String): Future[TimedResult[Document]] =
      ask(fetcherPool, url)(new Timeout(duration)).mapTo[TimedResult[Document]]

    val pages = (1 to lastPage).map {
      case p if p == lastPage && numEntries % entriesPerPage != 0 =>
        addToQueue(Fetcher.createPageUrl(url, p).toString) -> numEntries % entriesPerPage
      case p =>
        addToQueue(Fetcher.createPageUrl(url, p).toString) -> entriesPerPage
    }

    val results =
      Await.result(Future.sequence(pages.map(p => p._1.map(d => d -> BashOrgParser.parse(d.result, p._2)))), duration)
    val pageData  = results.map(_._1)
    val entryData = results.flatMap(_._2)
    JsonWriter.save(entryData.map(_.result), outputFile)
    printStats(entryData, pageData)
    system.terminate
  }

  private def printStats(entries: Iterable[TimedResult[Entry]], pages: Iterable[TimedResult[Document]]): Unit = {
    val entryTimings = entries.map(_.time)
    val avgEntryTime = (0L /: entryTimings)(_ + _) / entryTimings.size
    val pageTimings  = pages.map(_.time)
    val avgPageTime  = (0L /: pageTimings)(_ + _) / pageTimings.size
    print(
      s"Pages fetched: ${pageTimings.size}, average page fetching time: $avgPageTime ns\n" +
      s"Entries fetched: ${entryTimings.size}, average page fetching time: $avgEntryTime ns"
    )
  }
}
