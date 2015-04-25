package com.mirkowski;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;

public class EndpointSerwer extends Endpoint {

	private static Set<Session> usersSessionSet =  Collections.synchronizedSet(new HashSet<Session>());

	public void onOpen(Session session, EndpointConfig endpointConfig) {
		usersSessionSet.add(session);
		session.addMessageHandler(new MyMessageHandler(session));

	}

	@Override
	public void onClose(Session session, CloseReason closeReason) {
		if(session.getUserProperties().get("username") != null &&  ((MyMessageHandler) session.getMessageHandlers().iterator().next()).getOpponentSession() != null){
			((MyMessageHandler) session.getMessageHandlers().iterator().next())
					.executeInternalTask(new Message(MyMessageHandler.getSERVERNAME()
							, session.getUserProperties().get("username").toString()
							, "Stop"
							, ((MyMessageHandler) session.getMessageHandlers().iterator().next()).getOpponentSession().getUserProperties().get("username").toString() ));
		}
		usersSessionSet.remove(session);
		Iterator<Session> iterator = usersSessionSet.iterator();
		Session tempSession = null;
		while(iterator.hasNext()){
			try {
				tempSession = iterator.next();
				if(tempSession.getUserProperties().get("username").toString() != null)
				tempSession.getBasicRemote().sendText(JsonBulider.buildJsonUsersData(getUserNames()
													  , tempSession.getUserProperties().get("username").toString())); // err java.lang.reflect.InvocationTargetException
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	
	@Override
	public void onError(Session session, Throwable throwable) {
		throwable.printStackTrace();
		
	}
	
	
	
	
	public static Set<String> getUserNames(){
		HashSet<String> returnSet = new HashSet<String>();
		Iterator<Session> iterator = usersSessionSet.iterator();
		while(iterator.hasNext()) {
			try {
				returnSet.add(iterator.next().getUserProperties().get("username").toString());//err null pointer
			} catch (Exception e) {}
		}
		return returnSet;
	}

	public static Set<Session> getusersSessionSet() {
		return usersSessionSet;
	}
	
	
}
