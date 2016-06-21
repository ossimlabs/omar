package sqs.app
import groovy.util.logging.Slf4j
import grails.util.Environment

@Slf4j
class SqsUtils
{
   private static ConfigObject sqsConfig

   /**
    * Parse and load the sqs configuration.
    * @return the configuration
    */
   static synchronized ConfigObject getSqsConfig() {
      if (sqsConfig == null) {
         log.trace 'Building sqs config since there is no cached config'
         reloadSqsConfig()
      }
      sqsConfig
   }


   /** Force a reload of the sqs  configuration. */
   static void reloadSqsConfig() {
    log.trace "reloadSqsConfig: Entered........"
      mergeConfig SqsConfig.sqsConfig, 'DefaultSqsConfig'

      mergeConfigToGlobalConfig()

      log.info "Reloaded config:\n${sqsConfig.toString()}"

      log.trace 'reloadSqsConfig: Leaving......'
   }
   /** Reset the config for testing or after a dev mode Config.groovy change. */
   static synchronized void resetSqsConfig() {
      sqsConfig = null
      log.trace 'reset sqs config'
   }

   static void mergeConfigToGlobalConfig(){

      log.trace "mergeConfigToGlobalConfig(): Entered..............."

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
      sqsConfig = SqsConfig.sqsConfig = mergeConfig(currentConfig, secondary.sqs as ConfigObject)
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
