package com.proxy.proxygateway.context;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.tuple.Pair;

import lombok.Getter;

@Getter
public class ProxyContext {
	
	private ConcurrentMap<String, String> proxyMap = new ConcurrentHashMap<String, String>();
	
	private ConcurrentMap<String, Pair<String,String>> proxyAnonymousMap = new ConcurrentHashMap<String, Pair<String,String>>();

	private ConcurrentMap<String, Pair<String,String>> proxyAllPermitMap = new ConcurrentHashMap<String, Pair<String,String>>();

	@Inject
	public ProxyContext(){
		Properties properties = readPropsFromPath("application.properties");
		
	 properties.entrySet().stream().filter(entry -> ((String)entry.getKey()).contains("proxy_gateway_mapping")).forEach(entry ->{
		 String[] proxyKeyValueHolder=( (String)entry.getValue()).split(",");
		 proxyMap.putIfAbsent(proxyKeyValueHolder[0], proxyKeyValueHolder[1]);
	 });
	 
	 populatedAdditonalProxyConfig( properties,proxyAnonymousMap,"proxy_gateway_anonymous_mapping");
	 populatedAdditonalProxyConfig( properties,proxyAllPermitMap,"proxy_gateway_allPermit_mapping");


	}
	
	private void populatedAdditonalProxyConfig(Properties properties,ConcurrentMap<String, Pair<String,String>> additonalProxyConfig , String filterName){
		//TODO add more validation....
		properties.entrySet().stream().filter(entry -> ((String)entry.getKey()).contains(filterName)).forEach(entry ->{
			 String[] proxyKeyValueHolder=( (String)entry.getValue()).split(",");
			 String[] proxyKeyMethodHolder = proxyKeyValueHolder[0].split("\\|");
			 additonalProxyConfig.putIfAbsent(proxyKeyMethodHolder[0], Pair.of(proxyKeyMethodHolder[1], proxyKeyValueHolder[1]));
		 });
	}
	
	
private   Properties readPropsFromPath(String path)  {
		

		Properties properties = new Properties();
	    try(InputStream inputStream = getClass().getClassLoader()
				.getResourceAsStream(path)) {
			
					properties.load(inputStream);
	    } catch (Exception e) {
			throw new RuntimeException (e.getMessage());
		}

		return properties;
	}

}
