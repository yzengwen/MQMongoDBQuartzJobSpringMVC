package com.sap.datahubmonitor.utils;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public class QueueMessageProducer {

	private ConnectionFactory factory = null;
	private Connection connection = null;
	private Session session = null;
	private Destination destination = null;
	private MessageProducer producer = null;

	public void sendMessage() {

		try {
			factory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_BROKER_URL);
			connection = factory.createConnection();
			connection.start();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			destination = session.createQueue("SAMPLEQUEUE");
			producer = session.createProducer(destination);
			TextMessage message = session.createTextMessage();
			String text = "{ 'name' : 'xxlong4', 'age' : 104,'student' : [ 'stu1', 'stu2' ], 'course' : { 'book' :'c++' } }";
			message.setText(text);
			//message.setText(".......This is a sample message from QueueMessageSender..........");
			producer.send(message);
			System.out.println("Sent: " + message.getText());
			producer.close();
			session.close();
			connection.stop();
			connection.close();
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	public static void main (String[] args)
	{
		new QueueMessageProducer().sendMessage();
	}
}
