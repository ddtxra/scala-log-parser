package web.log.sessions

import web.log.sessions.utils.BotUtils

import org.scalatest._

class BotTest extends FlatSpec with Matchers {

  "An indexing robot" should "be either googlebot, baiduspider, bing" in {

    BotUtils.isAgentAnIndexingBot("hello my bot friend") should be (false)

  }

}