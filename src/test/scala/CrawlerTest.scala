import org.scalatest.{ FlatSpec, Matchers }

class CrawlerTest extends FlatSpec with Matchers {

  private val url = "http://bash.org.pl/latest/"

  "Crawler" should "download given page content" in {
    val header = "bash.org.pl"
    val page = io.Source.fromURL(url).mkString
    page should not be empty
    page.contains(header) should be (true)
  }
}