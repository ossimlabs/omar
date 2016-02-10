import omar.security.Requestmap
import omar.security.SecUser
import omar.security.SecRole
import omar.security.SecUserSecRole
import grails.plugin.springsecurity.SpringSecurityUtils

class OmarSecurityBootStrap
{
  def springSecurityService
  def grailsApplication

  def init = { servletContext ->

    if(SpringSecurityUtils.securityConfig?.active)
    {
      grails.plugin.springsecurity.SpringSecurityUtils.clientRegisterFilter('preAuthenticationFilter',
              grails.plugin.springsecurity.SecurityFilterPosition.PRE_AUTH_FILTER.getOrder() - 1)

      def roleData = [
              [authority: "ROLE_USER", description: "Standard User"],
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
      userData.each {
        if (!SecUser.findByUsername(it.username))
        {
          def user = new SecUser(it)
          user.encodePassword()
          user.save(flush: true)
          users << user
        }
      }
      users.each { user ->

        SecUserSecRole.create(user, roles['ROLE_USER'])

        if (user?.username == 'admin')
        {
          SecUserSecRole.create(user, roles['ROLE_ADMIN'])
        }
      }

      for (String url in [
              '/', '/error', '/index', '/index.gsp', '/**/favicon.ico', '/shutdown',
              '/**/js/**', '/**/css/**', '/**/images/**',
              '/login', '/login.*', '/login/*',
              '/logout', '/logout.*', '/logout/*'])
      {
        if (!Requestmap.findByUrl(url))
        {
          new Requestmap(url: url, configAttribute: 'permitAll').save(flush: true)
        }
      }

      for (String url in [
              '/secRole/save/**', '/secRole/update/**', '/secRole/delete/**', '/secRole/edit/**',
              '/secUser/save/**', '/secUser/update/**', '/secUser/delete/**', '/secUser/edit/**',
              '/secUserSecRole/save/**', '/secUser/update/**', '/secUser/delete/**', '/secUser/edit/**'])
      {
        if (!Requestmap.findByUrl(url))
        {
          new Requestmap(url: url, configAttribute: 'ROLE_ADMIN').save(flush: true)
        }
      }
      for (String url in [
              '/secRole/*', '/secRole/show/**', '/secRole/index/**',
              '/secUser/*', '/secUser/show/**', '/secUser/index/**',
              '/secUserSecRole/*', '/secUserSecRole/show/**', '/secUserSecRole/index/**'])
      {
        if (!Requestmap.findByUrl(url))
        {
          new Requestmap(url: url, configAttribute: 'ROLE_USER').save(flush: true)
        }
      }
    }
  }
  def destroy = {
  }

}