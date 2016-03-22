package web.log.sessions

import web.log.sessions.utils.BotUtils

import org.scalatest._

class BotTest extends FlatSpec with Matchers {

  "A robot" should "be detected" in {

    BotUtils.isAgentAnIndexingBot("hello my robot friend") should be (true)

  }

}