import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import fetcher.Fetcher
import org.jsoup.nodes.Document
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, Matchers}

class FetcherTest
    extends TestKit(ActorSystem("MySpec"))
    with ImplicitSender
    with FlatSpecLike
    with Matchers
    with BeforeAndAfterAll {

  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "fetcher" should "create proper page" in {
    val examplePage = 4
    Fetcher
      .createPageUrl("http://something.pl/", examplePage)
      .toString should equal(s"http://something.pl/?page=$examplePage")
    Fetcher
      .createPageUrl("http://something.pl", examplePage)
      .toString should equal(s"http://something.pl/?page=$examplePage")
  }

  it should "fetch page from a given url" in {
    val url = "http://bash.org.pl"
    val fetcherActor = system.actorOf(Fetcher.props)

    fetcherActor ! url
    val page = expectMsgType[Document]
    page.location() should equal(url)
  }
}
