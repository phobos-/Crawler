package crawler

import java.util.concurrent.TimeUnit

import akka.actor.{ActorRef, ActorSystem}
import akka.pattern.ask
import akka.routing.RoundRobinPool
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import fetcher.Fetcher
import org.jsoup.nodes.Document
import parser.BashOrgParser
import writer.JsonWriter

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{Await, Future}

object Crawler {

  private val entriesPerPage = 20
  private val pageTimeoutSeconds = 10

  private val config = ConfigFactory.load("config.conf")
  private val url = config.getString("url")
  private val outputFile = config.getString("outputFile")
  private lazy val system: ActorSystem = ActorSystem("crawler")

  val fetcherPool: ActorRef = system.actorOf(
    Fetcher.props.withRouter(
      RoundRobinPool(Runtime.getRuntime.availableProcessors)),
    name = "pool")

  def saveQuotes(numEntries: Int): Unit = {
    val lastPage: Int = Math.ceil(numEntries / entriesPerPage).toInt
    val duration =
      new FiniteDuration(lastPage * pageTimeoutSeconds, TimeUnit.SECONDS)
    val timeout: Timeout = new Timeout(duration)
    val pages = (0 to lastPage).map(
      p =>
        ask(fetcherPool, Fetcher.createPageUrl(url, p + 1).toString)(timeout)
          .mapTo[Document])
    val results = Await.result(
      Future.sequence(pages.map(_.map(BashOrgParser.parse))),
      duration)
    JsonWriter.save(results.flatten.take(numEntries), outputFile)
    system.terminate
  }
}
