package omar.security

import grails.plugin.springsecurity.userdetails.GrailsUserDetailsService
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.transaction.annotation.Transactional

class UserDetailsService  implements GrailsUserDetailsService {

    def autoAddUsers
    def userOverrides
    def defaultRoles
    private SecUser newUser(String username)
    {
        def secUser = new SecUser(username:username, password:"PreAuthenticated",
                    accountExpired: false, accountLocked: false, passwordExpired: false,
                    userRealName: username,
                    email: username.contains("@")?username:"")

        if(!secUser.save(flush:true))
        {
            secUser.errors.allErrors.each { println it }
        }

        secUser
    }
    UserDetails  loadUserByUsername(String username) {
        UserDetails result

        SecUser.withTransaction {
            SecUser secUser = SecUser.findByUsername(username)
            if (!secUser&&autoAddUsers)
            {
                // first add the user to the database
                secUser = newUser(username)
                secUser.save(flush:true)

                // We need to find all roles in the database
                //
                def userRoles = userOverrides?.find{it.username=="${username}"}

                // if user not found for overried then default the roles
                //
                userRoles = userRoles?:[username:username,
                                        roles:defaultRoles]
                def secRoles = []
                userRoles?.roles.each { role ->

                    def tempRole = SecRole.findByAuthority(role)
                    if (tempRole) secRoles << tempRole
                }

                // add the roles to the user by ading to the SecUserSecRole table
                if (secUser)
                {
                    secRoles?.each { secRole ->
                        SecUserSecRole.create(secUser, secRole)
                    }
                }

                result = secUser?.toUserDetails()
            }
            else if(secUser)
            {
                // found the user just convert the Domain to a User detail object
                //
                result = secUser?.toUserDetails()
            }
        }
        result
    }
    UserDetails  loadUserByUsername(String username, boolean v) {
        loadUserByUsername(username)
    }
}
