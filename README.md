# Web Sessions computed using Web Server Logs 

(This project is under development)

The aim of this project is to compare Google Analytics / Piwik Sessions versus sessions that could be computed analysing web server logs.

Google Analytics states that (simpler version) : A session is a group of interactions that take place on your website within a given time frame. A single user can open multiple sessions. Those sessions can occur on the same day, or over several days, weeks, or months. 
As soon as one session ends, there is then an opportunity to start a new session. 

A sessions ends when:
* After 30 minutes of inactivity (this value can be changed, but by default it's 30minutes)
* At midnight

Source: https://support.google.com/analytics/answer/2731565?hl=en

Piwik has a similar definition for visit:
http://piwik.org/faq/general/faq_36/


# Implementation details ...

# Results

"Normal" HTML website
![alt text](assets/ga-vs-log-html.png "Normal HTML application")

"Angular" website (single page application with partials)
![alt text](assets/ga-vs-log-spa.png "Single Page Application")



# Consideration

Caches ...
