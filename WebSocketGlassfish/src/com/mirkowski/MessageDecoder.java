package com.mirkowski;

import java.io.StringReader;
import javax.json.Json;
import javax.json.JsonObject;

public class MessageDecoder {
	public static Message decode(String jsonmessage){
		try{
		JsonObject jsonObject = Json.createReader(new StringReader(jsonmessage)).readObject();
		System.out.println("JsonObject: "+jsonObject.toString() + " Jsonmessage: " + jsonmessage);
		return new Message(jsonObject.getString("senderName")
				 		  ,jsonObject.getString("recipientName")
				 		  ,jsonObject.getString("messageType")
				 		  ,jsonObject.getString("message"));
		} catch(Exception e) {
			return null;
		}
	}
}
