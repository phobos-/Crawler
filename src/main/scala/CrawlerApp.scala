import com.typesafe.config.ConfigFactory
import crawler.Crawler
import parser.BashOrgParser
import writer.JsonWriter

object CrawlerApp extends App {

  private val config = ConfigFactory.load("config.conf")
  private val url = config.getString("url")
  private val outputFile = config.getString("outputFile")

  private val numEntries = args.headOption.getOrElse {
    print("Usage: crawler x\nWhere x is the amount of entries to be fetched\n")
    System.exit(1)
    ""
  }.toInt

  val pages = Crawler.getPages(url, numEntries)
  val entries = pages.flatMap(BashOrgParser.parse).take(numEntries)
  JsonWriter.save(entries, outputFile)
}
