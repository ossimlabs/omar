import grails.util.BuildSettings
import grails.util.Environment

import ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy
import ch.qos.logback.core.rolling.FixedWindowRollingPolicy

logback = {
   appenders {
      rollingFile name: 'myAppender', file: '/tmp/rolling.log',
            encoder: pattern(pattern: '%-4relative [%thread] %-5level %logger{35} - %msg%n'),
            triggeringPolicy: new SizeBasedTriggeringPolicy(maxFileSize: 10*1024*1024),
            rollingPolicy: new FixedWindowRollingPolicy(fileNamePattern: '/tmp/rolling.%i.log.gz')
   }

   info myAppender: 'grails.app'
}


// See http://logback.qos.ch/manual/groovy.html for details on configuration
appender('STDOUT', ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "%level %logger - %msg%n"
    }
}

root(ERROR, ['STDOUT'])

def targetDir = BuildSettings.TARGET_DIR
if (Environment.isDevelopmentMode() && targetDir) {
    appender("FULL_STACKTRACE", FileAppender) {
        file = "${targetDir}/stacktrace.log"
        append = true
        encoder(PatternLayoutEncoder) {
            pattern = "%level %logger - %msg%n"
        }
    }
    logger("StackTrace", ERROR, ['FULL_STACKTRACE'], false)
}
