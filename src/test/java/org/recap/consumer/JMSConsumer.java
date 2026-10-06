package org.recap.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnectionFactory;


import java.io.BufferedReader;
import java.io.InputStreamReader;

import jakarta.jms.Connection;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageConsumer;
import jakarta.jms.MessageListener;
import jakarta.jms.Queue;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;
import jakarta.jms.Topic;

/**
 * Created by sudhishk on 17/1/17.
 */
@Slf4j
public class JMSConsumer implements MessageListener {


    private static String topicName = "PUL.RequestT";
    private static String queueName = "lasOutgoingQ";
    private static String tstBrokerURL = "tcp://tst-recap-direct.htcinc.com:61616";


    public static void main(String[] args) throws Exception {
        topicQueueLasOutgoing();
    }

    private static void topicPulRequest() throws Exception {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(tstBrokerURL);
        Connection connection = connectionFactory.createConnection();
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        Topic topic = session.createTopic(topicName);
        MessageConsumer messageConsumer = session.createConsumer(topic);
        messageConsumer.setMessageListener(new JMSConsumer());
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

        log.info("Press enter to quit application");

        stdin.readLine();
        connection.close();
    }

    private static void topicQueueLasOutgoing() throws Exception {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(tstBrokerURL);
        Connection connection = connectionFactory.createConnection();
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        Queue queue = session.createQueue(queueName);
        MessageConsumer messageConsumer = session.createConsumer(queue);
        messageConsumer.setMessageListener(new JMSConsumer());
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

        log.info("Press enter to quit application");

        stdin.readLine();
        connection.close();
    }

    @Override
    public void onMessage(Message message) {
        try {
            TextMessage textMessage = (TextMessage) message;
            log.info("Message is " + textMessage.getText());
        } catch (JMSException e) {
            log.error("JMSException", e);
        }
    }

}
