package com.sap.datahubmonitor;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.datahubmonitor.service.MessageHandler;

public class QueueMessageListenner implements MessageListener {
	
	private static final Logger logger = LoggerFactory.getLogger(QueueMessageListenner.class);
	
	@Resource(name="messageHandler")
	private MessageHandler messageHandler;
	
	@Override
	public void onMessage(Message message) {
		if(message instanceof TextMessage)
		{
			TextMessage textMessage = (TextMessage) message;
			try {
				if(logger.isDebugEnabled())
				{
					logger.debug("QueueMessageListenner received message {}", textMessage.getText());
				}
				messageHandler.handle(textMessage.getText());
			} catch (JMSException e) {
				logger.error("Error to persist the message", e);
			}
		}
		
	}

}
