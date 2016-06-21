package omar.avro

import groovy.util.logging.Slf4j
import grails.core.GrailsApplication
import grails.util.Environment

@Slf4j
class OmarAvroUtils
{
   private static ConfigObject avroConfig
   static GrailsApplication application

   /**
    * Parse and load the avro configuration.
    * @return the configuration
    */
   static synchronized ConfigObject getAvroConfig() {
      if (avroConfig == null) {
         log.trace 'Building avro config since there is no cached config'
         reloadAvroConfig()
      }
      avroConfig
   }


   /** Force a reload of the avro  configuration. */
   static void reloadAvroConfig() {
      mergeConfig OmarAvroReflectionUtils.getAvroConfig(), 'DefaultOmarAvroConfig'

      mergeConfigToGlobalConfig()

      log.trace 'reloaded avro config'
   }
   /** Reset the config for testing or after a dev mode Config.groovy change. */
   static synchronized void resetAvroConfig() {
      avroConfig = null
      log.trace 'reset avro config'
   }

   static void mergeConfigToGlobalConfig(){

      log.trace "mergeConfigToGlobalConfig(): Entered..............."

      // I might need to bridge some items to the quarts config variables
      // will leave these here for reference that I cut and past from our other
      // plugin
      //
      application.config.quartz.autoStartup = true
      application.config.quartz.jdbcStore   = false
      application.config.quartz.sessionRequired = true
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

      avroConfig = OmarAvroReflectionUtils.avroConfig = mergeConfig(currentConfig, secondary.avro as ConfigObject)
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
