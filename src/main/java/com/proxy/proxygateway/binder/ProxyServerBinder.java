package com.proxy.proxygateway.binder;

import javax.inject.Singleton;

import org.glassfish.hk2.api.Immediate;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import com.proxy.proxygateway.context.ProxyContext;

import lombok.extern.slf4j.Slf4j;


/**
 * The ProxyServerBinder provides autowriing
 * 
 * @author Rohan Surve
 * @version 1.0
 * @since 2018-10-17
 */
@Slf4j
public class ProxyServerBinder extends AbstractBinder {
	@Override
	protected void configure() {

		log.debug("....ProxyServerBinder... configuration started");

		bind(ProxyContext.class).to(ProxyContext.class).in(Singleton.class);
		
	}
}
