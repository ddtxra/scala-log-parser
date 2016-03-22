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

  val filterBots = getSysPropOrElse("filterBos", "false").toBoolean;
  val sessionTimeoutInMinutes = getSysPropOrElse("sessionTimeoutInMinutes", "30").toInt; // The session in minute
  val logsFolder = getSysPropOrElse("logsFolder", "../uniprot-logs-uncompressed/");
  val filesRegex = getSysPropOrElse("filesRegex", "^access_log-15121\\d{7}$");

  val computeTopAgents = getSysPropOrElse("computeTopAgents", "false").toBoolean;
  val computeTopRequests = getSysPropOrElse("computeTopRequests", "false").toBoolean
  val computeTopIps = getSysPropOrElse("computeTopIps", "false").toBoolean

  val collector = new SessionCollector(sessionTimeoutInMinutes, filterBots, computeTopAgents, computeTopRequests, computeTopIps);

  val parser = new AccessLogParser();

  val files = filesAt(new File(logsFolder));
  val filesToSelect = files.filter(f => filesRegex.r.findFirstIn(f.getName).isDefined).toList.sortWith(_.getName < _.getName);

  println("Session properties: \n" +
    "\tlogsFolder=" + logsFolder  + "\n" +
    "\tfilesRegex=" + filesRegex  + "\n" +
    "\tsessionTimeoutInMinutes=" + sessionTimeoutInMinutes + "\n" +
    "\tfilterBots=" + filterBots  + "\n" +
    "\tcomputeTopAgents=" + computeTopAgents + "\n" +
    "\tcomputeTopRequests=" + computeTopRequests + "\n" +
    "\tcomputeTopIps=" + computeTopIps + "\n");

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

  private def getSysPropOrElse(s: String, elseValue: String) : String = if(System.getProperty(s) != null)  System.getProperty(s) else elseValue;


}
