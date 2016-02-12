security{
   preauth {
      autoAddUsers = true
      requestHeader {
         username = "REMOTE_USERNAME"
         password = "REMOTE_PASSWORD"
      }
      defaultRoles = ["ROLE_USER"]
      userOverrides = []
      exceptionIfHeaderMissing = false
   }
   bootstrap {
      addDefaultUsers = true
   }
   spring{
      active = true

      providerNames = [
              'omarPreAuthenticatedAuthenticationProvider',
              'daoAuthenticationProvider',
              'anonymousAuthenticationProvider',
              'rememberMeAuthenticationProvider'
      ]
      securityConfigType = "Requestmap"
      userLookup{
         userDomainClassName = "omar.security.SecUser"
         authorityJoinClassName = "omar.security.SecUserSecRole"
      }
      authority{
         className = "omar.security.SecRole"
      }
      requestMap{
         className = "omar.security.Requestmap"
      }
      password{
         algorithm = 'bcrypt'
      }
      filterChain{
         chainMap = [
                 [
                         pattern:'/assets/**',
                         filters: 'none'
                 ],
                 [
                         pattern:'/**/js/**',
                         filters: 'none'
                 ],
                 [
                         pattern:'/**/css/**',
                         filters: 'none'
                 ],
                 [
                         pattern:'/**/images/**',
                         filters: 'none'
                 ],
                 [
                         pattern:'/**/favicon.ico',
                         filters: 'none'
                 ],
                 [
                         pattern:'/**',
                         filters: 'JOINED_FILTERS'
                 ],
         ]
      }
   }
}