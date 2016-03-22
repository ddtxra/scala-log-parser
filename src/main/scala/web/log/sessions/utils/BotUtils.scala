package web.log.sessions.utils

import java.util.regex.Pattern

object BotUtils {

  val botRegex = "bot|spider|facebook|feed|crawler|baidu|bing|yahoo|googletoolbar";
  val botPattern = Pattern.compile(botRegex);

  def isAgentAnIndexingBot(agentString : String): Boolean ={
    return botPattern.matcher(agentString.toLowerCase()).find();
  }
}
