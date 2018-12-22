import java.io.File

import domain.Entry
import org.scalatest.{ FlatSpec, Matchers }
import writer.JsonWriter

import scala.io.Source

class JsonWriterTest extends FlatSpec with Matchers {
  "parser" should "create proper output" in {
    val path = "./out.txt"
    JsonWriter.save(List(Entry(1, 2, "test")), "./out.txt")

    val file = new File("./out.txt")
    file.exists() should be(true)
    Source.fromFile(file).getLines.mkString.contains("\"content\":\"test\"") should be(true)
    file.deleteOnExit()
  }
}
