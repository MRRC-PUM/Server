package com.mirkowski;


public class Message {
	

	private String senderName;
	private String recipientName;
	private String messageType;
	private String message;
	
	
	public Message(String senderName, String recipientName, String messageType,String message) {
		this.senderName = senderName;
		this.recipientName = recipientName;
		this.messageType = messageType;
		this.message = message;
	}
	
	public String getSenderName() {
		return senderName;
	}
	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}
	public String getRecipientName() {
		return recipientName;
	}
	public void setRecipientName(String recipientName) {
		this.recipientName = recipientName;
	}
	public String getMessageType() {
		return messageType;
	}
	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	
}
