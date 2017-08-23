/*
*  Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.wso2.carbon.inbound.custom.poll.Utils;

import com.ibm.mq.constants.CMQC;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Class for get the IBM MQ configuration parameters for setting up connection
 */
public class IBMMQConfiguration {

    /**
     * Port allowing IBM MQ for TCP/IP connections
     */
    private int port;

    /**
     * The host name of the QueueManager to use.
     */
    private String host;

    /**
     * Name of the queue manager
     */
    private String qManger;

    /**
     * Name of the queue which the messages need to be placed
     */
    private String queue;

    /**
     * The name of the client connection channel through which messages are
     * sent from the connector to the remote queue manager.
     */
    private String channel;

    /**
     * Username for IBM WebSphere MQ user group
     */
    private String userName;

    /**
     * Use this property to specify the password of the user specified
     * by the value typed in the Username property
     */
    private String password;

    /**
     * Ends connections that are not used for this time in customized
     * connection pool for ibm mq connections
     */
    private long timeout;

    /**
     * number of maximum connections managed by the customized connection
     * pool for ibm mq connections
     */
    private int maxConnections;

    /**
     * the number of mamximum unused connections in the customized connection
     * pool for ibm mq connections
     */
    private int maxUnusedConnections;

    /**
     * whether or not the ssl connection is needed or not (true/false)
     */
    private boolean sslEnable;

    /**
     * cipher suit specification for ibm mq connections.Note that IBM MQ versions
     * below 8.0.0.3 does not support many cipher specs.Update the IBM MQ using fix packs.
     */
    private String ciphersuit;

    /**
     * Specify whether you want to enable FIPS support for an agent
     */
    private boolean fipsRequired;

    /**
     * Name of the truststore.Use the wso2 keystore after importing the certificates.
     */
    private String trustStore;

    /**
     * truststore password
     */
    private String trustPassword;

    /**
     * Name of the keystore.Use the wso2 keystore after importing the certificates.
     */
    private String keyStore;

    /**
     * keystore password
     */
    private String keyPassword;

    /**
     * Use the properties in this group to specify the message identifier for messages.
     */
    private String messageID;

    /**
     * Use the properties in this group to specify the correlation identifier for messages.
     */
    private String correlationID;

    /**
     * The group identifier for messages. The group identifier defines the messages
     * that belong to a specified group.
     */
    private String groupID;

    /**
     * Reconnection parameters in case of connection failure.Add the list of hosts
     * and ports here to connector to retry for the connections.
     */
    private List<String> reconnectList;

    /**
     * Reconnection parameters in case of connection failure .Add reconnection
     * timeout for the reconnection.
     */
    private int reconnectTimeout;

    /**
     * Topic name for publish messages
     */
    private String topicName;

    /**
     * Topic String for the topic
     */
    private String topicString;

    /**
     * If set, this property overrides the coded character set property
     * of the destination queue or topic.
     */
    private int charSet;

    /**
     * Content_Type of the message
     */
    private String contentType;

    /**
     * durability of the subscription
     */
    private int durability;

    /**
     * Name of the subscription
     */
    private String subscriptionName;

