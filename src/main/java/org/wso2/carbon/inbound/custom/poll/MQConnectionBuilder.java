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

import com.ibm.mq.MQEnvironment;
import com.ibm.mq.MQException;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.MQSimpleConnectionManager;
import com.ibm.mq.constants.CMQC;
import com.ibm.mq.constants.CMQXC;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.util.Collection;
import java.util.Vector;

/**
 * Start connection with IBM MQ queue manager
 */
public class MQConnectionBuilder {

    private static final Log logger = LogFactory.getLog(MQConnectionBuilder.class);
    private static MQQueueManager queueManager;
    MQConfiguration config;

    /**
     * Build connection
     */
    MQConnectionBuilder buildConnection(MQConfiguration config) {

        this.config = config;

        MQEnvironment.hostname = config.getHost();
        MQEnvironment.channel = config.getChannel();
        MQEnvironment.port = config.getPort();

        if (config.isSslEnable()) {
            MQEnvironment.sslCipherSuite = config.getCiphersuit();
            MQEnvironment.sslSocketFactory = createSSLContext().getSocketFactory();
            MQEnvironment.sslCipherSuite = config.getCiphersuit();
            MQEnvironment.sslFipsRequired = config.getFlipRequired();
        }

        MQEnvironment.properties.put(CMQC.TRANSPORT_PROPERTY, CMQC.TRANSPORT_MQSERIES_CLIENT);
        MQEnvironment.properties.put(CMQC.USER_ID_PROPERTY, config.getUserName());
        MQEnvironment.properties.put(CMQC.PASSWORD_PROPERTY, config.getPassword());
        Collection headerComp = new Vector();
        headerComp.add(new Integer(CMQXC.MQCOMPRESS_SYSTEM));
        MQEnvironment.hdrCompList = headerComp;
        MQEnvironment.setDefaultConnectionManager(customizedPool(config.getTimeout(), config.getmaxConnections(), config.getmaxnusedConnections()));

        try {
            queueManager = new MQQueueManager(config.getqManger());
            return this;
        } catch (MQException e) {
            logger.error("Error in connecting MQQueue Manger-" + e.getMessage());
            return null;
        }
    }

    /**
     * Initializing queue manager
     */
    public MQQueueManager getQueueManager() {
        if (queueManager == null || !queueManager.isConnected()) {
            try {
                queueManager = new MQQueueManager(config.getqManger());
            } catch (MQException e) {

            }
        }
        return queueManager;
    }

    /**
     * Closing IBM-MQ connection
     */
    public void closeConnection() {
        try {
            if (queueManager.isConnected()) {
                queueManager.close();
                queueManager = null;
            }
        } catch (MQException e) {

        }
    }

    /**
     * Setup customized connection pool for caching
     */
    MQSimpleConnectionManager customizedPool(long timeout, int maxConnections, int maxunusedConnections) {
        MQSimpleConnectionManager customizedPool = new MQSimpleConnectionManager();
        customizedPool.setActive(MQSimpleConnectionManager.MODE_AUTO);
        customizedPool.setTimeout(timeout);
        customizedPool.setMaxConnections(maxConnections);
        customizedPool.setMaxUnusedConnections(maxunusedConnections);
        return customizedPool;
    }

    /**
     * Setup ssl context
     */
    public SSLContext createSSLContext() {
        try {
            Class.forName("com.sun.net.ssl.internal.ssl.Provider");

            KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(new FileInputStream(config.getKeyStore()), config.getKeyPassword().toCharArray());

            KeyStore trustStore = KeyStore.getInstance("JKS");
            trustStore.load(new FileInputStream(config.getTrustStore()), config.getTrustPassword().toCharArray());

            TrustManagerFactory trustManagerFactory =
                    TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());

            KeyManagerFactory keyManagerFactory =
                    KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());

            trustManagerFactory.init(trustStore);
            keyManagerFactory.init(ks, config.getKeyPassword().toCharArray());

            SSLContext sslContext = SSLContext.getInstance("SSLv3");

            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(),
                    null);

            return sslContext;
        } catch (Exception e) {
            return null;
        }
    }
}
