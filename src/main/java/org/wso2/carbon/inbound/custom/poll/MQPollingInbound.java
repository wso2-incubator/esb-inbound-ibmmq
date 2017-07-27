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

import com.ibm.mq.MQMessage;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.MQGetMessageOptions;
import com.ibm.mq.MQException;
import com.ibm.mq.constants.CMQC;
import com.ibm.mq.constants.MQConstants;
import com.ibm.mq.headers.MQMD;
import org.apache.axiom.om.OMElement;
import org.apache.axis2.builder.Builder;
import org.apache.axis2.builder.BuilderUtil;
import org.apache.axis2.builder.SOAPBuilder;
import org.apache.axis2.transport.TransportUtils;
import org.apache.commons.io.input.AutoCloseInputStream;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseException;
import org.apache.synapse.core.SynapseEnvironment;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.mediators.base.SequenceMediator;
import org.wso2.carbon.inbound.endpoint.protocol.generic.GenericPollingConsumer;

import java.io.ByteArrayInputStream;
import java.util.Properties;
import java.util.UUID;

/**
 * Polling consumer IBM MQ
 */
public class MQPollingInbound extends GenericPollingConsumer {

    private static final Log logger = LogFactory.getLog(MQPollingInbound.class);
    private MQConfiguration config;
    private MQQueueManager manager;
    private MQConnectionBuilder connectionBuilder = null;
    private MessageContext msgCtx;
    private boolean isConnected = false;

    public MQPollingInbound(Properties properties, String name, SynapseEnvironment synapseEnvironment, long scanInterval, String injectingSeq, String onErrorSeq, boolean coordination, boolean sequential) {

        super(properties, name, synapseEnvironment, scanInterval, injectingSeq, onErrorSeq, coordination, sequential);
        this.injectingSeq = injectingSeq;
        config = new MQConfiguration(properties);
        logger.info("Initialized the IBM-MQ inbound consumer " + name);
    }

    public Object poll() {

        if (logger.isDebugEnabled()) {
            logger.debug("Polling IBM-MQ messages for " + name);
        }

        if (!isConnected) {
            connectionBuilder = new MQConnectionBuilder().buildConnection(config);
            isConnected = true;
        }

        if (connectionBuilder == null) {
            logger.error("IBM-MQ Inbound endpoint " + name + " unable to get a connection.");
            isConnected = false;
            return null;
        }

        manager = connectionBuilder.getManager();

        MQQueue readableQueue;

        if (!manager.isConnected()) {
            logger.error("IBM-MQ Inbound endpoint queue manager not connected");
            return null;
        }
        try {
            readableQueue = manager.accessQueue(config.getQueue(), CMQC.MQRC_READ_AHEAD_MSGS);

            if (readableQueue == null) {
                logger.info("Queue is not initialized properly");
            } else {

                MQMessage message = new MQMessage();
                MQGetMessageOptions gmo = new MQGetMessageOptions();

                if (config.getCorrelationID() != null && config.getMessageID() != null) {
                    gmo.matchOptions = MQConstants.MQMO_MATCH_CORREL_ID + MQConstants.MQMO_MATCH_GROUP_ID;
                } else if (config.getMessageID() != null) {
                    gmo.matchOptions = MQConstants.MQMO_MATCH_MSG_ID;
                } else if (config.getCorrelationID() != null) {
                    gmo.matchOptions = MQConstants.MQMO_MATCH_CORREL_ID;
                }

                readableQueue.get(message, gmo);
                logger.info("A message received");

                MQMD md = new MQMD();
                md.copyFrom(message);

                message.getDataLength();
                int strLen = message.getDataLength();
                byte[] strData = new byte[strLen];
                message.readFully(strData);

                //crating new message context
                msgCtx = this.createMessageContext();
                msgCtx.setProperty("MessageId", new String(message.messageId));
                msgCtx.setProperty("CorrelationID", new String(message.correlationId));
                logger.info("Messsage data-" + new String(strData));

                if (message.getStringProperty("ContentType") != null) {
                    injectMessage(new String(strData), message.getStringProperty("ContentType"));
                } else {
                    injectMessage(new String(strData), MQConstant.DEFAULT_CONTENT_TYPE);
                }
                readableQueue.close();
            }

        } catch (Exception e) {
            logger.info("Queue is empty");
        }

        return null;
    }

    @Override
    protected boolean injectMessage(String strMessage, String contentType) {
        AutoCloseInputStream in = new AutoCloseInputStream(new ByteArrayInputStream(strMessage.getBytes()));
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Processed Custom inbound EP Message of Content-type : " + contentType + " for " + name);
            }
            org.apache.axis2.context.MessageContext axis2MsgCtx = ((Axis2MessageContext) msgCtx).getAxis2MessageContext();
            Builder builder;
            if (StringUtils.isEmpty(contentType)) {
                logger.warn("Unable to determine content type for message, setting to text/plain for " + name);
                contentType = MQConstant.DEFAULT_CONTENT_TYPE;
            }
            int index = contentType.indexOf(';');
            String type = index > 0 ? contentType.substring(0, index) : contentType;
            builder = BuilderUtil.getBuilderFromSelector(type, axis2MsgCtx);
            if (builder == null) {
                if (logger.isDebugEnabled()) {
                    logger.debug("No message builder found for type '" + type +
                            "'. Falling back to SOAP. for" + name);
                }
                builder = new SOAPBuilder();
            }
            OMElement documentElement = builder.processDocument(in, contentType, axis2MsgCtx);
            msgCtx.setEnvelope(TransportUtils.createSOAPEnvelope(documentElement));
            if (this.injectingSeq == null || "".equals(this.injectingSeq)) {
                logger.error("Sequence name not specified. Sequence : " + this.injectingSeq + " for " + name);
                return false;
            }
            SequenceMediator seq = (SequenceMediator) this.synapseEnvironment.getSynapseConfiguration()
                    .getSequence(this.injectingSeq);
            seq.setErrorHandler(this.onErrorSeq);
            if (seq != null) {
                if (logger.isDebugEnabled()) {
                    logger.debug("injecting message to sequence : " + this.injectingSeq + " of " + name);
                }
                if (!this.synapseEnvironment.injectInbound(msgCtx, seq, this.sequential)) {
                    return false;
                }
            } else {
                logger.error("Sequence: " + this.injectingSeq + " not found for " + name);
            }

        } catch (Exception e) {
            throw new SynapseException("Error while processing the IBM MQ Message ", e);
        }
        return true;
    }

    /**
     * Create the message context.
     */
    private MessageContext createMessageContext() {
        MessageContext msgCtx = this.synapseEnvironment.createMessageContext();
        org.apache.axis2.context.MessageContext axis2MsgCtx = ((Axis2MessageContext) msgCtx).getAxis2MessageContext();
        axis2MsgCtx.setServerSide(true);
        axis2MsgCtx.setMessageID(UUID.randomUUID().toString());
        return msgCtx;
    }

    /**
     * Stopping the inbound endpoint
     */
    public void destroy() {

        logger.info("Removing resources");
        try {
            if (manager.isConnected()) {
                manager.close();
                manager = null;
            } else {
                manager = null;
            }
        } catch (MQException e) {
            logger.error("Error removing services");
        }
    }

}
