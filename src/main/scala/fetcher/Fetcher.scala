package fetcher

import java.net.URL

import akka.actor.{Actor, ActorLogging, Props}
import org.jsoup.Jsoup
import timing.Timer

class Fetcher extends Actor with ActorLogging with Timer {
  override def receive: Receive = {
    case url: String =>
      time {
        sender() ! Jsoup.connect(url).get()
      }(s"Fetched $url in ", log.info)
  }
}

object Fetcher {
  private val pageUrl = "?page=%d"

  def createPageUrl(url: String, page: Int): URL =
    if (url.endsWith("/")) {
      new URL(url + pageUrl.format(page))
    } else {
      new URL(s"$url/${pageUrl.format(page)}")
    }

  def props: Props = Props[Fetcher]
}
