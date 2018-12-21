package crawler

import java.net.URL

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

object Crawler {
  private val pageUrl = "?page=%d"
  private val entriesPerPage = 20

  def createPageUrl(url: String, page: Int): URL =
    if (url.endsWith("/")) new URL(url + pageUrl.format(page)) else new URL(s"$url/${pageUrl.format(page)}")

  def getPages(url: String, entries: Int): Iterable[Document] = {
    val pagesToFetch = Math.ceil(entries / entriesPerPage).toInt
    (0 to pagesToFetch).map(p => Jsoup.connect(createPageUrl(url, p + 1).toString).get())
  }
}
