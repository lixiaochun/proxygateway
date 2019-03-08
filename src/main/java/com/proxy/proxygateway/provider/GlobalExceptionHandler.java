package com.proxy.proxygateway.provider;



import javax.ws.rs.ForbiddenException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import lombok.extern.slf4j.Slf4j;

/**
 * The GlobalExceptionHandler provides global error handler for endpoints...
 * 
 * @author Rohan Surve
 * @version 1.0
 * @since 2018-09-24
 */

@Slf4j
@Provider
public class GlobalExceptionHandler extends Throwable implements ExceptionMapper<Throwable>
{
 
   @Override
   public Response toResponse(Throwable exception)
   {
	   log.info(" handling exception- "+exception.getMessage());
	   
	   if( exception instanceof ForbiddenException){
		      return Response.status(((ForbiddenException) exception).getResponse().getStatus()).entity(exception.getMessage()).type(MediaType.APPLICATION_JSON).build();

	   }
	   return Response.serverError()  .entity("Customer Support Required......").type(MediaType.APPLICATION_JSON).build();
   }
}
