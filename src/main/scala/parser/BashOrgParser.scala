package parser

import domain.Entry
import org.jsoup.nodes.Document
import timing.{ TimedResult, Timer }

import scala.collection.JavaConverters._
import scala.util.Try

object BashOrgParser extends Timer {

  private val POST   = "div[class=q post]"
  private val POINTS = "span[class=points]"
  private val ID     = "a[class=qid click]"
  private val TEXT   = "div[class=quote post-content post-body]"

  def parse(page: Document, numEntries: Int): List[TimedResult[Entry]] =
    page
      .body()
      .select(POST)
      .asScala
      .take(numEntries)
      .map { p =>
        profile {
          val points = Try(p.selectFirst(POINTS).text().toLong).getOrElse(-1L)
          val id     = Try(p.selectFirst(ID).text().tail.toLong).getOrElse(-1L)
          val text   = Try(p.selectFirst(TEXT).text()).getOrElse("")
          Entry(id, points, text)
        }
      }
      .toList
}
