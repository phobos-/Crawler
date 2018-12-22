package writer

import java.io.{ BufferedWriter, File, FileWriter }
import net.liftweb.json._
import net.liftweb.json.JsonAST._
import net.liftweb.json.Extraction._
import domain.Entry

object JsonWriter {
  def save(entries: Iterable[Entry], path: String): Unit = {
    val bw = new BufferedWriter(new FileWriter(new File(path)))
    bw.write(entries.map(p => render(decompose(p)(DefaultFormats), RenderSettings.pretty)).mkString("\n"))
    bw.close()
  }
}
