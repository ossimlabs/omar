package omar.zuul

import javax.servlet.http.HttpServletRequest
import com.netflix.zuul.context.RequestContext
import com.netflix.zuul.ZuulFilter

import org.slf4j.Logger
import org.slf4j.LoggerFactory

public class SimpleFilter extends ZuulFilter {

  private static Logger log = LoggerFactory.getLogger(SimpleFilter.class)

  @Override
  public String filterType() {
    return "pre"
  }

  @Override
  public int filterOrder() {
    return 1
  }

  @Override
  public boolean shouldFilter() {
    return true
  }

  @Override
  public Object run() {
    RequestContext ctx = RequestContext.currentContext
    HttpServletRequest request = ctx.request
    def requestURL = request.requestURL.toString()

    log.info("requestURI: ${request.requestURI}")

    if ( request.requestURI ==~ '^/[^/]+$' )
    {
     def newRequestURL = "${requestURL}/"
      // def newRequestURL = "http://google.com"

//       if ( request.method == 'GET')
//       {
//         log.info(String.format("Forwarding %s request to %s", request.requestURI, newRequestURL))
// //        def dispatcher = request.getRequestDispatcher(newRequestURL)
//         def dispatcher = request.getRequestDispatcher('/message-server/')
//
//         println dispatcher
//
//         dispatcher.forward(request, response);
//       }
//       else
//       {
        log.info(String.format("Redirecting %s request to %s", requestURL, newRequestURL))
        ctx.response.sendRedirect(newRequestURL)
      // }
    }
    else
    {
      log.info(String.format("%s request to %s", request.method, requestURL))
    }

    return null
  }

}
