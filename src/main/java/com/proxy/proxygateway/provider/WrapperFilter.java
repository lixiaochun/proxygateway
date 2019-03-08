package com.proxy.proxygateway.provider;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.annotation.Priority;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import org.apache.commons.io.IOUtils;

/*@Provider
@PreMatching
@Priority(1)*/

/*public class WrapperFilter implements ContainerRequestFilter {
	
	  @Context
	    private HttpServletRequest httpServletRequest;

	@Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
		
        
        byte[] body = IOUtils.toByteArray(requestContext.getEntityStream());

        InputStream stream = new ByteArrayInputStream(body);
        requestContext.setEntityStream(stream);
      

   }	
	
	 
    class RequestWrapper extends HttpServletRequestWrapper {
	     
	    private String _body;
	 
	    public RequestWrapper(HttpServletRequest request) throws IOException {
	        super(request);
	        _body = "";
	        BufferedReader bufferedReader = request.getReader();           
	        String line;
	        while ((line = bufferedReader.readLine()) != null){
	            _body += line;
	        }
	    }
	 
	    @Override
	    public ServletInputStream getInputStream() throws IOException {
	        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(_body.getBytes());
	        return new ServletInputStream() {
	            public int read() throws IOException {
	                return byteArrayInputStream.read();
	            }
	        };
	    }
	 
	    @Override
	    public BufferedReader getReader() throws IOException {
	        return new BufferedReader(new InputStreamReader(this.getInputStream()));
	    }
	}



}*/
