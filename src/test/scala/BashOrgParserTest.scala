import akka.actor.ActorSystem
import akka.testkit.{ ImplicitSender, TestKit }
import domain.{ Entry, Page }
import org.jsoup.Jsoup
import org.scalatest.{ BeforeAndAfterAll, FlatSpecLike, Matchers }
import parser.BashOrgParser
import timing.TimedResult

class BashOrgParserTest
    extends TestKit(ActorSystem())
    with ImplicitSender
    with FlatSpecLike
    with Matchers
    with BeforeAndAfterAll {

  override def afterAll: Unit =
    TestKit.shutdownActorSystem(system)

  val id     = 123
  val points = 666
  val text   = "test"
  val pageBody =
    s"""
      |<!doctype html>
      |<html lang="pl">
      |<body>
      |<div id="d$id" class="q post">
      |<a class="qid click" href="/$id/">#$id</a>
      |<span class=" points">$points</span>
      |<div class="quote post-content post-body">
      |$text
      |</div>
      |</div>
      |<div id="d$id" class="q post">
      |</div>
      |</body>
    """.stripMargin
  val entries = 2
  val page    = Page(Jsoup.parse(pageBody), entries)

  "parser" should "parse example page" in {
    val result = BashOrgParser.parse(page)
    result.size should equal(entries)
    val entry = result.head.result
    entry.id should equal(id)
    entry.points should equal(points)
    entry.content should equal(text)
  }

  it should "pass result back to sender" in {
    val parserActor = system.actorOf(BashOrgParser.props)
    parserActor ! page
    val entries = expectMsgType[List[TimedResult[Entry]]]
    val entry   = entries.head.result
    entry.id should equal(id)
    entry.points should equal(points)
    entry.content should equal(text)
  }
}
