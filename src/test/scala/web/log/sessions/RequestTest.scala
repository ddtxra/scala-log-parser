package web.log.sessions

import org.scalatest._
import web.log.sessions.utils.{RequestUtils, BotUtils}

class RequestTest extends FlatSpec with Matchers {

  "An indexing robot" should "find possible html requests" in {

    RequestUtils.isPossiblyHtmlRequest("requests/test") should be (true)
    RequestUtils.isPossiblyHtmlRequest("requests/test.html") should be (true)
    RequestUtils.isPossiblyHtmlRequest("requests/test.htm") should be (true)

    RequestUtils.isPossiblyHtmlRequest("entry/insulin.xml") should be (false)
    RequestUtils.isPossiblyHtmlRequest("entry/insulin.png") should be (false)
    RequestUtils.isPossiblyHtmlRequest("entry/insulin.rdf") should be (false)
    RequestUtils.isPossiblyHtmlRequest("entry/insulin.diff") should be (false)
    RequestUtils.isPossiblyHtmlRequest("entry/insulin.zip") should be (false)
    RequestUtils.isPossiblyHtmlRequest("entry/insulin.gzip") should be (false)
    RequestUtils.isPossiblyHtmlRequest("entry/insulin.png") should be (false)
    RequestUtils.isPossiblyHtmlRequest("entry/insulin.txt") should be (false)
    RequestUtils.isPossiblyHtmlRequest("entry/insulin.json") should be (false)
    RequestUtils.isPossiblyHtmlRequest("/favicon.ico") should be (false)


  }

}