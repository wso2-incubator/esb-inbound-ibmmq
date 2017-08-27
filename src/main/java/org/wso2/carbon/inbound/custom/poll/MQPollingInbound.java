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

import com.ibm.mq.MQException;
import com.ibm.mq.MQGetMessageOptions;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.MQTopic;
import com.ibm.mq.constants.CMQC;
import com.ibm.mq.constants.MQConstants;
import com.ibm.mq.headers.MQMD;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.SynapseException;
import org.apache.synapse.core.SynapseEnvironment;
import org.wso2.carbon.inbound.custom.poll.Utils.IBMMQConfiguration;
import org.wso2.carbon.inbound.custom.poll.Utils.IBMMQConnectionUtils;
import org.wso2.carbon.inbound.endpoint.protocol.generic.GenericPollingConsumer;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Properties;

import static org.wso2.carbon.inbound.custom.poll.Utils.IBMMQConnectionUtils.getQueueManager;

/**
 * Polling consumer IBM MQ
 */
public class MQPollingInbound extends GenericPollingConsumer {

    private static final Log logger = LogFactory.getLog(MQPollingInbound.class);
    private MQQueueManager queueManager;
    private MQTopic subscriber;
    private MQQueue queue;
    private boolean isConnected = false;

    public MQPollingInbound(Properties properties, String name, SynapseEnvironment synapseEnvironment, long scanInterval, String injectingSeq, String onErrorSeq, boolean coordination, boolean sequential) {

        super(properties, name, synapseEnvironment, scanInterval, injectingSeq, onErrorSeq, coordination, sequential);
        this.injectingSeq = injectingSeq;
        logger.info("Initialized the IBM-MQ inbound consumer " + name);
    }

    public Object poll() {
        if (isConnected) {
            getMessage();
        } else {
            setupConnection();
        }
        return null;
    }

    /**
     * This method setup the connection with IBM MQ queue manager
     */
    private void setupConnection() {
        IBMMQConfiguration config = new IBMMQConfiguration(properties);
        try {
            queueManager = getQueueManager(config);
            if (config.getQueue() != null) {
                queue = queueManager.accessQueue(config.getQueue(), CMQC.MQRC_READ_AHEAD_MSGS);
            }
            if (config.getTopicName() != null && config.getTopicString() != null) {
                if (config.getDurability() == 0) {
                    subscriber = queueManager.accessTopic(config.getTopicString(), config.getTopicName(),
                            CMQC.MQTOPIC_OPEN_AS_SUBSCRIPTION, CMQC.MQSO_CREATE);
                } else {
                    int option = CMQC.MQSO_CREATE | CMQC.MQSO_FAIL_IF_QUIESCING | CMQC.MQSO_MANAGED | CMQC.MQSO_DURABLE;
                    subscriber = queueManager.accessTopic(config.getTopicString(), config.getTopicName(),
                            option, null, config.getSubscriptionName());
                }
            }
            isConnected = true;
        } catch (IOException ioe) {
            isConnected = false;
            handleException("Exception in queue", ioe);
        } catch (CertificateException ce) {
            isConnected = false;
            handleException("Certificate error", ce);
        } catch (NoSuchAlgorithmException iae) {
            isConnected = false;
            handleException("Invalid Algorithm", iae);
        } catch (UnrecoverableKeyException uke) {
            isConnected = false;
            handleException("Key is unrecoverable", uke);
        } catch (KeyStoreException ke) {
            isConnected = false;
            handleException("KeyStore is not valid", ke);
        } catch (ClassNotFoundException cne) {
            isConnected = false;
            handleException("Class not found", cne);
        } catch (KeyManagementException ikme) {
            isConnected = false;
            handleException("KeyManagement is invalid", ikme);
        } catch (Exception e) {
            isConnected = false;
            handleException("Generic exception", e);
        }
    }

    /**
     * This method gets the message from queue or topic
     */
    private void getMessage() {
        IBMMQConfiguration config = new IBMMQConfiguration(properties);
        MQMessage message = new MQMessage();
        MQGetMessageOptions gmo = new MQGetMessageOptions();
        if (config.getCorrelationID() != null && config.getMessageID() != null) {
            gmo.matchOptions = MQConstants.MQMO_MATCH_CORREL_ID + MQConstants.MQMO_MATCH_MSG_ID;
            message.correlationId = config.getCorrelationID().getBytes();
            message.messageId = config.getMessageID().getBytes();
        } else if (config.getMessageID() != null) {
            gmo.matchOptions = MQConstants.MQMO_MATCH_MSG_ID;
            message.messageId = config.getMessageID().getBytes();
        } else if (config.getCorrelationID() != null) {
            gmo.matchOptions = MQConstants.MQMO_MATCH_CORREL_ID;
            message.correlationId = config.getCorrelationID().getBytes();
        }
        try {
            if (queue != null) {
                queue.get(message, gmo);
            }
            if (subscriber != null) {
                subscriber.get(message);
            }
            MQMD md = new MQMD();
            md.copyFrom(message);
            message.getDataLength();
            int strLen = message.getDataLength();
            byte[] strData = new byte[strLen];
            message.readFully(strData);
            logger.info(new String(strData));
            injectIbmMqMessage(new String(strData), config.getcontentType());
        } catch (MQException e) {
            int reason = e.reasonCode;
            if (MQConstants.MQRC_CONNECTION_BROKEN == reason) {
                isConnected = false;
                logger.error("IBM MQ Connection Broken");
            } else if (MQConstants.MQRC_NO_MSG_AVAILABLE == reason) {
                logger.debug("Error while getting messages from queue", e);
            }
        } catch (Exception e) {
            handleException("", e);
        }
    }

    /**
     * This method inject the message to the sequence
     *
     * @param message
     * @param contentType
     */
    public void injectIbmMqMessage(String message, String contentType) {
        if (injectingSeq != null) {
            injectMessage(message, contentType);
            if (logger.isDebugEnabled()) {
                logger.debug("Injecting IBM MQ  message to the sequence : " + injectingSeq);
            }
        } else {
            handleException("The Sequence is not found");
        }
    }

    /**
     * This method handles the connection exceptions
     *
     * @param msg message to set for the exception
     * @param ex  throwable to set
     */
    public void handleException(String msg, Exception ex) {
        logger.error(msg, ex);
        throw new SynapseException(ex);
    }

    /**
     * This method handles the connection exceptions
     *
     * @param msg message to set for the exception
     */
    private void handleException(String msg) {
        logger.error(msg);
        throw new SynapseException(msg);
    }

    /**
     * This method terminate the connection with queue manager
     */
    public void destroy() {
        logger.info("Removing resources");
        try {
            if (queueManager.isConnected()) {
                queueManager.disconnect();
                queueManager = null;
            } else {
                queueManager = null;
            }
        } catch (MQException e) {
            logger.error("Error removing services");
        }
    }

}
