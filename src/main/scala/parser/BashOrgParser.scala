package parser

import domain.QuoteModel
import org.jsoup.nodes.Document

class BashOrgParser(url: String) {
  def parse(page: Document): List[QuoteModel] = {
    List.empty
  }
}
