# Web Sessions computed using Web Server Logs 

(This project is under development)

The aim of this project is to compare Google Analytics Sessions or Piwik Visits with sessions that could be computed analysing web server logs (like Apache logs).

From Google Analytics definition: A session is a group of interactions that take place on your website within a given time frame. A single user can open multiple sessions. By default on GA, a sessions ends when:
* After 30 minutes of inactivity (this value can be changed, but by default it's 30minutes)
* At midnight

Source: https://support.google.com/analytics/answer/2731565?hl=en (There are other parameters related to campaigns, but we don't take this into account)

Piwik has a similar definition for visit: http://piwik.org/faq/general/faq_36/

# Implementation details ...

# Results

"Normal" HTML website
![alt text](assets/ga-vs-log-html.png "Normal HTML application")

"Angular" website (single page application with partials)
![alt text](assets/ga-vs-log-spa.png "Single Page Application")


## Sessions

Measuring sessions (aka Piwik visits) has some advantages compared to hits when we want to measure **usage statistics**:

* It is **not dependent on the technology or the design** used to build the website. For example a SPA (Single Page Application) in AngularJS may have many partials (html files) to build one single page. It can potentially generate plenty of hits on the server side, but will generate only one session (which is more correct in term of usage statistics). On the other side, if a site is build with basic 1 basic HTML file, only one hit will be generated on the server side. Those websites have the same "usage", but the hits can differ a lot, while the number of sessions are the same.

* Other **dependent resources** used to build the pages like images, css/js files, ... won't biase the number of sessions, because they all are part of the same session. But they will surely biase hits, and it is sometimes difficult to agree what is a dependant resource and what is not (only looking at the logs).

* It increases if **unique IPs** increases. In term of usage statistics it's better to have 2 users acccessing the same resource, rather than 1 user accessing 2 pages. The number of hits won't reflect this information.

* It is much less affected by **monitoring tools** / **crawlers** or even **scripting / programmatic access**. For example if a monitoring tool, access the website every minute, the number of hits will increase while the session will only be 1 at the end of the day.

* It's less affected by the **cache** configuration. If a cache (based on expiration time) of 5minutes is defined and session is set to 30minutes sliding time window, then the number of sessions will be the same. While the number of hits can decrease a lot.

* It's in not very much affected by a **small downtime**  (less than 30 min) of the service, while the number of hits can be.

* It is not dependent on the *number of urls* or *interactivity* the resource contains. For example if a resource contains 10 entries and for each entry creates 10 sections (urls) to be more interactive, while another resource present these same entries in just 10 different pages, the number of sessions may be the same while the number of hits may differ a lot. 

As well as hits, it captures other data access like txt, xml, json data access (that are not caugh by Google Analytics)

# Consideration

Same config in GA
Caches (less than 30 min)...
