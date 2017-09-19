package uk.gov.justice.operations;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class OpenWireOperations {

    private final String queueName;

    public OpenWireOperations(final String queueName) {
        this.queueName = queueName;
    }

    public void sendMessage(final String messageText) throws JMSException {
        final ConnectionFactory connectionFactory = new ActiveMQConnectionFactory();

        try (final Connection connection = connectionFactory.createConnection();
             final Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)) {

            final Queue queue = session.createQueue(queueName);

            final MessageProducer producer = session.createProducer(queue);

            final TextMessage message = session.createTextMessage(messageText);

            connection.start();

            producer.send(message);

            connection.stop();
        }
    }

    public Message receiveMessage() throws JMSException {
        final Message messageReceived;
        final ConnectionFactory connectionFactory = new ActiveMQConnectionFactory();

        try (final Connection connection = connectionFactory.createConnection();
             final Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)) {

            final Queue queue = session.createQueue(queueName);

            final MessageConsumer messageConsumer = session.createConsumer(queue);

            connection.start();

            messageReceived = messageConsumer.receive(5000);

            connection.stop();
        }

        return messageReceived;
    }
}
