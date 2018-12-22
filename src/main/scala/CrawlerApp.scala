import crawler.Crawler

import scala.util.{Failure, Success, Try}

object CrawlerApp extends App {

  private val numEntries: Int = Try(args.head.toInt) match {
    case Success(x) =>
      if (x > 0) {
        x
      } else {
        throw new IllegalArgumentException("Number of entries must be positive")
      }
    case Failure(exception) =>
      print(
        "Usage: crawler x\nWhere x is the amount of entries to be fetched\n")
      throw exception
  }

  Crawler.saveQuotes(numEntries)
}
