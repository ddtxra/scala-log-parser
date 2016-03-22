package web.log.sessions.utils

import java.time.LocalDateTime

case class AccessLogRecord (clientIpAddress: String,         // should be an ip address, but may also be the hostname if hostname-lookups are enabled
                            rfc1413ClientIdentity: String,   // typically `-`
                            remoteUser: String,              // typically `-`
                            dateTime: LocalDateTime,                // [day/month/year:hour:minute:second zone]
                            verb: String,                 // `GET /foo ...`
                            request: String,                 // `GET /foo ...`
                            httpStatusCode: String,          // 200, 404, etc.
                            bytesSent: String,               // may be `-`
                            referer: String,                 // where the visitor came from
                            userAgent: String                // long string to represent the browser and OS
                           )
