import java.time.format.DateTimeFormatter
import java.time.{Duration, LocalDateTime}
import java.util.concurrent.atomic.AtomicLong

import scala.collection.mutable.HashMap

/**
  * Created by dteixeira on 19/01/16.
  */
class SessionCollector {

  val sessionTimeMinutes = 30;
  val fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  val lc = new AtomicLong();
  val sessionsByDay = new HashMap[String, Long];
  val requestsByIp = new HashMap[String, LocalDateTime];

  def purgeIfNecessary(currentDate: LocalDateTime): Unit = {

    lc.incrementAndGet();

    //RULE 2
    if(lc.get() % 100000 == 0 ){
      println("doing purge");

      val ips = requestsByIp.keySet.map(k => {
        // check if others must be removed because it has passed 24hours
        if(Duration.between(requestsByIp.getOrElse(k, null), currentDate).toHours() > 24){k;} else null
      }).filter(_ != null);

      ips.foreach(ip => {
        val dt = requestsByIp.remove(ip).get;
        val dtf = fmt.format(dt);

        sessionsByDay.put(dtf, sessionsByDay.getOrElse(dtf, 0L) + 1L);

      })
    }
  }

  def add(rec: AccessLogRecord): Unit = {

    purgeIfNecessary(rec.dateTime);

    if(!rec.request.contains("resources/test")){
      if(!rec.userAgent.contains("googlebot|baiduspider|bing")){

        val lastSessionTime = requestsByIp.getOrElse(rec.clientIpAddress, rec.dateTime);

        //RULE 1
        val duration = Duration.between(lastSessionTime, rec.dateTime).toMinutes();
        if(duration > sessionTimeMinutes){
          val day = fmt.format(rec.dateTime);
          sessionsByDay.put(day, sessionsByDay.getOrElse(day, 0L) + 1L);
        }
        requestsByIp.put(rec.clientIpAddress, rec.dateTime);
      }
    }
  }

  def printSessions {
    sessionsByDay.keySet.toList.sortWith(_ < _).foreach(k => println(k + "," + String.valueOf(sessionsByDay.getOrElse(k, null))));
  }


}
