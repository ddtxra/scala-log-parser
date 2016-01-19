import java.io.File
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalUnit
import java.time.{Duration, Period, LocalDate, LocalDateTime}

import scala.collection.mutable.HashMap
import scala.io.Source

/**
  * Created by dteixeira on 18/01/16.



GA
2015-12-07,11'864
2015-12-08,12'666
2015-12-09,12'677
2015-12-10,12'849
2015-12-11,10'465
2015-12-12,3'694


RULE 1 (30 minutes)
2015-12-07,5799
2015-12-08,6714
2015-12-09,7365
2015-12-10,7992
2015-12-11,8238
2015-12-12,6198


RULE 2 (with 24 hours constraint)
2015-12-07,13444
2015-12-08,13783
2015-12-09,13707
2015-12-10,14174
2015-12-11,9167
2015-12-12,5175

RULE 3 ( removing monitoring tool, doesn't affect very much =) )
2015-12-07,13438
2015-12-08,7896
2015-12-09,6658
2015-12-10,7243
2015-12-11,7390
2015-12-12,5818

RULE 4 (bots)
2015-12-07,11948
2015-12-08,12285
2015-12-09,12201
2015-12-10,12258
2015-12-11,7115
2015-12-12,3332

*/
object Sessionizer extends App{

  //val pw = new java.io.PrintWriter(new File("sessions.csv"))
  val sessionTimeMinutes = 30;
  val fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  val sessionMap = new HashMap[String, Long];
  val requestsPerIpMap = new HashMap[String, LocalDateTime];
  val parser = new AccessLogParser();

  var lc = 0L;

  for(line <- Source.fromFile("../expasy-logs/access_log-20151213").getLines()){

    //RULE 3
      lc = lc + 1;
      val rec = parser.parseRecord(line);

      if(!rec.get.request.contains("resources/test")){
        if(!rec.get.userAgent.contains("googlebot") && !line.toLowerCase().contains("baiduspider") && !line.toLowerCase().contains("bing")){

        val lastSessionTime = requestsPerIpMap.getOrElse(rec.get.clientIpAddress, rec.get.dateTime);

        //RULE 1
        val duration = Duration.between(lastSessionTime, rec.get.dateTime).toMinutes();
        if(duration > sessionTimeMinutes){
          val day = fmt.format(rec.get.dateTime);
          sessionMap.put(day, sessionMap.getOrElse(day, 0L) + 1L);


          //RULE 2
          if(lc % 10000 == 0 ){
            println("check for purge")
            // check if others must be removed because it has passed 24hours
            val ips = requestsPerIpMap.keySet.map(k => {
              val hours = Duration.between(requestsPerIpMap.getOrElse(k, null), rec.get.dateTime).toHours();
              if(hours > 24){
                k;
              } else null
            }).filter(_ != null);

            ips.foreach(ip => {
              val dt = requestsPerIpMap.remove(ip).get;
              val dtf = fmt.format(dt);

              sessionMap.put(dtf, sessionMap.getOrElse(dtf, 0L) + 1L);

            })
          }


        }
        requestsPerIpMap.put(rec.get.clientIpAddress, rec.get.dateTime);
      }


    }

  }

  sessionMap.keySet.toList.sortWith(_ < _).foreach(k => println(k + "," + String.valueOf(sessionMap.getOrElse(k, null))));


  def purgeRequestsToSessions(): Unit ={

  }

}
