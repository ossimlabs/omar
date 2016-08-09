package omar.security

import grails.plugins.*
import grails.plugin.springsecurity.SpringSecurityUtils
import groovy.util.logging.Slf4j
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider
import org.springframework.security.web.authentication.preauth.PreAuthenticatedGrantedAuthoritiesUserDetailsService
import org.springframework.security.core.userdetails.UserDetailsByNameServiceWrapper

@Slf4j
class OmarSecurityGrailsPlugin extends Plugin {

    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "3.1.10 > *"
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
        "grails-app/views/error.gsp"
    ]

    // TODO Fill in these fields
    def title = "Omar Security" // Headline display name of the plugin
    def author = "Your name"
    def authorEmail = ""
    def description = '''\
Brief summary/description of the plugin.
'''
    def profiles = ['web']

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/omar-security"

    // Extra (optional) plugin metadata

    // License: one of 'APACHE', 'GPL2', 'GPL3'
//    def license = "APACHE"

    // Details of company behind the plugin (if there is one)
//    def organization = [ name: "My Company", url: "http://www.my-company.com/" ]

    // Any additional developers beyond the author specified above.
//    def developers = [ [ name: "Joe Bloggs", email: "joe@bloggs.net" ]]

    // Location of the plugin's issue tracker.
//    def issueManagement = [ system: "JIRA", url: "http://jira.grails.org/browse/GPMYPLUGIN" ]

    // Online location of the plugin's browseable source code.
//    def scm = [ url: "http://svn.codehaus.org/grails-plugins/" ]

    Closure doWithSpring() {
       log.trace("doWithSpring(): entered.............")

       //println OmarSecurityUtils.application.config.grails.plugin.springsecurity

       OmarSecurityReflectionUtils.application = OmarSecurityUtils.application = grailsApplication

       // reset and re-synch security configs
       SpringSecurityUtils.resetSecurityConfig()
       OmarSecurityUtils.resetSecurityConfig()
       OmarSecurityUtils.securityConfig

       //OmarSecurityUtils.application.config.grails.plugin.springsecurity.securityConfigType =  "Requestmap"
       //OmarSecurityUtils.application.config.grails.plugin.springsecurity.providerNames =  [
               //'omarPreAuthenticatedAuthenticationProvider',
       //        'daoAuthenticationProvider'
       //]
      // println "**********************"
      // println OmarSecurityUtils.securityConfig
      // println "**********************"
      // println SpringSecurityUtils.securityConfig
       // If spring security is enabled then setup and return the pre authentication beans
       //
       if(SpringSecurityUtils.securityConfig?.active)
       {

          log.trace('Spring security is active, initializing preauth beans')
          log.trace("doWithSpring(): leaving.............")
          return      {->
             preAuthenticationFilter(RequestHeaderAuthenticationFilter){
                authenticationManager              = ref('authenticationManager')
                usernameRequestHeader              = OmarSecurityUtils.securityConfig.preauth.requestHeader.username
                passwordRequestHeader              = OmarSecurityUtils.securityConfig.preauth.requestHeader.password
                exceptionIfHeaderMissing           = OmarSecurityUtils.securityConfig.preauth.exceptionIfHeaderMissing
                invalidateSessionOnPrincipalChange = true
             }
             omarUserDetailsService(UserDetailsService){
                autoAddUsers  = OmarSecurityUtils.securityConfig.preauth?.autoAddUsers
                userOverrides = OmarSecurityUtils.securityConfig.preauth?.userOverrides
                defaultRoles  = OmarSecurityUtils.securityConfig.preauth?.defaultRoles
             }

             omarUserDetailsServiceWrapper(UserDetailsByNameServiceWrapper) {
                userDetailsService = ref('omarUserDetailsService')
             }
             omarPreAuthenticatedAuthenticationProvider(PreAuthProvider){
                preAuthenticatedUserDetailsService = ref('omarUserDetailsServiceWrapper')
             }
          }
       }
       log.trace('Spring security is not active.  Beans will not be added')

       // return no beans if authentication is disabled
       log.trace("doWithSpring(): leaving.............")
       return {->

       }
    }

    void doWithDynamicMethods() {
        // TODO Implement registering dynamic methods to classes (optional)
    }

    void doWithApplicationContext() {
        // TODO Implement post initialization spring config (optional)
    }

    void onChange(Map<String, Object> event) {
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    void onConfigChange(Map<String, Object> event) {
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }

    void onShutdown(Map<String, Object> event) {
        // TODO Implement code that is executed when the application shuts down (optional)
    }
}
