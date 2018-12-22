import org.jsoup.Jsoup
import org.scalatest.{ FlatSpec, Matchers }
import parser.BashOrgParser

class BashOrgParserTest extends FlatSpec with Matchers {

  val id     = 123
  val points = 666
  val text   = "test"
  val pageBody =
    s"""
      |<!doctype html>
      |<html lang="pl">
      |<body>
      |<div id="d$id" class="q post">
      |<a class="qid click" href="/$id/">#$id</a>
      |<span class=" points">$points</span>
      |<div class="quote post-content post-body">
      |$text
      |</div>
      |</div>
      |<div id="d$id" class="q post">
      |</div>
      |</body>
    """.stripMargin

  "parser" should "parse example page" in {
    val entries = 2
    val page    = Jsoup.parse(pageBody)
    val result  = BashOrgParser.parse(page, entries)
    result.size should equal(entries)
    val entry = result.head.result
    entry.id should equal(id)
    entry.points should equal(points)
    entry.content should equal(text)
  }

}
