package com.cooksys.ftd.assessment.filesharing.model.db;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlRootElement
public class ClientMessage {
	
	private Logger log = LoggerFactory.getLogger(ClientMessage.class);

	@XmlElement(name = "command")
	private String command;
	@XmlElement(name = "content")
	private String content;

	public String getCommand() {
		//log.debug("you have reached the get command");
		return this.command;
	}
	
	public void setCommand(String command) {
		this.command = command;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public ClientMessage() {
		
	}

	@Override
	public String toString() {
		return "ClientMessage [log=" + log + ", command=" + command + ", content=" + content + "]";
	}
	
	
}
