package omar.security

import grails.plugin.springsecurity.SpringSecurityUtils
import groovy.util.logging.Slf4j

@Slf4j
class OmarSecurityBootStrap
{
  def springSecurityService
  def grailsApplication

  def init = { servletContext ->

    log.trace("init: entered....................")

    if(SpringSecurityUtils.securityConfig?.active)
    {
      if(SpringSecurityUtils.securityConfig?.providerNames.find{it=='omarPreAuthenticatedAuthenticationProvider'})
      {
        grails.plugin.springsecurity.SpringSecurityUtils.clientRegisterFilter('preAuthenticationFilter',
                grails.plugin.springsecurity.SecurityFilterPosition.PRE_AUTH_FILTER.getOrder() - 1)
      }

      def roleData = [
              [authority: "ROLE_USER", description: "Standard User"],
              [authority: "ROLE_SWITCH_USER", description: "Allows one to switch to another user.  Similar to 'su' on unix"],
              [authority: "ROLE_ADMIN", description: "Administrator"],
      ]
      def roles = roleData.collect { SecRole.findOrSaveWhere(it) }.inject([:]) { a, b -> a[b.authority] = b; a }

      def userData = [
              [username      : "user", password: "user", enabled: true,
               accountExpired: false, accountLocked: false, passwordExpired: false, userRealName: "Some User",
               email         : "user@ossim.org"],
              [username      : "admin", password: "admin", enabled: true,
               accountExpired: false, accountLocked: false, passwordExpired: false, userRealName: "The Admin",
               email         : "admin@ossim.org"],
      ]
      def users = []

      //  Only add default users if requested to do so
      //
      if(OmarSecurityUtils.securityConfig.bootstrap.addDefaultUsers)
      {
        userData.each {
          if (!SecUser.findByUsername(it.username))
          {
            def user = new SecUser(it)
            user.save(flush: true)
            users << user
          }
        }
      }
      users.each { user ->
        SecUserSecRole.create(user, roles['ROLE_USER'])

        if (user?.username == 'admin')
        {
          SecUserSecRole.create(user, roles['ROLE_ADMIN'])
          SecUserSecRole.create(user, roles['ROLE_SWITCH_USER'])
        }
      }

      log.trace("init: Setting up for Spring Security config type = ${SpringSecurityUtils.securityConfig.securityConfigType}")
      // Only add request map definitions if request map config type is "requestmap"
      //
      if(SpringSecurityUtils.securityConfig.securityConfigType.toLowerCase() == "requestmap")
      {
        for (String url in [
                '/**', '/error', '/index', '/index.gsp', '/**/favicon.ico', '/shutdown',
                '/**/js/**', '/**/css/**', '/**/images/**',
                '/login', '/login.*', '/login/*',
                '/logout', '/logout.*', '/logout/*','/register/**'])
        {
          if (!Requestmap.findByUrl(url))
          {
            new Requestmap(url: url, configAttribute: 'permitAll').save(flush: true)
          }
        }

        for (String url in [
                '/secRole/save/**', '/secRole/update/**', '/secRole/delete/**', '/secRole/edit/**',
                '/secUser/save/**', '/secUser/update/**', '/secUser/delete/**', '/secUser/edit/**',
                '/secUserSecRole/save/**', '/secUser/update/**', '/secUser/delete/**', '/secUser/edit/**',
        '/securityinfo','/securityinfo/**','/user/**','/role/**','/registrationCode/**'])
        {
          if (!Requestmap.findByUrl(url))
          {
            new Requestmap(url: url, configAttribute: 'ROLE_ADMIN').save(flush: true)
          }
        }
        for (String url in [
                '/secRole/*', '/secRole/show/**', '/secRole/index/**',
                '/secUser/*', '/secUser/show/**', '/secUser/index/**',
                '/secUserSecRole/*', '/secUserSecRole/show/**', '/secUserSecRole/index/**',
                '/secure/**','/secure/index','/secure/index.*','/secure/index/**'])
        {
          if (!Requestmap.findByUrl(url))
          {
            new Requestmap(url: url, configAttribute: 'ROLE_USER').save(flush: true)
          }
        }
      }
    }

    log.trace("init: leaving....................")

  }
  def destroy = {
  }

}