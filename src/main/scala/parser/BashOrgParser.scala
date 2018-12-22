package parser

import domain.QuoteModel
import org.jsoup.nodes.Document

import scala.collection.JavaConverters._
import scala.util.Try

object BashOrgParser {

  private val POST = "div[class=q post]"
  private val POINTS = "span[class=points]"
  private val ID = "a[class=qid click]"
  private val TEXT = "div[class=quote post-content post-body]"

  def parse(page: Document): List[QuoteModel] = {
    page
      .body()
      .select(POST)
      .asScala
      .map { p =>
        val points = Try(p.selectFirst(POINTS).text().toLong).getOrElse(-1L)
        val id = Try(p.selectFirst(ID).text().tail.toLong).getOrElse(-1L)
        val text = Try(p.selectFirst(TEXT).text()).getOrElse("")
        QuoteModel(id, points, text)
      }
      .toList
  }
}
