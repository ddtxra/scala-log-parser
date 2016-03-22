package web.log.sessions

import java.time.format.DateTimeFormatter
import java.time.{Duration, LocalDateTime}
import java.util.concurrent.atomic.AtomicLong

import web.log.sessions.utils.{AccessLogRecord, BotUtils, RequestUtils}

import scala.collection.immutable.ListMap
import scala.collection.mutable.HashMap

class UsersCollector {

  val fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  val lc = new AtomicLong();

  //val topAgents = new HashMap[String, Long];
  //val topRequests = new HashMap[String, Long];
  //val topIps = new HashMap[String, Long];

  val usersCountPerDay = new HashMap[String, Long];
  val usersLastRequest = new HashMap[(String, String), LocalDateTime];


  private def addUser(client: Tuple2[String, String], time : LocalDateTime): Unit = {
    val dayTime = fmt.format(time);
    usersCountPerDay.put(dayTime, usersCountPerDay.getOrElse(dayTime, 0L) + 1L);
  }

  //For requests that have been here for a while
  private def purgeIfNecessary(currentDate: LocalDateTime, force: Boolean): Unit = {

    lc.incrementAndGet();

    if(lc.get() % 10000 == 0 || force){ // Purge every 10'000 rows

      val clients = usersLastRequest.keySet.map(k => {
        if(Duration.between(usersLastRequest.getOrElse(k, null), currentDate).toHours() > 24){k;} else null
      }).filter(_ != null);

      clients.foreach(client => {
        addUser(client, usersLastRequest.remove(client).get);
        usersLastRequest.remove(client);
      })
    }
  }

  def sameDay(t1: LocalDateTime, t2: LocalDateTime): Boolean ={
    return t1.getYear.equals(t2.getYear) && t1.getDayOfYear.equals(t2.getDayOfYear);
  }

  def add(rec: AccessLogRecord): Unit = {

    val currentRequestTime = rec.dateTime;

    /*if(!rec.request.contains("resources/test")){
      if(!BotUtils.isAgentAnIndexingBot(rec.userAgent) && RequestUtils.isPossiblyHtmlRequest(rec.request) && (rec.verb != "HEAD" && rec.verb != "NULL" && rec.verb != "OPTIONS")){*/

        // check if some requests have been done more than 24hours ago
        purgeIfNecessary(currentRequestTime, false);

        //The client is a combination of ip address ? with user agent ?
        val client = (rec.clientIpAddress, null);
        val lastSessionTime = usersLastRequest.getOrElse(client, currentRequestTime);

        //RULE 1
        //If not the same day, then add a new user
        if(!sameDay(lastSessionTime, currentRequestTime)){
          addUser(client, lastSessionTime);
        }

        usersLastRequest.put(client, currentRequestTime);
      }
    /*}
  }*/

  def printUsers {
    purgeIfNecessary(LocalDateTime.of(2100, 1, 1, 0, 0), true); // 1st January 2100 to force the purge to be done
    usersCountPerDay.keySet.toList.sortWith(_ < _).foreach(k => println(String.valueOf(usersCountPerDay.getOrElse(k, null))));
  }

}
