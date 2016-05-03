package omar.superoverlay

import groovy.util.logging.Slf4j
import grails.core.GrailsApplication
import grails.util.Environment

@Slf4j
class OmarSuperOverlayUtils
{
   private static ConfigObject superOverlayConfig
   static GrailsApplication application

   /**
    * Parse and load the superOverlay configuration.
    * @return the configuration
    */
   static synchronized ConfigObject getSuperOverlayConfig() {
      if (superOverlayConfig == null) {
         log.trace 'Building superOverlay config since there is no cached config'
         reloadSuperOverlayConfig()
      }
      superOverlayConfig
   }


   /** Force a reload of the superOverlay  configuration. */
   static void reloadSuperOverlayConfig() {
      mergeConfig OmarSuperOverlayReflectionUtils.getSuperOverlayConfig(), 'DefaultOmarSuperOverlayConfig'

      mergeConfigToGlobalConfig()

      log.trace 'reloaded superOverlay config'
   }
   /** Reset the config for testing or after a dev mode Config.groovy change. */
   static synchronized void resetSuperOverlayConfig() {
      superOverlayConfig = null
      log.trace 'reset superOverlay config'
   }

   static void mergeConfigToGlobalConfig(){

      log.trace "mergeConfigToGlobalConfig(): Entered..............."

      // I might need to bridge some items to the quarts config variables
      // will leave these here for reference that I cut and past from our other
      // plugin
      //
      log.trace "mergeConfigToGlobalConfig(): Leaving..............."

   }
   /**
    * Merge in a secondary config (provided by a plugin as defaults) into the main config.
    * @param currentConfig the current configuration
    * @param className the name of the config class to load
    */
   private static void mergeConfig(ConfigObject currentConfig, String className) {
      ConfigObject secondary = new ConfigSlurper(Environment.current.name).parse(
              new GroovyClassLoader(this.classLoader).loadClass(className))

      superOverlayConfig = OmarSuperOverlayReflectionUtils.superOverlayConfig = mergeConfig(currentConfig, secondary.superOverlay as ConfigObject)
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
