package com.sap.datahubmonitor.utils;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQMessage;

public class MQConsumer {

	private static Properties properties = new Properties();

	static{
        try {
        	//System.out.println(MQConsumer.class.getClassLoader().getResource(""));
            InputStreamReader fileReader = new InputStreamReader(MQConsumer.class.getResourceAsStream("/config.properties"));
            properties.load(fileReader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	
	public void receive() {

		String brokerUrl = properties.getProperty("amqUrl", ActiveMQConnection.DEFAULT_BROKER_URL);
		String queueName = properties.getProperty("queueName");
		try {
			ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(brokerUrl);
			ActiveMQConnection connection = (ActiveMQConnection) factory.createConnection();
			final Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Destination destination = session.createQueue(queueName);
			MessageConsumer consumer = session.createConsumer(destination);
			consumer.setMessageListener(new MessageListener() {
				@Override
				public void onMessage(Message message) {
					try {
						ActiveMQMessage m = (ActiveMQMessage) message;
						System.out.println("#######message#########" + m.toString());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			connection.start();
			// connection.close();
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		MQConsumer receiver = new MQConsumer();
		receiver.receive();
	}
	
}