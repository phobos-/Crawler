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
  private val poolSize           = Runtime.getRuntime.availableProcessors

  private val config     = ConfigFactory.load("config.conf")
  private val url        = config.getString("url")
  private val outputFile = config.getString("outputFile")

  private lazy val system: ActorSystem = ActorSystem("crawler")
  private lazy val fetcherPool: ActorRef =
    system.actorOf(Fetcher.props.withRouter(RoundRobinPool(poolSize)), name = "pool")

  def crawlBashOrg(numEntries: Int): Unit = {
    val lastPage = Math.ceil(numEntries.toDouble / entriesPerPage).toInt
    val duration = new FiniteDuration(lastPage * pageTimeoutSeconds, TimeUnit.SECONDS)
    val timeout  = new Timeout(duration)

    val pages = (1 to lastPage).map {
      // format: off
      case p if p == lastPage && numEntries % entriesPerPage != 0 =>
        addToQueue(url, p, timeout) -> numEntries % entriesPerPage
      // format: on
      case p =>
        addToQueue(url, p, timeout) -> entriesPerPage
    }

    val results =
      Await.result(Future.sequence(pages.map(p => p._1.map(d => d -> BashOrgParser.parse(d.result, p._2)))), duration)
    val pageData  = results.map(_._1)
    val entryData = results.flatMap(_._2)
    JsonWriter.save(entryData.map(_.result), outputFile)
    printStats(entryData, pageData)
    system.terminate
  }

  private def addToQueue(url: String, page: Int, timeout: Timeout): Future[TimedResult[Document]] =
    ask(fetcherPool, Fetcher.createPageUrl(url, page))(timeout).mapTo[TimedResult[Document]]

  private def printStats(entries: Iterable[TimedResult[Entry]], pages: Iterable[TimedResult[Document]]): Unit = {
    def avgTime(timings: Iterable[Long]): Long = (0L /: timings)(_ + _) / timings.size

    val entryTimings = entries.map(_.time)
    val avgEntryTime = avgTime(entryTimings)
    val pageTimings  = pages.map(_.time)
    val avgPageTime  = avgTime(pageTimings)

    print(
      s"Pages fetched: ${pageTimings.size}, average page fetching time: $avgPageTime ns\n" +
      s"Entries fetched: ${entryTimings.size}, average page fetching time: $avgEntryTime ns"
    )
  }
}