    /**
     * IBMMQConfiguration constructor for get the configuration parameters from
     * message context
     *
     * @param properties properties HashMap from IBMMQPropertyUtils
     */
    public IBMMQConfiguration(Properties properties) {

        //setting up default values for configuration parameters
        setupdefaultValues();

        if (properties.get(IBMMQConstants.PORT) != null) {
            this.port = Integer.valueOf(properties.getProperty(IBMMQConstants.PORT));
        }

        if (properties.get(IBMMQConstants.TOPIC_NAME) != null) {
            this.topicName = properties.getProperty(IBMMQConstants.TOPIC_NAME);
        }

        if (properties.get(IBMMQConstants.CONNECTION_NAMELIST) != null) {
            Arrays.asList((properties.getProperty(IBMMQConstants.CONNECTION_NAMELIST)).split(",")).forEach(item -> reconnectList.add(item));
        }

        if (properties.get(IBMMQConstants.TOPIC_STRING) != null) {
            this.topicString = properties.getProperty(IBMMQConstants.TOPIC_STRING);
        }

        if (properties.get(IBMMQConstants.SSL_ENABLE) != null) {
            this.sslEnable = Boolean.valueOf(properties.getProperty(IBMMQConstants.SSL_ENABLE));
        }

        if (properties.get(IBMMQConstants.TIMEOUT) != null) {
            this.timeout = Long.valueOf(properties.getProperty(IBMMQConstants.TIMEOUT)) * 1000;
        }

        if (properties.get(IBMMQConstants.RECONNECT_TIMEOUT) != null) {
            this.reconnectTimeout = Integer.valueOf(properties.getProperty(IBMMQConstants.RECONNECT_TIMEOUT)) * 1000;
        }

        if (properties.get(IBMMQConstants.MAX_CONNECTIONS) != null) {
            this.maxConnections = Integer.valueOf(properties.getProperty(IBMMQConstants.MAX_CONNECTIONS));
        }

        if (properties.get(IBMMQConstants.MAX_UNUSED_CONNECTIONS) != null) {
            this.maxUnusedConnections = Integer.valueOf(properties.getProperty(IBMMQConstants.CIPHERSUIT));
        }

        if (properties.getProperty(IBMMQConstants.CIPHERSUIT) != null) {
            this.ciphersuit = properties.getProperty(IBMMQConstants.CIPHERSUIT);
        }

        if (properties.get(IBMMQConstants.CHARACTER_SET) != null) {
            this.charSet = Integer.valueOf(properties.getProperty(IBMMQConstants.CHARACTER_SET));
        }

        if (properties.get(IBMMQConstants.FIPS_REQUIRED) != null) {
            this.fipsRequired = Boolean.valueOf(properties.getProperty(IBMMQConstants.FIPS_REQUIRED));
        }

        if (properties.get(IBMMQConstants.TRUST_STORE) != null) {
            this.trustStore = System.getProperty("user.dir") + "/repository/resources/security/" + properties.getProperty(IBMMQConstants.TRUST_STORE);
        }

        if (properties.get(IBMMQConstants.TRUST_PASSWORD) != null) {
            this.trustPassword = properties.getProperty(IBMMQConstants.TRUST_PASSWORD);
        }

        if (properties.get(IBMMQConstants.MESSAGE_ID) != null) {
            this.messageID = properties.getProperty(IBMMQConstants.MESSAGE_ID);
        }

        if (properties.get(IBMMQConstants.CORRELATION_ID) != null) {
            this.correlationID = properties.getProperty(IBMMQConstants.CORRELATION_ID);
        }

        if (properties.get(IBMMQConstants.GROUP_ID) != null) {
            this.groupID = properties.getProperty(IBMMQConstants.GROUP_ID);
        }

        if (properties.get(IBMMQConstants.KEY_STORE) != null) {
            this.keyStore = System.getProperty("user.dir") + "/repository/resources/security/" + properties.getProperty(IBMMQConstants.KEY_STORE);
        }

        if (properties.get(IBMMQConstants.KEY_PASSWORD) != null) {
            this.keyPassword = properties.getProperty(IBMMQConstants.KEY_PASSWORD);
        }

        if (properties.get(IBMMQConstants.HOST) != null) {
            this.host = properties.getProperty(IBMMQConstants.HOST);
        }

        if (properties.get(IBMMQConstants.QMANAGER) != null) {
            this.qManger = properties.getProperty(IBMMQConstants.QMANAGER);
        }

        if (properties.get(IBMMQConstants.QUEUE) != null) {
            this.queue = properties.getProperty(IBMMQConstants.QUEUE);
        }

        if (properties.get(IBMMQConstants.CHANNEL) != null) {
            this.channel = properties.getProperty(IBMMQConstants.CHANNEL);
        }

        if (properties.get(IBMMQConstants.USERNAME) != null) {
            this.userName = properties.getProperty(IBMMQConstants.USERNAME);
        }

        if (properties.get(IBMMQConstants.PASSWORD) != null) {
            this.password = properties.getProperty(IBMMQConstants.PASSWORD);
        }

        if (properties.get(IBMMQConstants.DURABILITY) != null) {
            this.durability = Integer.valueOf(properties.getProperty(IBMMQConstants.DURABILITY));
        }

        if (properties.get(IBMMQConstants.SUBSCRIPTION_NAME) != null) {
            this.subscriptionName = properties.getProperty(IBMMQConstants.SUBSCRIPTION_NAME);
        }

        if (properties.get(IBMMQConstants.CONTENT_TYPE) != null) {
            this.contentType = properties.getProperty(IBMMQConstants.CONTENT_TYPE);
        }
    }

