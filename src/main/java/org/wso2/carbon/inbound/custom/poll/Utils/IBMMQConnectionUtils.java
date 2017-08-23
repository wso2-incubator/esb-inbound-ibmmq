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

import com.ibm.mq.MQException;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.MQSimpleConnectionManager;
import com.ibm.mq.constants.CMQC;
import com.ibm.mq.constants.CMQXC;
import com.ibm.mq.constants.MQConstants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Start connection with IBM MQ queue manager
 */
public class IBMMQConnectionUtils {

    private static final Log logger = LogFactory.getLog(IBMMQConnectionUtils.class);
    private static Map<String, MQSimpleConnectionManager> poolHistory = new HashMap<>();

    /**
     * This method use to get a queue manager specified by the parameters in IBMMQConfiguration.class.
     *
     * @param config IBMMQConfiguration object to get MQQueueManager parameters.
     * @return MQQueuemanager object for publishing messages.
     * @throws NoSuchAlgorithmException  This exception is thrown when a particular cryptographic algorithm is
     *                                   requested but is not available in the environment.
     * @throws ClassNotFoundException    Thrown when an application tries to load in a
     *                                   class through its string name but no definition for the class with the specified name could be found.
     * @throws KeyStoreException         This is the generic KeyStore exception.
     * @throws CertificateException      This exception indicates one of a variety of certificate problems.
     * @throws KeyManagementException    This is the general key management exception for
     *                                   all operations dealing with key management
     * @throws IOException               Signals that an I/O exception of some sort has occurred. This class is the general class of
     *                                   exceptions produced by failed or interrupted I/O operations.
     * @throws UnrecoverableKeyException This is the exception for invalid Keys (invalid encoding, wrong length,
     *                                   uninitialized, etc).
     */
    public synchronized static MQQueueManager getQueueManager(IBMMQConfiguration config) throws ClassNotFoundException,
            KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException,
            UnrecoverableKeyException,
            KeyManagementException {

        //Setting up environment for MQQueueManager connection
        Hashtable mqEnvironment = getMQEnvironment(config);
        //Initialize the connection with
        MQQueueManager queueManager = null;
        List<String> reconnectList = config.getReconnectList();
        reconnectList.add(config.getHost() + "/" + config.getPort());
        long start = System.currentTimeMillis();
        long end = start + config.getReconnectTimeout();
        A:
        while (System.currentTimeMillis() < end) {
            for (String conList : reconnectList) {
                String[] conArray = conList.split("/");
                mqEnvironment.put(MQConstants.HOST_NAME_PROPERTY, conArray[0]);
                mqEnvironment.put(MQConstants.PORT_PROPERTY, Integer.valueOf(conArray[1]));
                queueManager = ConnectQueueManager(mqEnvironment, config, conArray[0] + " " + conArray[1] + " " + config.getChannel());
                if (queueManager != null) {
                    break A;
                }
            }
        }
        if (queueManager == null) {
            logger.error("Reconnection timeout without establishing connection with the queue manager");
        }
        return queueManager;
    }

    /**
     * This method use to create a HashTable including IBMMQConfiguration parameters.
     *
     * @param config IBMMQConfiguration for create HashTable
     * @return A hashmap containing configuration details for IBM WebSphere MQ.
     * @throws NoSuchAlgorithmException  This exception is thrown when a particular cryptographic algorithm is
     *                                   requested but is not available in the environment.
     * @throws ClassNotFoundException    Thrown when an application tries to load in a
     *                                   class through its string name but no definition for the class with the specified name could be found.
     * @throws KeyStoreException         This is the generic KeyStore exception.
     * @throws CertificateException      This exception indicates one of a variety of certificate problems.
     * @throws KeyManagementException    This is the general key management exception for
     *                                   all operations dealing with key management
     * @throws IOException               Signals that an I/O exception of some sort has occurred. This class is the general class of
     *                                   exceptions produced by failed or interrupted I/O operations.
     * @throws UnrecoverableKeyException This is the exception for invalid Keys (invalid encoding, wrong length,
     *                                   uninitialized, etc).
     */
    private static Hashtable getMQEnvironment(IBMMQConfiguration config) throws ClassNotFoundException,
            KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException,
            UnrecoverableKeyException, KeyManagementException {

        Hashtable mqEnvironment = new Hashtable();
        //configurations for ssl
        if (config.isSslEnable()) {
            if (config.getCiphersuit().contains("TLS")) {
                Properties props = System.getProperties();
                props.setProperty("com.ibm.mq.cfg.useIBMCipherMappings", "false");
            }
            mqEnvironment.put(CMQC.SSL_CIPHER_SUITE_PROPERTY, config.getCiphersuit());
            mqEnvironment.put(CMQC.SSL_SOCKET_FACTORY_PROPERTY, createSSLContext(config).getSocketFactory());
            mqEnvironment.put(CMQC.SSL_FIPS_REQUIRED_PROPERTY, config.getFipsRequired());
        }

        //set up general configurations
        mqEnvironment.put(CMQC.TRANSPORT_PROPERTY, MQConstants.TRANSPORT_MQSERIES_CLIENT);
        mqEnvironment.put(CMQC.USER_ID_PROPERTY, config.getUserName());
        mqEnvironment.put(CMQC.PASSWORD_PROPERTY, config.getPassword());
        mqEnvironment.put(CMQC.CHANNEL_PROPERTY, config.getChannel());

        //compress header for bandwidth optimization
        Collection headerComp = new Vector();
        headerComp.add(new Integer(CMQXC.MQCOMPRESS_SYSTEM));
        mqEnvironment.put(CMQC.HDR_CMP_LIST, headerComp);

        //compress message for bandwidth optimization
        Collection msgComp = new Vector();
        msgComp.add(new Integer(CMQXC.MQCOMPRESS_RLE));
        msgComp.add(new Integer(CMQXC.MQCOMPRESS_ZLIBHIGH));
        mqEnvironment.put(CMQC.MSG_CMP_LIST, msgComp);
        return mqEnvironment;
    }

