package omar.security

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.core.Ordered
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken
import org.springframework.security.authentication.AccountStatusUserDetailsChecker
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsChecker
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.util.Assert;

public class PreAuthProvider implements AuthenticationProvider,
      InitializingBean, Ordered {
   private static final Log logger = LogFactory
         .getLog(PreAuthProvider.class);

   AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> preAuthenticatedUserDetailsService = null;
   UserDetailsChecker userDetailsChecker = new AccountStatusUserDetailsChecker();
   boolean throwExceptionWhenTokenRejected = false;

   int order = -1; // default: same as non-ordered


   /**
    * Check whether all required properties have been set.
    */
   public void afterPropertiesSet() {
      Assert.notNull(preAuthenticatedUserDetailsService,
            "An AuthenticationUserDetailsService must be set");
   }

   public Authentication authenticate(Authentication authentication)
                              throws AuthenticationException
   {
      Authentication result
      if (!supports(authentication.getClass())) {
         return null;
      }
      UserDetails ud =  preAuthenticatedUserDetailsService?.loadUserDetails(authentication)
      if(ud)
      {
         result = new PreAuthenticatedAuthenticationToken(
                                                           ud,
                                                           authentication.credentials,
                                                           ud.authorities);

      }

      result
   }

   /**
    * Indicate that this provider only supports PreAuthenticatedAuthenticationToken
    * (sub)classes.
    */
   public final boolean supports(Class<?> authentication) {
      return PreAuthenticatedAuthenticationToken.class.isAssignableFrom(authentication);
   }


   /**
    * If true, causes the provider to throw a BadCredentialsException if the presented
    * authentication request is invalid (contains a null principal or credentials).
    * Otherwise it will just return null. Defaults to false.
    */
   public void setThrowExceptionWhenTokenRejected(boolean throwExceptionWhenTokenRejected) {
      this.throwExceptionWhenTokenRejected = throwExceptionWhenTokenRejected;
   }

}


