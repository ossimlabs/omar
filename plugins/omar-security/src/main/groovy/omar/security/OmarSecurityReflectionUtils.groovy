package omar.security

import groovy.util.logging.Slf4j
import groovy.transform.CompileStatic
import grails.core.GrailsApplication
import grails.util.Holders
import org.springframework.core.env.PropertySource
import org.springframework.core.env.MapPropertySource

@Slf4j
class OmarSecurityReflectionUtils
{
   static GrailsApplication application

   private static GrailsApplication getApplication() {
      if (!application) {
         application = Holders.grailsApplication
      }
      application
   }
   static ConfigObject getSecurityConfig() {
      def config = getApplication().config

      config.omar.security
   }
   static void setSecurityConfig(ConfigObject c) {
      ConfigObject config = new ConfigObject()
      config.omar.security = c
   }

}