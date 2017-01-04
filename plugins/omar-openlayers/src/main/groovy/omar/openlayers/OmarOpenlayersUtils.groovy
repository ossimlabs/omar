package omar.openlayers

import groovy.util.logging.Slf4j
import grails.core.GrailsApplication
import grails.util.Environment
import omar.openlayers.OmarOpenlayersUtils
import omar.openlayers.OmarOpenlayersReflectionUtils

@Slf4j
class OmarOpenlayersUtils
{
  //  private static ConfigObject openlayersConfig
  //  static GrailsApplication application
   //
  //  /**
  //   * Parse and load the openlayers configuration.
  //   * @return the configuration
  //   */
  //  static synchronized ConfigObject getOpenlayersConfig() {
  //     if (openlayersConfig == null) {
  //        log.trace 'Building openlayers config since there is no cached config'
  //        reloadOpenlayersConfig()
  //     }
  //     openlayersConfig
  //  }
   //
   //
  //  /** Force a reload of the openlayers  configuration. */
  //  static void reloadOpenlayersConfig() {
  //     mergeConfig OmarOpenlayersReflectionUtils.getOpenlayersConfig(), 'DefaultOmarOpenlayersConfig'
   //
  //     mergeConfigToGlobalConfig()
   //
  //     log.trace 'reloaded openlayers config'
  //  }
  //  /** Reset the config for testing or after a dev mode Config.groovy change. */
  //  static synchronized void resetOpenlayersConfig() {
  //     openlayersConfig = null
  //     log.trace 'reset openlayers config'
  //  }
   //
  //  static void mergeConfigToGlobalConfig(){
   //
  //     log.trace "mergeConfigToGlobalConfig(): Entered..............."
   //
  //     log.trace "mergeConfigToGlobalConfig(): Leaving..............."
   //
  //  }
  //  /**
  //   * Merge in a secondary config (provided by a plugin as defaults) into the main config.
  //   * @param currentConfig the current configuration
  //   * @param className the name of the config class to load
  //   */
  //  private static void mergeConfig(ConfigObject currentConfig, String className) {
  //     ConfigObject secondary = new ConfigSlurper(Environment.current.name).parse(
  //             new GroovyClassLoader(this.classLoader).loadClass(className))
   //
  //     openlayersConfig = OmarOpenlayersReflectionUtils.openlayersConfig = mergeConfig(currentConfig, secondary.openlayers as ConfigObject)
  //  }
   //
  //  /**
  //   * Merge two configs together. The order is important if <code>secondary</code> is not null then
  //   * start with that and merge the main config on top of that. This lets the <code>secondary</code>
  //   * config act as default values but let user-supplied values in the main config override them.
  //   *
  //   * @param currentConfig the main config, starting from Config.groovy
  //   * @param secondary new default values
  //   * @return the merged configs
  //   */
  //  private static ConfigObject mergeConfig(ConfigObject currentConfig, ConfigObject secondary) {
  //     (secondary ?: new ConfigObject()).merge(currentConfig ?: new ConfigObject()) as ConfigObject
  //  }
   //
  //  private static def mergeConfig(java.util.Map currentConfig, def grailsConfig)
  //  {
  //     currentConfig.keySet().each{key->
  //        grailsConfig."${key}" = currentConfig."${key}"
  //     }
  //     grailsConfig
  //  }
}
