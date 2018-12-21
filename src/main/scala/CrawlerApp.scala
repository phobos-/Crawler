import com.typesafe.config.ConfigFactory

object CrawlerApp extends App {

  private val config = ConfigFactory.load("config.conf")
  private val url = config.getString("url")
  private val outputFile = config.getString("outputFile")

  println(url)
  println(outputFile)

}