package ddt.log.analyser

import java.io.File

import ddt.log.analyser.utils.AccessLogParser

import scala.io.Source

/**
  * Created by dteixeira on 18/01/16.
  */
object LogAnalyserApp extends App{


  val sessionCollector = new SessionCollector();
  val parser = new AccessLogParser();

  val files = filesAt(new File("../expasy-logs/"));

  val filesToSelect = files.filter(f => "^access_log-2015\\d{4}$".r.findFirstIn(f.getName).isDefined).toList.sortWith(_.getName < _.getName);

  for(file <- filesToSelect){
    println(file.getName);
    for(line <- Source.fromFile(file).getLines()){
      sessionCollector.add(parser.parseRecord(line).get)
    }
  }

  sessionCollector.printSessions;


  private def filesAt(f: File): Array[File] = if (f.isDirectory) f.listFiles flatMap filesAt else Array(f)

}
