package com.cooksys.ftd.assessment.filesharing.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cooksys.ftd.assessment.filesharing.dao.FileDao;
import com.cooksys.ftd.assessment.filesharing.dao.UserDao;
import com.cooksys.ftd.assessment.filesharing.model.api.AddFile;
import com.cooksys.ftd.assessment.filesharing.model.api.CreateUser;
import com.cooksys.ftd.assessment.filesharing.model.api.GetUser;
import com.cooksys.ftd.assessment.filesharing.model.api.IndexFile;
import com.cooksys.ftd.assessment.filesharing.model.api.SendFile;
import com.cooksys.ftd.assessment.filesharing.model.api.ServerResponse;
import com.cooksys.ftd.assessment.filesharing.model.db.ClientMessage;

public class ClientHandler implements Runnable {

	private Logger log = LoggerFactory.getLogger(ClientHandler.class);

	private BufferedReader reader;
	private PrintWriter writer;

	private JAXBContext context;
	private JAXBContext content;
	private Marshaller marshaller;
	private Unmarshaller unmarshaller;

	private FileDao fileDao;
	private UserDao userDao;

	public ClientHandler() {
		try {
			this.context = JAXBContext.newInstance(ClientMessage.class);
			this.content = JAXBContext.newInstance(ServerResponse.class);

			marshaller = content.createMarshaller();
			marshaller.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
			marshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, true);
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			unmarshaller = context.createUnmarshaller();
			unmarshaller.setProperty(UnmarshallerProperties.MEDIA_TYPE, "application/json");
			unmarshaller.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, true);
		} catch (JAXBException e) {
			log.error("Error creating client.", e);
		}
	}

	@Override
	public void run() {
		while (true) {
			log.debug("Started a connection");
			try {
				log.info("you have made it to the try block in CH");
				String echo = this.reader.readLine();
				log.debug("{}", echo);
				
				
				StringReader sr = new StringReader(echo);

				ClientMessage message = (ClientMessage) unmarshaller.unmarshal(sr);
				
				if (message.getCommand() == "register") {
					log.info("you made it to the register if staement");
					StringWriter sw = new StringWriter();
					ServerResponse<String> temp = CreateUser.newUser(message.getContent());
					marshaller.marshal(temp.getData(), sw);
					this.writer.println(sw.toString());
					this.writer.flush();
				} else if (message.getCommand() == "login") {
					StringWriter sw = new StringWriter();
					ServerResponse<String> temp = GetUser.getPassword(message.getContent());
					marshaller.marshal(temp.getData(), sw);
					this.writer.println(sw.toString());
					this.writer.flush();
				} else if (message.getCommand() == "files") {
					StringWriter sw = new StringWriter();
					ServerResponse<List<String>> temp = IndexFile.getFileList(message.getContent());
					marshaller.marshal(temp.getData(), sw);
					this.writer.println(sw.toString());
					this.writer.flush();
				} else if (message.getCommand() == "upload") {
					StringWriter sw = new StringWriter();
					ServerResponse<String> temp = AddFile.newFile(message.getContent());
					marshaller.marshal(temp.getData(), sw);
					this.writer.println(sw.toString());
					this.writer.flush();
				} else if (message.getCommand() == "download") {
					StringWriter sw = new StringWriter();
					ServerResponse<String> temp = SendFile.getFile(message.getContent());
					marshaller.marshal(temp.getData(), sw);
					this.writer.println(sw.toString());
					this.writer.flush();
				}
			} catch (IOException | JAXBException e) {
				log.error("A client error occured.", e);
			}
		}
	}

	public BufferedReader getReader() {
		return reader;
	}

	public void setReader(BufferedReader reader) {
		this.reader = reader;
	}

	public PrintWriter getWriter() {
		return writer;
	}

	public void setWriter(PrintWriter writer) {
		this.writer = writer;
	}

	public FileDao getFileDao() {
		return fileDao;
	}

	public void setFileDao(FileDao fileDao) {
		this.fileDao = fileDao;
	}

	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
}
