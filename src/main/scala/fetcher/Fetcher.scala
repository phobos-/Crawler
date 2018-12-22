package fetcher

import java.net.URL

import akka.actor.{ Actor, Props }
import org.jsoup.Jsoup
import timing.Timer

class Fetcher extends Actor with Timer {
  override def receive: Receive = {
    case url: String => sender() ! profile(Jsoup.connect(url).get())
  }
}

object Fetcher {
  private val pageUrl = "?page=%d"

  def createPageUrl(url: String, page: Int): String =
    if (url.endsWith("/")) {
      new URL(url + pageUrl.format(page)).toString
    } else {
      new URL(s"$url/${pageUrl.format(page)}").toString
    }

  def props: Props = Props[Fetcher]
}
