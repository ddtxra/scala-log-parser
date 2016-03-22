package web.log.sessions

import java.io.File
import java.nio.charset.CodingErrorAction

import web.log.sessions.utils.AccessLogParser

import scala.io.{Codec, Source}

/**
  * Created by dteixeira on 18/01/16.
  */
object LogAnalyserApp extends App{

  implicit val codec = Codec("UTF-8")
  codec.onMalformedInput(CodingErrorAction.REPLACE)
  codec.onUnmappableCharacter(CodingErrorAction.REPLACE)

  val filterBots = getSysPropOrElse("filterBots", "false").toBoolean;
  val sessionTimeoutInMinutes = getSysPropOrElse("sessionTimeoutInMinutes", "30").toInt; // The sessions timeout in minute
  val logFilesFolder = getSysPropOrElse("logFilesFolder", ".");
  val logFilesRegex = getSysPropOrElse("logFilesRegex", "^access_log*");

  val computeTopAgents = getSysPropOrElse("computeTopAgents", "false").toBoolean;
  val computeTopRequests = getSysPropOrElse("computeTopRequests", "false").toBoolean
  val computeTopIps = getSysPropOrElse("computeTopIps", "false").toBoolean

  val collector = new SessionCollector(sessionTimeoutInMinutes, filterBots, computeTopAgents, computeTopRequests, computeTopIps);

  val parser = new AccessLogParser();

  val files = filesAt(new File(logFilesFolder));
  val filesToSelect = files.filter(f => logFilesRegex.r.findFirstIn(f.getName).isDefined).toList.sortWith(_.getName < _.getName);

  println("## Web Log sessions ############################### \n");

  println("Properties:\n" +
  "\t-DlogFilesFolder=" + logFilesFolder  + "\n" +
  "\t-DlogFilesRegex=" + logFilesRegex  + "\n" +
  "\t-DsessionTimeoutInMinutes=" + sessionTimeoutInMinutes + "\n" +
  "\t-DfilterBots=" + filterBots  + "\n" +

  "\t-DcomputeTopAgents=" + computeTopAgents + "\n" +
  "\t-DcomputeTopRequests=" + computeTopRequests + "\n" +
  "\t-DcomputeTopIps=" + computeTopIps + "\n");

  println("Found " + filesToSelect.size + " files");

  for(file <- filesToSelect){
    println("Processing " + file.getName + " ...");
    for(line <- Source.fromFile(file).getLines()){
      collector.add(parser.parseRecord(line).get)
    }
  }

  println("Results:")
  println("")

  collector.printSessions;

  private def filesAt(f: File): Array[File] = if (f.isDirectory) f.listFiles flatMap filesAt else Array(f)

  private def getSysPropOrElse(key: String, elseValue: String) : String = if(System.getProperty(key) != null)  System.getProperty(key) else elseValue;


}
