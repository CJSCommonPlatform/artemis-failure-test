package uk.gov.justice;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static uk.gov.justice.operations.JsonMessageCreator.createMessageWith;

import uk.gov.justice.operations.JmsOperations;
import uk.gov.justice.operations.JmxOperations;
import uk.gov.justice.operations.OpenWireOperations;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

//to run this test from IDE start artemis first by executing ./target/server0/bin/artemis run
public class JmxArtemisQueueBrowserIT {

    private static final String JMS_URL = "tcp://localhost:61616";
    private static final String JMX_URL = "service:jmx:rmi:///jndi/rmi://localhost:3000/jmxrmi";
    private static final String QUEUE_NAME = "DLQ";

    private final JmsOperations jmsOperations = new JmsOperations(JMS_URL, QUEUE_NAME);
    private final JmxOperations jmxOperations = new JmxOperations(JMX_URL, QUEUE_NAME);
    private final OpenWireOperations openWireOperations = new OpenWireOperations(QUEUE_NAME);

    @Before
    public void setup() throws Exception {
        jmsOperations.cleanQueue();
    }

    /**
     * Send a message using JMS and then use JMX to browse the messages on the queue.
     *
     * Sending a message of 1686 lines long, which is on the boundary of being a large message and
     * works as expected.
     */
    @Test
    public void shouldBrowseMessageFromQueueWithJmx() throws Exception {
        final String messageText = createMessageWith(1686L);

        //Send message using JMS
        jmsOperations.sendMessage(messageText);

        //Browse queue using JMX
        final List<String> messageData = jmxOperations.browseTextMessagesFromQueue();

        assertThat(messageData.size(), is(1));
        assertThat(messageData.get(0), is(messageText));
    }

    /**
     * Send a large message using JMS and then use JMX to browse the messages on the queue.
     *
     * Sending a message of 1687 or more lines long is a large message and results in a
     * NullPointerException being thrown.
     */
    @Test
    public void shouldBrowseLargeMessageFromQueueWithJmx() throws Exception {
        final String messageText = createMessageWith(1687L);

        //Send large message using JMS
        jmsOperations.sendMessage(messageText);

        //Browse queue using JMX
        final List<String> messageData = jmxOperations.browseTextMessagesFromQueue();

        assertThat(messageData.size(), is(1));
        assertThat(messageData.get(0), is(messageText));
    }

    /**
     * Send a large message using OpenWire and then use JMX to browse the messages on the queue.
     *
     * Sending a message of 1687 or more lines long is a large message and works as expected.
     */
    @Test
    public void shouldBrowseLargeMessageSentWithOpenWireFromQueueWithJmx() throws Exception {
        final String messageText = createMessageWith(1687L);

        //Send large message using OpenWire
        openWireOperations.sendMessage(messageText);

        //Browse queue using JMX
        final List<String> messageData = jmxOperations.browseTextMessagesFromQueue();

        assertThat(messageData.size(), is(1));
        assertThat(messageData.get(0), is(messageText));
    }
}
