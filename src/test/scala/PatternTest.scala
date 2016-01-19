import ddt.log.analyser.utils.BotUtils

import org.scalatest._

class ExampleSpec extends FlatSpec with Matchers {

  "An indexing robot" should "be either googlebot, baiduspider, bing" in {

    BotUtils.isAgentAnIndexingBot("hello my bot friend") should be (false)

  }

}