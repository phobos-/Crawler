package writer

import java.io.{BufferedWriter, File, FileWriter}
import net.liftweb.json._
import net.liftweb.json.JsonAST._
import net.liftweb.json.Extraction._
import domain.QuoteModel

object JsonWriter {
  def save(entries: Iterable[QuoteModel], path: String): Unit = {
    implicit val formats: DefaultFormats.type = DefaultFormats
    val bw = new BufferedWriter(new FileWriter(new File(path)))
    bw.write(
      entries
        .map(p => render(decompose(p), RenderSettings.pretty))
        .mkString("\n"))
    bw.close()
  }
}
