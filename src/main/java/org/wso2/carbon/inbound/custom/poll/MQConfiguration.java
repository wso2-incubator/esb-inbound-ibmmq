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
package org.wso2.carbon.inbound.custom.poll;

import java.util.Properties;
import java.util.UUID;

/**
 * IBM MQ configuration
 */
public class MQConfiguration {
    private final int port;
    private final String host;
    private String qManger;
    private String topicName;
    private String topicString;
    private String queue;
    private String channel;
    private String userName;
    private String password;
    private int transportType;
    private long timeout;
    private int maxconnections;
    private int maxunusedconnections;
    private String ciphersuit;
    private Boolean flipRequired;
    private boolean sslEnable;
    private String trustStore;
    private String trustPassword;
    private String keyStore;
    private String keyPassword;
    private String messageID;
    private String correlationID;

    MQConfiguration(Properties ibmmqProperties) {

        if (ibmmqProperties.getProperty(MQConstant.PORT) != null) {
            this.port = Integer.valueOf(ibmmqProperties.getProperty(MQConstant.PORT));
        } else {
            this.port = 1414;
        }

        if (ibmmqProperties.getProperty(MQConstant.TOPIC_NAME) != null) {
            this.topicName = ibmmqProperties.getProperty(MQConstant.TOPIC_NAME);
        } else {
            this.topicName = null;
        }

        if (ibmmqProperties.getProperty(MQConstant.TOPIC_STRING) != null) {
            this.topicString = ibmmqProperties.getProperty(MQConstant.TOPIC_STRING);
        } else {
            this.topicString = null;
        }

        if (ibmmqProperties.getProperty(MQConstant.SSL_ENABLE) != null) {
            boolean sslProps = Boolean.valueOf(ibmmqProperties.getProperty(MQConstant.SSL_ENABLE));
            if (sslProps) {
                this.sslEnable = true;
            } else {
                this.sslEnable = false;
            }
        } else {
            this.sslEnable = false;
        }

        if (ibmmqProperties.getProperty(MQConstant.TIMEOUT) != null) {
            this.timeout = Long.valueOf(ibmmqProperties.getProperty(MQConstant.CIPHERSUIT));
        } else {
            this.timeout = 3600000;
        }

        if (ibmmqProperties.getProperty(MQConstant.MAX_CONNECTIONS) != null) {
            this.maxconnections = Integer.valueOf(ibmmqProperties.getProperty(MQConstant.CIPHERSUIT));
        } else {
            this.maxconnections = 75;
        }

        if (ibmmqProperties.getProperty(MQConstant.MAX_UNUSED_CONNECTIONS) != null) {
            this.maxunusedconnections = Integer.valueOf(ibmmqProperties.getProperty(MQConstant.CIPHERSUIT));
        } else {
            this.maxunusedconnections = 50;
        }

        if (ibmmqProperties.getProperty(MQConstant.CIPHERSUIT) != null) {
            this.ciphersuit = ibmmqProperties.getProperty(MQConstant.CIPHERSUIT);
        } else {
            this.ciphersuit = "SSL_RSA_WITH_3DES_EDE_CBC_SHA";
        }

        if (ibmmqProperties.getProperty(MQConstant.FLIP_REQUIRED) != null) {
            this.flipRequired = Boolean.valueOf(ibmmqProperties.getProperty(MQConstant.FLIP_REQUIRED));
        } else {
            this.flipRequired = false;
        }

        if (ibmmqProperties.getProperty(MQConstant.TRUST_STORE) != null) {
            this.trustStore = System.getProperty("user.dir") + "/repository/resources/security/"+ ibmmqProperties.getProperty(MQConstant.TRUST_STORE);
        } else {
            this.trustStore = null;
        }

        if (ibmmqProperties.getProperty(MQConstant.TRUST_PASSWORD) != null) {
            this.trustPassword = ibmmqProperties.getProperty(MQConstant.TRUST_PASSWORD);
        } else {
            this.trustPassword = null;
        }

        if (ibmmqProperties.getProperty(MQConstant.KEY_STORE) != null) {
            this.keyStore = System.getProperty("user.dir") + "/repository/resources/security/"+ ibmmqProperties.getProperty(MQConstant.KEY_STORE);
        } else {
            this.keyStore = null;
        }

        if (ibmmqProperties.getProperty(MQConstant.KEY_PASSWORD) != null) {
            this.keyPassword = ibmmqProperties.getProperty(MQConstant.KEY_PASSWORD);
        } else {
            this.keyPassword = null;
        }

        if (ibmmqProperties.getProperty(MQConstant.MESSAGE_ID) != null) {
            this.messageID = ibmmqProperties.getProperty(MQConstant.MESSAGE_ID);
        } else {
            this.messageID = UUID.randomUUID().toString();
        }

        if (ibmmqProperties.getProperty(MQConstant.CORRELATION_ID) != null) {
            this.correlationID = ibmmqProperties.getProperty(MQConstant.CORRELATION_ID);
        } else {
            this.correlationID = UUID.randomUUID().toString();
        }

        if (ibmmqProperties.getProperty(MQConstant.HOST) != null) {
            this.host = ibmmqProperties.getProperty(MQConstant.HOST);
        } else {
            this.host = "localhost";
        }

        if (ibmmqProperties.getProperty(MQConstant.TRANSPORT_TYPE) != null) {
            this.transportType = Integer.valueOf(ibmmqProperties.getProperty(MQConstant.TRANSPORT_TYPE));
        } else {
            this.transportType = 1;
        }

        if (ibmmqProperties.getProperty(MQConstant.QMANAGER) != null) {
            this.qManger = ibmmqProperties.getProperty(MQConstant.QMANAGER);
        } else {
            this.qManger = null;
        }

        if (ibmmqProperties.getProperty(MQConstant.QUEUE) != null) {
            this.queue = ibmmqProperties.getProperty(MQConstant.QUEUE);
        } else {
            this.queue = null;
        }

        if (ibmmqProperties.getProperty(MQConstant.CHANNEL) != null) {
            this.channel = ibmmqProperties.getProperty(MQConstant.CHANNEL);
        } else {
            this.channel = null;
        }

        if (ibmmqProperties.getProperty(MQConstant.USERNAME) != null) {
            this.userName = ibmmqProperties.getProperty(MQConstant.USERNAME);
        } else {
            this.userName = null;
        }

        if (ibmmqProperties.getProperty(MQConstant.PASSWORD) != null) {
            this.password = ibmmqProperties.getProperty(MQConstant.PASSWORD);
        } else {
            this.password = null;
        }
    }

    public int getmaxConnections() {
        return maxconnections;
    }

    public int getmaxnusedConnections() {
        return maxunusedconnections;
    }

    public long getTimeout() {
        return timeout;
    }

    public String getCiphersuit() {
        return ciphersuit;
    }

    public Boolean getFlipRequired() {
        return flipRequired;
    }

    public boolean isSslEnable() {
        return sslEnable;
    }

    public String getTrustStore() {
        return trustStore;
    }

    public String getTrustPassword() {
        return trustPassword;
    }

    public String getKeyStore() {
        return keyStore;
    }

    public String getKeyPassword() {
        return keyPassword;
    }

    public int getTransportType() {
        return transportType;
    }

    public String getTopicName() {
        return topicName;
    }

    public String getTopicString() {
        return topicString;
    }

    public int getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }

    public String getqManger() {
        return qManger;
    }

    public String getQueue() {
        return queue;
    }

    public String getChannel() {
        return channel;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }
}
