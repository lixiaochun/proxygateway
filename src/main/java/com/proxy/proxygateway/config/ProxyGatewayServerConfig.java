package com.proxy.proxygateway.config;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

import com.proxy.proxygateway.binder.ProxyServerBinder;

import lombok.extern.slf4j.Slf4j;

@ApplicationPath("/*")
@Slf4j

public class ProxyGatewayServerConfig extends ResourceConfig {
	
	public ProxyGatewayServerConfig() {
		log.info(" AppServerConfig for ApiServer is initialized");
		register(new ProxyServerBinder());

  
        register(RolesAllowedDynamicFeature.class,JacksonFeature.class);
    	property(ServerProperties.APPLICATION_NAME, "apiservers");

        packages("com.proxy.proxygateway.provider");
        packages("com.proxy.proxygateway.router");
        
    }
    
	
	


}
