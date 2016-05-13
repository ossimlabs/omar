package omar.jpip

import grails.core.GrailsApplication
import grails.util.Environment
import groovy.util.logging.Slf4j

@Slf4j
class OmarJpipUtils
{
   private static ConfigObject jpipConfig
   static GrailsApplication application

   /**
    * Parse and load the jpip configuration.
    * @return the configuration
    */
   static synchronized ConfigObject getJpipConfig() {
      if (jpipConfig == null) {
         log.trace 'Building jpip config since there is no cached config'
         reloadJpipConfig()
      }
      jpipConfig
   }


   /** Force a reload of the jpip  configuration. */
   static void reloadJpipConfig() {
      mergeConfig OmarJpipReflectionUtils.getJpipConfig(), 'DefaultOmarJpipConfig'

      mergeConfigToGlobalConfig()

      log.trace 'reloaded jpip config'
   }
   /** Reset the config for testing or after a dev mode Config.groovy change. */
   static synchronized void resetJpipConfig() {
      jpipConfig = null
      log.trace 'reset jpip config'
   }

   static void mergeConfigToGlobalConfig(){

      log.trace "mergeConfigToGlobalConfig(): Entered..............."

      // I might need to bridge some items to the quarts config variables
      // will leave these here for reference that I cut and past from our other
      // plugin
      //
      log.trace "mergeConfigToGlobalConfig(): Leaving..............."

//      mergeConfig this.securityConfig.spring,  application?.config.grails.plugin.springsecurity
//      mergeConfig this.securityConfig.spring,  SpringSecurityUtils.securityConfig
   }
   /**
    * Merge in a secondary config (provided by a plugin as defaults) into the main config.
    * @param currentConfig the current configuration
    * @param className the name of the config class to load
    */
   private static void mergeConfig(ConfigObject currentConfig, String className) {
      ConfigObject secondary = new ConfigSlurper(Environment.current.name).parse(
              new GroovyClassLoader(this.classLoader).loadClass(className))

      jpipConfig = OmarJpipReflectionUtils.jpipConfig = mergeConfig(currentConfig, secondary.jpip as ConfigObject)
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

   private static def mergeConfig(java.util.Map currentConfig, def grailsConfig)
   {
      currentConfig.keySet().each{key->
         grailsConfig."${key}" = currentConfig."${key}"
      }
      grailsConfig
   }

}
