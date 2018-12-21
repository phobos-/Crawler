package writer

import java.io.{BufferedWriter, File, FileWriter}
import net.liftweb.json._
import net.liftweb.json.Serialization.write
import domain.QuoteModel

object JsonWriter {
  def save(entries: Iterable[QuoteModel], path: String): Unit = {
    implicit val formats: DefaultFormats.type = DefaultFormats
    val bw = new BufferedWriter(new FileWriter(new File(path)))
    bw.write(entries.map(p => write(p)).mkString)
    bw.close()
  }
}
