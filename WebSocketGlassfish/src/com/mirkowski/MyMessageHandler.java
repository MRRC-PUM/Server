package com.mirkowski;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import javax.websocket.MessageHandler;
import javax.websocket.Session;

public class MyMessageHandler implements MessageHandler.Whole<String> {
	private final static String SERVERNAME = "System";
	
	private Session userSession = null;
	private Session opponentSession = null;
	
	public MyMessageHandler(Session session) {
		this.userSession = session;
	
	}

	
	public static String getSERVERNAME() {
		return SERVERNAME;
	}

	
	public Session getOpponentSession() {
		return opponentSession;
	}


	public void setOpponentSession(Session opponentSession) {
		this.opponentSession = opponentSession;
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
				
				if(message.getRecipientName().equals(SERVERNAME)){
					executeTask(message);
				} else if(opponentSession != null){
					 if(opponentSession.getUserProperties().get("username").equals(message.getRecipientName())){
						 unicastTransmission(jsonMessage,false);
					 } else {
						 echo("Incorrect player name");
					 }
				} else {
					if(message.getMessageType().equals("ChatroomMessage"))broadcastTransmission(jsonMessage);
					else if(message.getMessageType().equals("ChatMessage"))unicastTransmission(jsonMessage,true);
					else echo("you are don't connected to this user");
					
				}
			}
		}
			
		
		
	}
	
	private void sessionRegistration(Message message){
		if(message.getSenderName().equals(SERVERNAME)) message.setSenderName("Nice try, but no !!!");
		userSession.getUserProperties().put("username", giveName(message.getSenderName()));
		try {
			userSession.getBasicRemote()
			.sendText(JsonBulider.buildJsonMessageData(new Message(SERVERNAME
					, userSession.getUserProperties().get("username").toString()
					, "Registration"
					, (String) userSession.getUserProperties().get("username"))));

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
	
	private void broadcastTransmission(String jsonMessage){
		Session tempSession = null;
		Iterator<Session> iterator = EndpointSerwer.getusersSessionSet().iterator();
		while (iterator.hasNext()){
			tempSession = iterator.next();
			try {
				tempSession.getBasicRemote().sendText(jsonMessage);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@SuppressWarnings("unused")
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
						SERVERNAME
						, userSession.getUserProperties().get("username").toString()
						, "Error"
						, "The User is not available or does not exist")));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
		if(echoMode && isSent)
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
						SERVERNAME
						, userSession.getUserProperties().get("username").toString()
						, "Error"
						, "The User is not available or does not exist")));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
		if(echoMode && isSent)
			try {
				userSession.getBasicRemote().sendText(jsonMessage);
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	private void echo(String message){
		try {
			userSession.getBasicRemote()
			.sendText(JsonBulider.buildJsonMessageData(new Message(SERVERNAME
					, userSession.getUserProperties().get("username").toString()
					, "SystemResponse"
					, message)));

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
	
	private void sendRequest(Message message){
		unicastTransmission(JsonBulider.buildJsonMessageData(message), false);
	}
	private void executeTask(Message message){
		
		if(message.getMessageType().equals("Echo")){
			echo(message.getMessage());
		} else if(message.getMessageType().equals("PlayersList")){
			sendResponse(message);
		} else if(message.getMessageType().equals("StartGameRequest")){
			if(isCorrectName(message.getMessage())){
				sendRequest(new Message(SERVERNAME, message.getMessage(), "StartGameRequest", message.getSenderName()));
			}
		} else if(message.getMessageType().equals("StartGameResponse")){
			if(isCorrectName(message.getMessage())){
				opponentSession = findSession(message.getMessage());
				((MyMessageHandler) opponentSession.getMessageHandlers().iterator().next()).setOpponentSession(userSession);
				sendRequest(new Message(SERVERNAME, message.getMessage(), "StartGame", message.getSenderName()));
				sendRequest(new Message(SERVERNAME, message.getSenderName(), "StartGame", message.getMessage()));
			} 
		} else if(message.getMessageType().equals("NoStartGameResponse")){
			if(isCorrectName(message.getMessage())){
				sendRequest(new Message(SERVERNAME, message.getMessage(), "NoStartGameResponse", message.getSenderName()));
			} 
		} else if(message.getMessageType().equals("EndGame") || (message.getMessageType().equals("Stop") && message.getSenderName().equals(SERVERNAME))){
			if(isCorrectName(message.getMessage())){
				((MyMessageHandler) opponentSession.getMessageHandlers().iterator().next()).setOpponentSession(null);
				opponentSession = null;
				
				if(message.getMessageType().equals("Stop") && message.getSenderName().equals(SERVERNAME)){
					sendRequest(new Message(SERVERNAME, message.getMessage(), "Win", message.getRecipientName()));
//					sendRequest(new Message(SERVERNAME, message.getRecipientName(), "Defeat", message.getMessage()));
				} else if(message.getSenderName().equals(opponentSession.getUserProperties().get("username"))){
					sendRequest(new Message(SERVERNAME, message.getMessage(), "Win", message.getSenderName()));
					sendRequest(new Message(SERVERNAME, message.getSenderName(), "Defeat", message.getMessage()));
				} else {
					echo("Error");
				}
			} 
		} else {
			echo("Error");
		}
		
	}
	
	private String giveName(String senderName){
		Set<String> userNames = EndpointSerwer.getUserNames();
		Iterator<String> iterator = userNames.iterator();
		int count = 0;
		String tempName = senderName;
		while(iterator.hasNext()){
			if(iterator.next().equals(tempName)){
			tempName = senderName + "(" + ++count + ")";
			iterator = userNames.iterator();
			}
		}
		return tempName;
	}
	
	private boolean isCorrectName(String name){
		Iterator<String> iterator = EndpointSerwer.getUserNames().iterator();
		while(iterator.hasNext()){
			if(iterator.next().equals(name)) return true;
		}
		return false;
	}
	
	private Session findSession(String nameSession){
		Session tempSession = null;
		Iterator<Session> iterator = EndpointSerwer.getusersSessionSet().iterator();
		while (iterator.hasNext()){
			tempSession = iterator.next();
			if(tempSession.getUserProperties().get("username").toString().equals(nameSession)){
				return tempSession;
			} 
		}
		return null;
	}
	
	public void executeInternalTask(Message message){
		executeTask(message);
		
	}
}
