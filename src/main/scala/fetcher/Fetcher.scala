package fetcher

import java.net.URL

import akka.actor.{Actor, Props}
import org.jsoup.Jsoup

class Fetcher extends Actor {
  override def receive: Receive = {
    case url: String => sender() ! Jsoup.connect(url).get()
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
