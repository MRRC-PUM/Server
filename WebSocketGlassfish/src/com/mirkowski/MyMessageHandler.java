package com.mirkowski;

import java.io.IOException;
import java.util.Iterator;

import javax.websocket.MessageHandler;
import javax.websocket.Session;

public class MyMessageHandler implements MessageHandler.Whole<String> {

	
	private Session userSession = null;
	
	
	public MyMessageHandler(Session session) {
		this.userSession = session;
	
	}

	@Override
	public void onMessage(String jsonMessage) {
		String username = (String) userSession.getUserProperties().get("username");
		Message message = MessageDecoder.decode(jsonMessage);
		
		System.out.println("InMes: "+jsonMessage);
		
		if(message != null){
			
			if (username == null) {
				sessionRegistration(message);

			} else {
				
				if(message.getRecipientName().equals("System")){
					executeTask(message);
				} else {
					unicastTransmission(message,true);
					//broadcastTransmission(MessageDecoder.decode(jsonMessage));
				}
			}
		}
			
		
		
	}
	
	private void sessionRegistration(Message message){
		userSession.getUserProperties().put("username", message.getSenderName());
		try {
			userSession.getBasicRemote()
			.sendText(JsonBulider.buildJsonMessageData(new Message("System"
					, userSession.getUserProperties().get("username").toString()
					, "Registration"
					, "you are connected as "+message.getSenderName())));

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unused")
	private void broadcastTransmission(Message message){
		Session tempSession = null;
		Iterator<Session> iterator = EndpointSerwer.getusersSessionSet().iterator();
		while (iterator.hasNext()){
			tempSession = iterator.next();
			try {
				tempSession.getBasicRemote()
						.sendText(JsonBulider.buildJsonMessageData(new Message( userSession.getUserProperties().get("username").toString()
								, tempSession.getUserProperties().get("username").toString()
								, message.getMessageType()
								, message.getMessage())));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void unicastTransmission(Message message,Boolean echoMode){
		Boolean isSent = false;
		Session tempSession = null;
		Iterator<Session> iterator = EndpointSerwer.getusersSessionSet().iterator();
		while (iterator.hasNext()){
			tempSession = iterator.next();
			if(tempSession.getUserProperties().get("username").toString().equals(message.getRecipientName())){
				isSent=true;
				try {
					tempSession.getBasicRemote().sendText(JsonBulider.buildJsonMessageData(new Message( 
												   userSession.getUserProperties().get("username").toString()
												   , message.getRecipientName()
												   , message.getMessageType()
											 	   , message.getMessage())));
				} catch (IOException e) {
					e.printStackTrace();
				}
			} 
			
			if(isSent)break;
		}
		
		if(!isSent){
			try {
				userSession.getBasicRemote().sendText(JsonBulider.buildJsonMessageData(new Message(
						"System"
						, userSession.getUserProperties().get("username").toString()
						, "Error"
						, "The User is not available or does not exist")));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
		if(echoMode)
			try {
				userSession.getBasicRemote().sendText(JsonBulider.buildJsonMessageData(new Message( 
											   userSession.getUserProperties().get("username").toString()
											   , message.getRecipientName()
											   , message.getMessageType()
											   , message.getMessage())));
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	
	@SuppressWarnings("unused")
	private void unicastTransmission(String jsonMessage,Boolean echoMode){
		Boolean isSent = false;
		Session tempSession = null;
		String tempuserName = MessageDecoder.decode(jsonMessage).getRecipientName();
		Iterator<Session> iterator = EndpointSerwer.getusersSessionSet().iterator();
		while (iterator.hasNext()){
			tempSession = iterator.next();
			if(tempSession.getUserProperties().get("username").toString().equals(tempuserName)){
				isSent=true;
				try {
					tempSession.getBasicRemote().sendText(jsonMessage);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} 
			
			if(isSent)break;
		}
		
		if(!isSent){
			try {
				userSession.getBasicRemote().sendText(JsonBulider.buildJsonMessageData(new Message(
						"System"
						, userSession.getUserProperties().get("username").toString()
						, "Error"
						, "The User is not available or does not exist")));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
		if(echoMode)
			try {
				userSession.getBasicRemote().sendText(jsonMessage);
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	private void echo(Message message){
		try {
			userSession.getBasicRemote()
			.sendText(JsonBulider.buildJsonMessageData(new Message("System"
					, userSession.getUserProperties().get("username").toString()
					, "SystemResponse"
					, message.getMessage())));

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void sendResponse(Message message){
		try {
			userSession.getBasicRemote().sendText(JsonBulider.buildJsonUsersData(EndpointSerwer.getUserNames()
												  ,userSession.getUserProperties().get("username").toString()));

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void executeTask(Message message){
		
		if(message.getMessageType().equals("Echo")){
			echo(message);
		} else if(message.getMessageType().equals("PlayersList")){
			sendResponse(message);
		} else {
			
		}
		
	}
}