    /**
     * Assign default values to configuration parameters
     */
    private void setupdefaultValues() {
        this.port = 1414;
        this.host = "127.0.0.1";
        this.qManger = null;
        this.queue = null;
        this.channel = null;
        this.userName = null;
        this.password = null;
        this.timeout = 3600000;
        this.maxConnections = 75;
        this.maxUnusedConnections = 50;
        this.sslEnable = false;
        this.ciphersuit = null;
        this.fipsRequired = false;
        this.trustStore = "wso2carbon.jks";
        this.trustPassword = "wso2carbon";
        this.keyStore = "wso2carbon.jks";
        this.keyPassword = "wso2carbon";
        this.messageID = null;
        this.correlationID = null;
        this.groupID = null;
        this.reconnectList = new ArrayList<>();
        this.reconnectTimeout = 10;
        this.topicName = null;
        this.topicString = null;
        this.charSet = IBMMQConstants.INTEGER_CONST;
        this.contentType = IBMMQConstants.DEFAULT_CONTENT_TYPE;
        this.durability = CMQC.MQSO_NON_DURABLE;
        this.subscriptionName = null;
    }

    /**
     * @return variable maxConnections.
     */
    public int getmaxConnections() {
        return maxConnections;
    }

    /**
     * @return variable maxUnusedConnections.
     */
    public int getmaxnusedConnections() {
        return maxUnusedConnections;
    }

    /**
     * @return variable timeout.
     */
    public long getTimeout() {
        return timeout;
    }

    /**
     * @return variable ciphersuit.
     */
    public String getCiphersuit() {
        return ciphersuit;
    }

    /**
     * @return variable fipsRequired.
     */
    public Boolean getFipsRequired() {
        return fipsRequired;
    }

    /**
     * @return variable sslEnable.
     */
    public boolean isSslEnable() {
        return sslEnable;
    }

    /**
     * @return variable trustStore.
     */
    public String getTrustStore() {
        return trustStore;
    }

    /**
     * @return variable trustPassword.
     */
    public String getTrustPassword() {
        return trustPassword;
    }

    /**
     * @return variable keyStore.
     */
    public String getKeyStore() {
        return keyStore;
    }

    /**
     * @return variable keyPassword.
     */
    public String getKeyPassword() {
        return keyPassword;
    }

    /**
     * @return variable topicName.
     */
    public String getTopicName() {
        return topicName;
    }

    /**
     * @return variable topicString.
     */
    public String getTopicString() {
        return topicString;
    }

    /**
     * @return variable port.
     */
    public int getPort() {
        return port;
    }

    /**
     * @return variable host.
     */
    public String getHost() {
        return host;
    }

    /**
     * @return variable qManager.
     */
    public String getqManger() {
        return qManger;
    }

    /**
     * @return variable queue.
     */
    public String getQueue() {
        return queue;
    }

    /**
     * @return variable channel.
     */
    public String getChannel() {
        return channel;
    }

    /**
     * @return variable userName.
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @return variable password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * @return variable messageID.
     */
    public String getMessageID() {
        return messageID;
    }

    /**
     * @return variable correlationID.
     */
    public String getCorrelationID() {
        return correlationID;
    }

    /**
     * @return variable groupID.
     */
    public String getgroupID() {
        return groupID;
    }

    /**
     * @return variable reconnectList.
     */
    public List<String> getReconnectList() {
        return reconnectList;
    }

    /**
     * @return int reconnectTimeout.
     */
    public int getReconnectTimeout() {
        return reconnectTimeout;
    }

    /**
     * @return int reconnectTimeout.
     */
    public int getCharSet() {
        return charSet;
    }

    /**
     * @return int durability.
     */
    public int getDurability() {
        return durability;
    }

    /**
     * @return variable contentType
     */
    public String getcontentType() {
        return contentType;
    }

    /**
     * @return variable subscriptionName
     */
    public String getSubscriptionName() {
        return subscriptionName;
    }
}