package com.proxy.proxygateway.provider;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang3.tuple.Pair;

import com.proxy.proxygateway.context.ProxyContext;

import lombok.extern.slf4j.Slf4j;

/**
 * The ProxyFilter for reverse proxy
 * 
 * @author Rohan Surve
 * @version 1.0
 * @since 2018-09-20
 */

@Provider
@PreMatching
@Priority(Priorities.AUTHENTICATION)
@Slf4j
public class ProxyFilter implements ContainerRequestFilter {

	private final ProxyContext context;
	

	@Inject
	public ProxyFilter(ProxyContext context) {

		this.context = context;

	}

	public void filter(ContainerRequestContext ctx) throws IOException {
		URI absolutePath = ctx.getUriInfo().getAbsolutePath();
		ConcurrentMap<String, Pair<String, String>> proxyAnonymousMap = this.context.getProxyAnonymousMap();

		boolean isAnonymous = false;
		for (Map.Entry<String, Pair<String, String>> entry : proxyAnonymousMap.entrySet()) {
			if (absolutePath.getPath().contains(entry.getKey())
					&& entry.getValue().getLeft().equalsIgnoreCase(ctx.getMethod())) {
				URI v2Redirect = URI.create(absolutePath.toString().replace(ctx.getUriInfo().getPath(true),
						"anonymous/" + ctx.getUriInfo().getPath(true)));
				log.info("****" + v2Redirect);
				ctx.setRequestUri(v2Redirect);
				ctx.getHeaders().add("anonymous", "yes");
				isAnonymous = true;
				break;
			}

		}

		if (!isAnonymous) {
			ConcurrentMap<String, Pair<String, String>> proxyAllPermitMap = this.context.getProxyAllPermitMap();

			for (Map.Entry<String, Pair<String, String>> entry : proxyAllPermitMap.entrySet()) {
				if (absolutePath.getPath().contains(entry.getKey())
						&& entry.getValue().getLeft().equalsIgnoreCase(ctx.getMethod())) {
					URI v2Redirect = URI.create(absolutePath.toString().replace(ctx.getUriInfo().getPath(true),
							"allpermit/" + ctx.getUriInfo().getPath(true)));
					log.info("allpermit..." + v2Redirect);
					ctx.getHeaders().add("allpermit", "yes");
				//	httpHeaders.getRequestHeaders().add("allpermit", "yes");

					ctx.setRequestUri(v2Redirect);
					break;
				}

			}

		}
		ctx.getHeaders().forEach((k,v) -> log.info("k..." + k+" v: "+v));

		/*
		 * UriInfo uriInfo = ctx.getUriInfo();
		 * 
		 * // convert baseUri to http://example.com/delegate/rest URI baseUri =
		 * uriInfo.getBaseUriBuilder() .path( uriInfo.getPathSegments().get( 0
		 * ).getPath() ).build();
		 * 
		 * 
		 * URI requestUri = uriInfo.getRequestUri();
		 * 
		 * // As expected, this will print out //
		 * setRequestUri("http://example.com/delegate/rest",
		 * "http://example.com/delegate/rest/foo") log.debug(
		 * "setRequestUri(\"{}\",\"{}\")", baseUri, requestUri );
		 * 
		 * ctx.setRequestUri( baseUri, requestUri );
		 */
	}

}
