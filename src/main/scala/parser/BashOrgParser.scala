package parser

import domain.QuoteModel
import org.jsoup.nodes.Document
import scala.collection.JavaConverters._

object BashOrgParser {

  private val POST = "div[class=q post]"
  private val POINTS = "span[class=points]"
  private val ID = "a[class=qid click]"
  private val TEXT = "div[class=quote post-content post-body]"

  def parse(page: Document, entries: Int): List[QuoteModel] = {
    page.body().select(POST).parallelStream().iterator().asScala.take(entries).map { p =>
      val points = Option(p.selectFirst(POINTS).text().toLong).getOrElse(-1L)
      val id =  Option(p.selectFirst(ID).text().tail.toLong).getOrElse(-1L)
      val text = Option(p.selectFirst(TEXT).text()).getOrElse("")
      QuoteModel(id, points, text)
    }.toList
  }
}
