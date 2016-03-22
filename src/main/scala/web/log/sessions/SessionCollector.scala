package web.log.sessions

import java.time.format.DateTimeFormatter
import java.time.{Duration, LocalDateTime}
import java.util.concurrent.atomic.AtomicLong

import web.log.sessions.utils.{RequestUtils, BotUtils, AccessLogRecord}

import scala.collection.immutable.ListMap
import scala.collection.mutable.HashMap

class SessionCollector (sessionTimeout : Integer, filterBots : Boolean, computeTopAgents: Boolean, computeTopRequests: Boolean, computeTopIps: Boolean) {

  val fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  val lc = new AtomicLong();

  val topAgents = new HashMap[String, Long];
  val topRequests = new HashMap[String, Long];
  val topIps = new HashMap[String, Long];

  val sessionsByDay = new HashMap[String, Long];
  val clientLastRequestTime = new HashMap[(String, String), LocalDateTime];


  private def addSession(client: Tuple2[String, String], time : LocalDateTime): Unit = {
    val lastRequestDayFormatted = fmt.format(time);
    sessionsByDay.put(lastRequestDayFormatted, sessionsByDay.getOrElse(lastRequestDayFormatted, 0L) + 1L);

    if(computeTopAgents) {
      topAgents.put(client._2, topAgents.getOrElse(client._2, 0L) + 1L)
    };

  }

  private def purgeIfNecessary(currentDate: LocalDateTime): Unit = {

    lc.incrementAndGet();

    if(lc.get() % 100000 == 0 ){ // Purge every 10'000 times

      val clients = clientLastRequestTime.keySet.map(k => {
        if(Duration.between(clientLastRequestTime.getOrElse(k, null), currentDate).toHours() > 24){k;} else null
      }).filter(_ != null);

      clients.foreach(client => {
        addSession(client, clientLastRequestTime.remove(client).get);
        clientLastRequestTime.remove(client);
      })
    }
  }

  def add(rec: AccessLogRecord): Unit = {

    val currentRequestTime = rec.dateTime;

    //if(!rec.request.contains("resources/test")){
      if((!filterBots || !BotUtils.isAgentAnIndexingBot(rec.userAgent))
      /*&& RequestUtils.isPossiblyHtmlRequest(rec.request) */
        /*&& (rec.verb != "HEAD" && rec.verb != "NULL" && rec.verb != "OPTIONS")*/){

        if(computeTopRequests){
          topRequests.put(rec.request, topRequests.getOrElse(rec.request, 0L) + 1L);
        }
        if(computeTopIps){
          topIps.put(rec.clientIpAddress, topIps.getOrElse(rec.clientIpAddress, 0L) + 1L);
        }


        // check if some requests have been done more than 24hours ago
        purgeIfNecessary(currentRequestTime);

        //The client is a combination of ip address with user agent
        val client = (rec.clientIpAddress, rec.userAgent);
        val lastSessionTime = clientLastRequestTime.getOrElse(client, currentRequestTime);

        //RULE 1
        if(Duration.between(lastSessionTime, currentRequestTime).toMinutes() > sessionTimeout){
          addSession(client, lastSessionTime);
        }

        clientLastRequestTime.put(client, currentRequestTime);
      }
  }

  def printSessions {
    purgeIfNecessary(LocalDateTime.of(2100, 1, 1, 0, 0)); // 1st January 2100 to force the purge to be done
    sessionsByDay.keySet.toList.sortWith(_ < _).foreach(k => println(k + "," + String.valueOf(sessionsByDay.getOrElse(k, null))));

    if(computeTopAgents){
      println("Top 10 Agents (who generated sessions)");
      ListMap(topAgents.toSeq.sortWith(_._2 > _._2):_*).take(10).foreach(k => println(k + " " + String.valueOf(k)));
    }

    if(computeTopRequests){
      println("Top 10 Requests");
      ListMap(topRequests.toSeq.sortWith(_._2 > _._2):_*).take(10).foreach(k => println(k + " " + String.valueOf(k)));
    }

    if(computeTopIps){
      println("Top 10 Ips");
      ListMap(topIps.toSeq.sortWith(_._2 > _._2):_*).take(10).foreach(k => println(k + " " + String.valueOf(k)));
    }

  }

}
