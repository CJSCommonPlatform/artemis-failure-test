package uk.gov.justice.operations;

import static java.util.Arrays.stream;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toList;
import static javax.management.MBeanServerInvocationHandler.newProxyInstance;
import static org.apache.activemq.artemis.api.config.ActiveMQDefaultConfiguration.getDefaultJmxDomain;

import java.util.List;

import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.activemq.artemis.api.core.RoutingType;
import org.apache.activemq.artemis.api.core.SimpleString;
import org.apache.activemq.artemis.api.core.management.ObjectNameBuilder;
import org.apache.activemq.artemis.api.core.management.QueueControl;

public class JmxOperations {

    private final String jmxUrl;
    private final String queueName;

    public JmxOperations(final String jmxUrl, final String queueName) {
        this.jmxUrl = jmxUrl;
        this.queueName = queueName;
    }

    /**
     * Browse the queue using a JMX connection, returns a list of the message text.
     *
     * @return the List of message text
     */
    public List<String> browseTextMessagesFromQueue() throws Exception {
        final ObjectName objectName = ObjectNameBuilder.create(getDefaultJmxDomain(), "0.0.0.0", true)
                .getQueueObjectName(SimpleString.toSimpleString(queueName), SimpleString.toSimpleString(queueName), RoutingType.ANYCAST);

        try (final JMXConnector connector = JMXConnectorFactory.connect(new JMXServiceURL(jmxUrl), emptyMap())) {

            final QueueControl queueControl = newProxyInstance(connector.getMBeanServerConnection(), objectName, QueueControl.class, false);
            final CompositeData[] compositeData = queueControl.browse("");

            return stream(compositeData)
                    .map(cd -> String.valueOf(cd.get("text")))
                    .collect(toList());
        }
    }
}
