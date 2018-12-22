import com.typesafe.config.ConfigFactory
import crawler.Crawler
import parser.BashOrgParser
import writer.JsonWriter

import scala.util.{Failure, Success, Try}

object CrawlerApp extends App {

  private val config = ConfigFactory.load("config.conf")
  private val url = config.getString("url")
  private val outputFile = config.getString("outputFile")

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

  val pages = Crawler.getPages(url, numEntries)
  val entries = pages.flatMap(BashOrgParser.parse).take(numEntries)
  JsonWriter.save(entries, outputFile)
}
