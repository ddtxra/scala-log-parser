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

  val collector = new SessionCollector();
  //val collector = new UsersCollector();

  val parser = new AccessLogParser();

  val files = filesAt(new File("../uniprot-logs-uncompressed/"));
  val filesToSelect = files.filter(f => "^access_log-15121\\d{7}$".r.findFirstIn(f.getName).isDefined).toList.sortWith(_.getName < _.getName);

//  val files = filesAt(new File("../search-logs/"));
//  val filesToSelect = files.filter(f => "^ssl_access_log-2015\\d{4}$".r.findFirstIn(f.getName).isDefined).toList.sortWith(_.getName < _.getName);

  var l = "";
    for(file <- filesToSelect){
      println(file.getName);
      for(line <- Source.fromFile(file).getLines()){
        collector.add(parser.parseRecord(line).get)
      }
    }


  collector.printSessions;
  //collector.printUsers;


  private def filesAt(f: File): Array[File] = if (f.isDirectory) f.listFiles flatMap filesAt else Array(f)

}
