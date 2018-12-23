package parser

import akka.actor.{Actor, ActorLogging, Props}
import domain.{Entry, Page}
import timing.{TimedResult, Timer}

import scala.collection.JavaConverters._
import scala.util.Try

class BashOrgParser extends Actor with ActorLogging {
  override def receive: Receive = {
    case page: Page =>
      log.debug("received page " + page.element.baseUri())
      sender() ! BashOrgParser.parse(page)
  }
}

object BashOrgParser extends Timer {

  private val POST   = "div[class=q post]"
  private val POINTS = "span[class=points]"
  private val ID     = "a[class=qid click]"
  private val TEXT   = "div[class=quote post-content post-body]"

  def parse(page: Page): List[TimedResult[Entry]] =
    page.element
      .select(POST)
      .asScala
      .take(page.numEntries)
      .map { p =>
        profile {
          val points = Try(p.selectFirst(POINTS).text().toLong).getOrElse(-1L)
          val id     = Try(p.selectFirst(ID).text().tail.toLong).getOrElse(-1L)
          val text   = Try(p.selectFirst(TEXT).text()).getOrElse("")
          Entry(id, points, text)
        }
      }
      .toList

  def props: Props = Props[BashOrgParser]
}
