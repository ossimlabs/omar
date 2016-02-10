package omar.security
import groovy.util.logging.Slf4j
import groovy.transform.CompileStatic
import grails.core.GrailsApplication
import grails.util.Environment

@Slf4j
@CompileStatic
class OmarSecurityUtils
{
   private static ConfigObject securityConfig
   static GrailsApplication application

   /**
    * Parse and load the security configuration.
    * @return the configuration
    */
   static synchronized ConfigObject getSecurityConfig() {
      if (securityConfig == null) {
         log.trace 'Building security config since there is no cached config'
         reloadSecurityConfig()
      }

      securityConfig
   }
   /** Force a reload of the security configuration. */
   static void reloadSecurityConfig() {
      mergeConfig OmarSecurityReflectionUtils.getSecurityConfig(), 'DefaultOmarSecurityConfig'
      log.trace 'reloaded security config'
   }
   /** Reset the config for testing or after a dev mode Config.groovy change. */
   static synchronized void resetSecurityConfig() {
      securityConfig = null
      log.trace 'reset security config'
   }
   /**
    * Merge in a secondary config (provided by a plugin as defaults) into the main config.
    * @param currentConfig the current configuration
    * @param className the name of the config class to load
    */
   private static void mergeConfig(ConfigObject currentConfig, String className) {
      ConfigObject secondary = new ConfigSlurper(Environment.current.name).parse(
              new GroovyClassLoader(this.classLoader).loadClass(className))
      securityConfig = OmarSecurityReflectionUtils.securityConfig = mergeConfig(currentConfig, secondary.security as ConfigObject)
   }

   /**
    * Merge two configs together. The order is important if <code>secondary</code> is not null then
    * start with that and merge the main config on top of that. This lets the <code>secondary</code>
    * config act as default values but let user-supplied values in the main config override them.
    *
    * @param currentConfig the main config, starting from Config.groovy
    * @param secondary new default values
    * @return the merged configs
    */
   private static ConfigObject mergeConfig(ConfigObject currentConfig, ConfigObject secondary) {
      (secondary ?: new ConfigObject()).merge(currentConfig ?: new ConfigObject()) as ConfigObject
   }

}
