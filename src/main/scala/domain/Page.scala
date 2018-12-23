package domain
import org.jsoup.nodes.Element

final case class Page(element: Element, numEntries: Int)
