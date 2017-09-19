package uk.gov.justice;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static uk.gov.justice.operations.JsonMessageCreator.createMessageWith;

import uk.gov.justice.operations.JmsOperations;
import uk.gov.justice.operations.OpenWireOperations;

import javax.jms.Message;
import javax.jms.TextMessage;

import org.junit.Before;
import org.junit.Test;

//to run this test from IDE start artemis first by executing ./target/server0/bin/artemis run
public class OpenWireArtemisQueueIT {

    private static final String JMS_URL = "tcp://localhost:61616";
    private static final String QUEUE_NAME = "DLQ";

    private final JmsOperations jmsOperations = new JmsOperations(JMS_URL, QUEUE_NAME);
    private final OpenWireOperations openWireOperations = new OpenWireOperations(QUEUE_NAME);

    @Before
    public void setup() throws Exception {
        jmsOperations.cleanQueue();
    }

    /**
     * Send a message using JMS and then use OpenWire to receive the message from the queue.
     *
     * Sending a message of 1686 lines long, which is on the boundary of being a large message and
     * works as expected.
     */
    @Test
    public void shouldSendSmallMessageWithJmsAndReceiveOverOpenWire() throws Exception {
        final String messageText = createMessageWith(1686L);

        // Send message to queue using JMS
        jmsOperations.sendMessage(messageText);

        //Receive message from queue using OpenWire
        final Message messageReceived = openWireOperations.receiveMessage();

        assertThat(messageReceived, is(notNullValue()));
        assertThat(((TextMessage) messageReceived).getText(), is(messageText));
    }

    /**
     * Send a large message using JMS and then use OpenWire to receive the message from the queue.
     *
     * Sending a message of 1687 or more lines long is a large message and results in a
     * NullPointerException being thrown on the broker and the receive message operation timing
     * out.
     */
    @Test
    public void shouldSendLargeMessageWithJmsAndReceiveOverOpenWire() throws Exception {
        final String messageText = createMessageWith(1687L);

        // Send large message to queue using JMS
        jmsOperations.sendMessage(messageText);

        //Receive message from queue using OpenWire
        final Message messageReceived = openWireOperations.receiveMessage();

        assertThat(messageReceived, is(notNullValue()));
        assertThat(((TextMessage) messageReceived).getText(), is(messageText));
    }

    /**
     * Send a large message using OpenWire and then use OpenWire to receive the message from the
     * queue.
     *
     * Sending a message of 1687 or more lines long is a large message and works as expected.
     */
    @Test
    public void shouldSendAndReceiveLargeMessageOverOpenWire() throws Exception {
        final String messageText = createMessageWith(1687L);

        // Send large message to queue using OpenWire
        openWireOperations.sendMessage(messageText);

        //Receive large message from queue using OpenWire
        final Message messageReceived = openWireOperations.receiveMessage();

        assertThat(messageReceived, is(notNullValue()));
        assertThat(((TextMessage) messageReceived).getText(), is(messageText));
    }
}
