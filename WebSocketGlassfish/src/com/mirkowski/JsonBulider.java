package com.mirkowski;

import java.io.StringWriter;
import java.util.Iterator;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonWriter;


public class JsonBulider {
	
	public static String buildJsonUsersData(Set<String> UserNames, String recipientName){
		Iterator<String> iterator = UserNames.iterator();
		JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
		while(iterator.hasNext())jsonArrayBuilder.add((String)iterator.next());
		JsonObject jsonObject = Json.createObjectBuilder().add("senderName", MyMessageHandler.getSERVERNAME())
														  .add("recipientName", recipientName)
														  .add("messageType", "PlayersList")
														  .add("message", jsonArrayBuilder)
														  .build();
		

		return jsonObject.toString();
	}
	
	public static String buildJsonMessageData(Message message){
		JsonObject jsonObject = Json.createObjectBuilder().add("senderName", message.getSenderName())
														  .add("recipientName", message.getRecipientName())
														  .add("messageType", message.getMessageType())
														  .add("message", message.getMessage())
														  .build(); 
		
		StringWriter stringWriter = new StringWriter();
		try(JsonWriter jsonWriter = Json.createWriter(stringWriter)){jsonWriter.write(jsonObject);}
		System.out.println("OutMessage: "+stringWriter.toString());
		return stringWriter.toString();
	}
	
	
}
