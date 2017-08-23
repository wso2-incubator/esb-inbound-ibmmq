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

/**
 * IBM MQ constants
 */
public class IBMMQConstants {

    /**
     * Username for IBM WebSphere MQ user group
     */
    public static final String USERNAME = "username";

    /**
     * Use this property to specify the password of the user specified
     * by the value typed in the Username property
     */
    public static final String PASSWORD = "password";

    /**
     * Topic String for the topic
     */
    public static final String TOPIC_STRING = "topicString";

    /**
     * Topic name for publish messages
     */
    public static final String TOPIC_NAME = "topicName";

    /**
     * Port allowing IBM MQ for TCP/IP connections
     */
    public static final String PORT = "port";

    /**
     * The host name of the QueueManager to use.
     */
    public static final String HOST = "host";

    /**
     * Ends connections that are not used for this time in customized
     * connection pool for ibm mq connections
     *
     * @see http://www.ibm.com/support/knowledgecenter/en/SSFKSJ_7.5.0/com.ibm.mq.dev.doc/q031110_.htm
     */
    public static final String TIMEOUT = "timeout";

    /**
     * number of maximum connections managed by the customized connection
     * pool for ibm mq connections
     *
     * @see http://www.ibm.com/support/knowledgecenter/en/SSFKSJ_7.5.0/com.ibm.mq.dev.doc/q031110_.htm
     */
    public static final String MAX_CONNECTIONS = "maxConnections";

    /**
     * the number of maximum unused connections in the customized connection
     * pool for ibm mq connections
     *
     * @see http://www.ibm.com/support/knowledgecenter/en/SSFKSJ_7.5.0/com.ibm.mq.dev.doc/q031110_.htm
     */
    public static final String MAX_UNUSED_CONNECTIONS = "maxUnusedConnections";

    /**
     * Name of the queue manager
     */
    public static final String QMANAGER = "queueManager";

    /**
     * Name of the queue which the messages need to be placed
     */
    public static final String QUEUE = "queue";

    /**
     * The name of the client connection channel through which messages are
     * sent from the connector to the remote queue manager.
     */
    public static final String CHANNEL = "channel";

    /**
     * Whether to use a local binding or client/server TCP binding
     */
    public static final String TRANSPORT_TYPE = "transportType";

    /**
     * cipher suit specification for ibm mq connections.Note that IBM MQ versions
     * below 8.0.0.3 does not support many cipher specs.Update the IBM MQ using fix packs.
     *
     * @see http://www.ibm.com/support/knowledgecenter/en/SSFKSJ_7.5.0/com.ibm.mq.dev.doc/q031290_.htm
     * @see http://www-01.ibm.com/support/docview.wss?uid=swg27006037
     */
    public static final String CIPHERSUIT = "cipherSuite";

    /**
     * Specify whether you want to enable FIPS support for an agent
     */
    public static final String FIPS_REQUIRED = "fipsRequired";

    /**
     * whether or not the ssl connection is needed or not (true/false)
     */
    public static final String SSL_ENABLE = "sslEnable";

    /**
     * Name of the truststore.Use the wso2 keystore after importing the certificates.
     */
    public static final String TRUST_STORE = "trustStore";

    /**
     * truststore password
     */
    public static final String TRUST_PASSWORD = "trustPassword";

    /**
     * Name of the keystore.Use the wso2 keystore after importing the certificates.
     */
    public static final String KEY_STORE = "keyStore";

    /**
     * keystore password
     */
    public static final String KEY_PASSWORD = "keyPassword";

    /**
     * Use the properties in this group to specify the message identifier for messages.
     */
    public static final String MESSAGE_ID = "messageID";

    /**
     * Use the properties in this group to specify the correlation identifier for messages.
     */
    public static final String CORRELATION_ID = "correlationID";

    /**
     * The group identifier for messages. The group identifier defines the messages
     * that belong to a specified group.
     */
    public static final String GROUP_ID = "groupID";

    /**
     * Reconnection parameters in case of connection failure.Add the list of hosts
     * and ports here to connector to retry for the connections.
     */
    public static final String CONNECTION_NAMELIST = "connectionNameList";

    /**
     * Reconnection parameters in case of connection failure .Add reconnection
     * timeout for the reconnection.
     */
    public static final String RECONNECT_TIMEOUT = "reconnectTimeout";

    /**
     * If set, this property overrides the coded character set property
     * of the destination queue or topic.
     */
    public static final String CHARACTER_SET = "charSet";

    /**
     * Integer constant to identify the message priority and charset
     */
    public static final int INTEGER_CONST = -1;

    /**
     * Default content_type to use to build the message
     */
    public static final String DEFAULT_CONTENT_TYPE = "text/plain";

    /**
     * Durable or non durable subscription
     */
    public static final String DURABILITY = "durability";

    /**
     * Name of the subscription
     */
    public static final String SUBSCRIPTION_NAME = "subscriptionName";

    /**
     * Name of the subscription
     */
    public static final String CONTENT_TYPE = "contentType";

}
