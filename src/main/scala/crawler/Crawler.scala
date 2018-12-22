package crawler

import com.typesafe.config.ConfigFactory

object Crawler {

  private val config = ConfigFactory.load("config.conf")
  private val url = config.getString("url")
  private val outputFile = config.getString("outputFile")

  def saveQuotes(numEntries: Int): Unit = {}

}
