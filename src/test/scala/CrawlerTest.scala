import crawler.Crawler
import org.scalatest.{FlatSpec, Matchers}

class CrawlerTest extends FlatSpec with Matchers {

  "crawler" should "create proper page" in {
    val examplePage = 4
    Crawler
      .createPageUrl("http://something.pl/", examplePage)
      .toString should equal(s"http://something.pl/?page=$examplePage")
    Crawler
      .createPageUrl("http://something.pl", examplePage)
      .toString should equal(s"http://something.pl/?page=$examplePage")
  }

  it should "fetch proper number of pages from url for x entries given" in {
    val entries = (4, 21, 55)
    val url = "http://bash.org.pl"
    Crawler.getPages(url, entries._1).size should equal(1)
    Crawler.getPages(url, entries._2).size should equal(2)
    Crawler.getPages(url, entries._3).size should equal(3)
    Crawler.getPages(url, entries._1).head.location() should equal(
      s"$url/?page=1")
  }
}
