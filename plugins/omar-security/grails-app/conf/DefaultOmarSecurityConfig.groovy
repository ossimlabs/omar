security{
   preauth {
      autoAddUsers = true
      requestHeader {
         username = "REMOTE_USERNAME"
         password = "REMOTE_PASSWORD"
      }
      defaultRoles = ["ROLE_USER"]

      /**
       * format is a list of maps where each map can have the
       * userOverrides =
         [ [
             username: 'test@foo.com'
             roles: ["ROLE_ADMIN",.....]
           ],
         ]
       **/
      userOverrides = []
      exceptionIfHeaderMissing = false
   }
   bootstrap {
      addDefaultUsers = true
   }

   // this is spring specific items that this plugin integrates into
   // Please change the config values here instead of under spring.
   // These will get synched to spring's configuration
   //
   spring{
      ui{
         switchUserRoleName = "ROLE_SWITCH_USER"
         register{
            defaultRoleNames = ["ROLE_USER"]
         }
      }
      useSwitchUserFilter = true
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