package com.proxy.proxygateway.context;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;

import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;

@JsonInclude(Include.NON_NULL)
@Getter
public class GatewayProxyRequest {

	private String host;
	private int port;
	private String method;
	private String path;
	private String url;
	private Map<String, String> params;
	private Map<String, String> headers;
	private String content;
	
	private boolean anonymous ;
	private boolean allPermit ;

	// private MultivaluedMap<String, String> headers ;

	public GatewayProxyRequest(HttpServletRequest req, ContainerRequestContext crc) {
		this.method = crc.getMethod();// req.getMethod();
		// this.headers = crc.getHeaders();
		this.headers = getHeadersInfo(req,crc);
		this.params = getParametersInfo(req);
		 this.url = req.getRequestURL().toString();
		 this.url = req.getContextPath();
		this.host = req.getServerName();
		this.port = req.getServerPort();
		try {
			this.content = IOUtils.toString(crc.getEntityStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(anonymous){
			this.path = 	crc.getUriInfo().getPath(true).replace("anonymous/","");
			//URI v2Redirect = URI.create(crc.getUriInfo().getAbsolutePath().toString().replace("anonymous",""));
			//crc.setRequestUri(v2Redirect);
		}else if(allPermit){
			this.path = 	crc.getUriInfo().getPath(true).replace("allpermit/","");
			//URI v2Redirect = URI.create(crc.getUriInfo().getAbsolutePath().toString().replace("anonymous",""));
			//crc.setRequestUri(v2Redirect);
		}else{
		    this.path = crc.getUriInfo().getPath(true); 
		}
	}

	private Map<String, String> getHeadersInfo(HttpServletRequest request,ContainerRequestContext crc) {

		Map<String, String> headerParams = Collections.list((request).getHeaderNames()).stream()
				.collect(Collectors.toMap(h -> h, request::getHeader));
		
		if(crc.getHeaderString("anonymous")!=null){
			anonymous = true;
			headerParams.remove("anonymous");
		}else if(crc.getHeaderString("allpermit")!=null){
			allPermit = true;
			headerParams.remove("allpermit");
		}

		/*
		 * Map<String, String> map = new HashMap<String, String>();
		 * 
		 * Enumeration<?> headerNames = request.getHeaderNames(); while
		 * (headerNames.hasMoreElements()) { String key = (String)
		 * headerNames.nextElement(); String value = request.getHeader(key);
		 * map.put(key, value); }
		 */
		return headerParams;
	}

	private Map<String, String> getParametersInfo(HttpServletRequest request) {

		Map<String, String> map = new HashMap<String, String>();

		Enumeration<?> params = request.getParameterNames();
		while (params.hasMoreElements()) {
			String key = (String) params.nextElement();
			String value = request.getParameter(key);
			map.put(key, value);
		}

		return map;
	}

}