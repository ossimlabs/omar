package omar.security

import grails.plugin.springsecurity.userdetails.GrailsUser
import org.springframework.security.core.userdetails.User
import  org.springframework.security.core.authority.SimpleGrantedAuthority

class UserDetails extends GrailsUser {

   String email
   String userRealName

   UserDetails(HashMap params)
   {
      super(params.username?:"", params.password?:"", params?.enabled,
              params.accountNonExpired!=null?params.accountNonExpired:true,
              params.credentialsNonExpired!=null?params.credentialsNonExpired:true,
              params.accountNonLocked!=null?params.accountNonLocked:true,
              params.authorities, params.id!=null?params.id:null)
      userRealName      = params?.userRealName
      email             = params?.email

   }
   HashMap toMap()
   {
      [
              username : username,
              password : password,
              userRealName : userRealName,
              email : email,
              enabled:enabled,
              accountNonExpired : accountNonExpired,
              accountNonLocked : accountNonLocked,
              credentialsNonExpired : credentialsNonExpired,
              authorites : authorities
      ]
   }
   Object getDomainClass() {
      return null;
   }
}