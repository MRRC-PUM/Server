package com.mirkowski;

import java.util.HashSet;
import java.util.Set;

import javax.websocket.Endpoint;
import javax.websocket.server.ServerApplicationConfig;
import javax.websocket.server.ServerEndpointConfig;

public class SerwerApplicationConfigMy implements ServerApplicationConfig{

	@Override
	public Set<Class<?>> getAnnotatedEndpointClasses(Set<Class<?>> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<ServerEndpointConfig> getEndpointConfigs(Set<Class<? extends Endpoint>> arg0) {
		Set<ServerEndpointConfig> serwerEndpointConfigSet = new HashSet<ServerEndpointConfig>();
		serwerEndpointConfigSet.add(ServerEndpointConfig.Builder.create(EndpointSerwer.class, "/chat").build());
		return serwerEndpointConfigSet;
	}

}
