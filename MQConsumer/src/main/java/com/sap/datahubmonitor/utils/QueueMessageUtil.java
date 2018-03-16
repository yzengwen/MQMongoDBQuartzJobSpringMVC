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

public class QueueMessageUtil {

	private ConnectionFactory factory = null;
	private Connection connection = null;
	private Session session = null;
	private Destination destination = null;
	private MessageProducer producer = null;
	
	public void activeConnection() {
		factory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_BROKER_URL);
		try {
			connection = factory.createConnection();
			connection.start();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			destination = session.createQueue("SAMPLEQUEUE");
			producer = session.createProducer(destination);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	public void closeConnection() {
		try {
			producer.close();
			session.close();
			connection.stop();
			connection.close();
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	public void sendMessage(String json) {
		try {
			TextMessage message = session.createTextMessage();
			message.setText(json);
			producer.send(message);
			System.out.println("Sent: " + message.getText());
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	public static void main (String[] args) {
		String text = "{'title' : 'JVM原理', 'type' : 'technical', 'pages' : 350, 'tag2' : [ 'java', 'jvm', '虚拟机' ], "
				+ "'作者信息' : { 'name' : 'Yang', 'age' : '33', 'mobile' : '18688888888'}}";
		QueueMessageUtil producer = new QueueMessageUtil();
		producer.activeConnection();
		int num = 100;
		System.out.println("Will send " + num + " messages");
		for(int i=0; i<num; i++) {
			producer.sendMessage(text);
		}
		producer.closeConnection();
	}
}
