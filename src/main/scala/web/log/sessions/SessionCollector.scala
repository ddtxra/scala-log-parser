package web.log.sessions

import java.time.format.DateTimeFormatter
import java.time.{Duration, LocalDateTime}
import java.util.concurrent.atomic.AtomicLong

import web.log.sessions.utils.AccessLogRecord

import scala.collection.mutable.HashMap

class SessionCollector {

  val sessionTimeMinutes = 30;
  val fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  val lc = new AtomicLong();
  val sessionsByDay = new HashMap[String, Long];
  val clientLastRequestTime = new HashMap[String, LocalDateTime];


  private def addSession(time : LocalDateTime): Unit = {
    val lastRequestDayFormatted = fmt.format(time);
    sessionsByDay.put(lastRequestDayFormatted, sessionsByDay.getOrElse(lastRequestDayFormatted, 0L) + 1L);
  }

  private def purgeIfNecessary(currentDate: LocalDateTime): Unit = {

    lc.incrementAndGet();

    if(lc.get() % 100000 == 0 ){ // Purge every 10'000 times

      val clients = clientLastRequestTime.keySet.map(k => {
        if(Duration.between(clientLastRequestTime.getOrElse(k, null), currentDate).toHours() > 24){k;} else null
      }).filter(_ != null);

      clients.foreach(client => {
        addSession(clientLastRequestTime.remove(client).get);
        clientLastRequestTime.remove(client);
      })
    }
  }

  def add(rec: AccessLogRecord): Unit = {

    val currentRequestTime = rec.dateTime;

    // check if some requests have been done more than 24hours ago
    purgeIfNecessary(currentRequestTime);

    //if(!rec.request.contains("resources/test")){
      //if(!BotUtils.isAgentAnIndexingBot(rec.userAgent)){

        //The client is a combination of ip address ? with user agent ?
        val client = rec.clientIpAddress;
        val lastSessionTime = clientLastRequestTime.getOrElse(client, currentRequestTime);

        //RULE 1
        if(Duration.between(lastSessionTime, currentRequestTime).toMinutes() > sessionTimeMinutes){
          addSession(currentRequestTime);
        }

        clientLastRequestTime.put(client, currentRequestTime);
    //  } else {
        //println("Agent" + rec.userAgent)
      //}
    //}else {
      //println("Request" + rec.request)
    //}
  }

  def printSessions {
    purgeIfNecessary(LocalDateTime.of(2100, 1, 1, 0, 0)); // 1st January 2100 to force the purge to be done
    sessionsByDay.keySet.toList.sortWith(_ < _).foreach(k => println(String.valueOf(sessionsByDay.getOrElse(k, null))));
  }

}
