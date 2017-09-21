package uk.gov.justice.operations;

import static org.apache.activemq.artemis.api.jms.ActiveMQJMSClient.createQueue;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.artemis.jms.client.ActiveMQQueueConnectionFactory;

public class JmsOperations {

    private final String url;
    private final String queueName;

    public JmsOperations(final String url, final String queueName) {
        this.url = url;
        this.queueName = queueName;
    }

    public void cleanQueue() throws JMSException {
        try (final ActiveMQQueueConnectionFactory connectionFactory = new ActiveMQQueueConnectionFactory(url);
             final QueueConnection queueConnection = connectionFactory.createQueueConnection();
             final QueueSession queueSession = queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE)) {

            queueConnection.start();
            final MessageConsumer consumer = queueSession.createConsumer(createQueue(queueName));

            while (consumer.receiveNoWait() != null) {
                //Do nothing
            }

            queueConnection.stop();
        }
    }

    public void sendMessage(final String messageText) throws JMSException {
        try (final ActiveMQQueueConnectionFactory connectionFactory = new ActiveMQQueueConnectionFactory(url);
             final QueueConnection queueConnection = connectionFactory.createQueueConnection();
             final QueueSession queueSession = queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE)) {

            queueConnection.start();

            final TextMessage message = queueSession.createTextMessage(messageText);

            final Queue queue = createQueue(queueName);

            final MessageProducer producer = queueSession.createProducer(queue);

            producer.send(message);

            queueConnection.stop();
        }
    }
}
