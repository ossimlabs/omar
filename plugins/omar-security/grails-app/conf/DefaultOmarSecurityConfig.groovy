security{
   preauth {
      autoAddUsers = true
      requestHeader {
         username = "REMOTE_USERNAME"
         password = "REMOTE_PASSWORD"
      }
      defaultRoles = ["ROLE_USER"]
      userOverrides = []
   }
   bootstrap {
      addDefaultUsers = true
   }
}