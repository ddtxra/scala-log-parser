package web.log.sessions.utils

import java.util.regex.Pattern

/**
  * Defines whether a request is an html file or not (this is an approximation, the list is not exhaustive)
  */
object RequestUtils {

  val botRegex = "([^\\s]+(\\.(?i)(bmp|class|css|gif|ico|icon|jar|jpg|jpeg|js|json|otf|pl|png|rss|swf|tif|tiff|xml|json|rdf|zip|gzip|gz|diff|txt))$)";
  val botPattern = Pattern.compile(botRegex);

  def isPossiblyHtmlRequest(requestString : String): Boolean ={
    return !botPattern.matcher(requestString.toLowerCase()).find();
  }
}