    /**
     * This method use to create a SSLContext for ssl connections
     *
     * @param config IBMMQConfiguration object to get the keystore/truststore names and passwords.
     * @return SSLContext using the IBMMQConfiguration parameters
     * @throws NoSuchAlgorithmException  This exception is thrown when a particular cryptographic algorithm is
     *                                   requested but is not available in the environment.
     * @throws ClassNotFoundException    Thrown when an application tries to load in a
     *                                   class through its string name but no definition for the class with the specified name could be found.
     * @throws KeyStoreException         This is the generic KeyStore exception.
     * @throws CertificateException      This exception indicates one of a variety of certificate problems.
     * @throws KeyManagementException    This is the general key management exception for
     *                                   all operations dealing with key management
     * @throws IOException               Signals that an I/O exception of some sort has occurred. This class is the general class of
     *                                   exceptions produced by failed or interrupted I/O operations.
     * @throws UnrecoverableKeyException This is the exception for invalid Keys (invalid encoding, wrong length,
     *                                   uninitialized, etc).
     */
    private static SSLContext createSSLContext(IBMMQConfiguration config) throws ClassNotFoundException,
            KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException,
            UnrecoverableKeyException, KeyManagementException {

        Class.forName("com.sun.net.ssl.internal.ssl.Provider");
        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(new FileInputStream(config.getKeyStore()), config.getKeyPassword().toCharArray());

        KeyStore trustStore = KeyStore.getInstance("JKS");
        trustStore.load(new FileInputStream(config.getTrustStore()), config.getTrustPassword().toCharArray());

        TrustManagerFactory trustManagerFactory =
                TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());

        KeyManagerFactory keyManagerFactory =
                KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());

        trustManagerFactory.init(trustStore);
        keyManagerFactory.init(keyStore, config.getKeyPassword().toCharArray());

        SSLContext sslContext = SSLContext.getInstance("SSLv3");

        sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(),
                null);

        return sslContext;
    }

    /**
     * This method use to create a pool for caching connections
     *
     * @param mqEnvironment MQEnvironment for connection
     * @param config        IBMMQConfiguration object to get the values for customized connection pool
     * @return MQSimpleConnectionManager object as customized pool
     */
    private static MQQueueManager ConnectQueueManager(Hashtable mqEnvironment, IBMMQConfiguration config, String message) {
        String status = "";
        MQQueueManager[] queueManager = {null};
        Future<String> manageConnection = Executors.newSingleThreadExecutor().submit(() -> {
            try {
                logger.debug("Attempting connection using connection pool");
                queueManager[0] = new MQQueueManager(config.getqManger(), mqEnvironment);
                logger.info("Queue manager connection established " + message);
                return "Connection established";
            } catch (MQException e) {
                logger.debug("Connection with IBM MQ not established");
                return "Connection not established";
            }
        });
        try {
            status = manageConnection.get(2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.debug(status);
            manageConnection.cancel(true);
        } catch (ExecutionException e) {
            logger.debug(status);
            manageConnection.cancel(true);
        } catch (TimeoutException e) {
            logger.debug(status);
            manageConnection.cancel(true);
        }
        return queueManager[0];
    }
}
