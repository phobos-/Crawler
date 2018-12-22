import crawler.Crawler
import timing.Timer

import scala.util.{ Failure, Success, Try }

object CrawlerApp extends App with Timer {

  private val numEntries: Int = Try(args.head.toInt) match {
    case Success(x) =>
      if (x > 0) {
        x
      } else {
        throw new IllegalArgumentException("Number of entries must be positive")
      }
    case Failure(exception) =>
      print("Usage: crawler x\nWhere x is the amount of entries to be fetched\n")
      throw exception
  }

  print("Total time spent: " + formatTime(profile(Crawler.crawlBashOrg(numEntries)).time))
}
