package omar.security

import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedCredentialsNotFoundException;
import org.springframework.context.ApplicationContextAware
import org.springframework.context.ApplicationContext

import javax.servlet.http.HttpServletRequest;

import org.springframework.util.Assert;

public class RequestHeaderAuthenticationFilter extends AbstractPreAuthenticatedProcessingFilter{
    String usernameRequestHeader = "REMOTE_USER";
    String passwordRequestHeader = "REMOTE_PASSWORD";
    boolean exceptionIfHeaderMissing = false;

    /**
     * Read and returns the header named by {@code principalRequestHeader} from the request.
     *
     * @throws PreAuthenticatedCredentialsNotFoundException if the header is missing and {@code exceptionIfHeaderMissing}
     *          is set to {@code true}.
     */
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
        String username  = request.getHeader(usernameRequestHeader)
        if (username == null && exceptionIfHeaderMissing) {
            throw new PreAuthenticatedCredentialsNotFoundException(usernameRequestHeader
                    + " header not found in request.");
        }

       username;
    }

    /**
     * Credentials aren't usually applicable, but if a {@code credentialsRequestHeader} is set, this
     * will be read and used as the credentials value. Otherwise a dummy value will be used.
     */
    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
       if (passwordRequestHeader) {
            return request.getHeader(passwordRequestHeader);
        }

       return "N/A";
    }

    /**
     * Defines whether an exception should be raised if the principal header is missing. Defaults to {@code true}.
     *
     * @param exceptionIfHeaderMissing set to {@code false} to override the default behaviour and allow
     *          the request to proceed if no header is found.
     */
    public void setExceptionIfHeaderMissing(boolean exceptionIfHeaderMissing) {
        this.exceptionIfHeaderMissing = exceptionIfHeaderMissing;
    }
}