# esb-ibm-mq-polling-inbound

### Steps for testing the inbound

1. Build the project using <b>mvn clean install -Dmaven.test.skip=true</b><br>
2. You will get the .Jar file copy file and paste it into lib folder $CARBON_HOME/repository/components/lib
3. Copy the following three jars to <b>$CARBON_HOME/repository/components/lib</b>

* com.ibm.mq.allclient.jar
* providerutil.jar
* fscontext.jar

5. Test using following sample configuration through proxy
6. For ssl import your certificate to the wso2carbon.jks using following command.
```
keytool -importcert -file <certificate file> -keystore <ESB>/repository/resources/security/wso2carbon.jks -alias "ibmwebspheremqqmanager"
```
#### SSL CipherSpecs and CipherSuites

Following cipher suites tested with the given fips configuration.(Some weak cipher suites are no longer supported in IBM websphere version 8.0.0.x)

CipherSpec  | Equivalent CipherSuite (Oracle JRE)|FipsRequired
------------- | ------------- | ------------- 
TLS_RSA_WITH_AES_128_CBC_SHA  | TLS_RSA_WITH_AES_128_CBC_SHA | False
TLS_RSA_WITH_3DES_EDE_CBC_SHA  | SSL_RSA_WITH_3DES_EDE_CBC_SHA |False
TLS_RSA_WITH_AES_128_CBC_SHA256  | TLS_RSA_WITH_AES_128_CBC_SHA256 |False
TLS_RSA_WITH_AES_128_CBC_SHA  | TLS_RSA_WITH_AES_128_CBC_SHA |False
TLS_RSA_WITH_AES_256_CBC_SHA   | TLS_RSA_WITH_AES_256_CBC_SHA |False
TLS_RSA_WITH_AES_256_CBC_SHA256  | TLS_RSA_WITH_AES_256_CBC_SHA256 |False

#### Description of the parameters

1. username - Username of the IBM MQ user group.
2. password - Password of the IBM MQ user group.
3. port - Port allowing IBM MQ for TCP/IP connections.
4. queueManager - Name of the IBM MQ queue manager.
5. channel - Name of the IBM MQ remote channel.
6. queue - Name of the queue.
7. maxConnections - number of maximum connections managed by the customized [connection pool](https://www.ibm.com/support/knowledgecenter/en/SSFKSJ_7.5.0/com.ibm.mq.dev.doc/q031110_.htm) for ibm mq connections.
8. maxUnusedConnections - Number of maximum unused connections managed by the customized [connection pool](https://www.ibm.com/support/knowledgecenter/en/SSFKSJ_7.5.0/com.ibm.mq.dev.doc/q031110_.htm).
9. timeout - Ends connections that are not used for this time in customized connection pool for ibm mq connections.
10. sslEnable - whether or not the ssl connection is needed (true/false).
11. cipherSuite - cipher suit specification for ibm mq connections.For further understanding refer [here](https://www.ibm.com/support/knowledgecenter/en/SSFKSJ_7.5.0/com.ibm.mq.dev.doc/q031290_.htm)Note that IBM MQ versions below 8.0.0.3 does not support many cipher specs.Update the IBM MQ using fix packs as mentioned in [this](http://www-01.ibm.com/support/docview.wss?uid=swg27006037) tutorial. 
12. trustStore - wso2carbon.jks
13. trustPassword - wso2carbon
14. keyStore - wso2carbon.jks
15. keyPassword - wso2carbon
16. [correlationID](https://www.ibm.com/support/knowledgecenter/en/SSFKSJ_7.5.0/com.ibm.mq.dev.doc/q033280_.htm#q033280___s1)-The CorrelationId to be included in the MQMD of a message when put on a queue. Also the ID to be matched against when getting a message from a queue.
17. [messageID](https://www.ibm.com/support/knowledgecenter/en/SSFKSJ_7.5.0/com.ibm.mq.dev.doc/q033280_.htm#q033280___s1)-The MessageId to be included in the MQMD of a message when put on a queue. Also the ID to be matched against when getting a message from a queue.Its initial value is all nulls.
18. connectionNameList - Reconnection parameters in case of connection failure.Add the list of hosts and ports here to connector to retry for the connections.
19. reconnectionTimeout - Reconnection parameters in case of connection failure .Add reconnection timeout for the reconnection.
20. topicName - Name of the topic as initialized in the queue manager.
21. [topicString](https://www.ibm.com/support/knowledgecenter/SSFKSJ_8.0.0/com.ibm.mq.pro.doc/q005000_.htm) topicString attribute as initialized in the queue manager.
22. durability - Whether the subscription for topic is durable or not
23. subscriptionName - Subscription name for durable subscriptions
24. contentType - the type that the message needs to build before injecting to synapse engine

#### Important Notes

* The username and the password parameters must be provided in order to obtain the necessary permissions to access the queue manager.
* The channel and the queueManager parameters should be provided with the correct configuration of sslEnable parameter to establish a successful connection.
* If the host and the port parameters not provided the connector will attempt to establish a connnection through the host "localhost" and port "1414".
* If the messageType parameter not provided the connector will use the default message type MQMT_DATAGRAM when publishing messages.
* If the timeout,the maxConnections and the maxUnusedConnections parameters not specified the default values of 3600,75 and 50 will be used.(3600s - 1Hr).
* The two timeout parameters(timeout and reconnectionTimeout) should be provided in seconds.
* If you are using a durable subscription subscriptionName parameter should be specified.

#### Sample inbound configuration for get the messages from queue
```
<inboundEndpoint name="class" sequence="{inject handler sequence}" onError="fault"
                            class="org.wso2.carbon.inbound.custom.poll.MQPollingInbound" suspend="false">
   <parameters>
      <parameter name="sequential">true</parameter>
      <parameter name="interval">2000</parameter>
      <parameter name="coordination">true</parameter>
      <parameter name="username">mqm</parameter>
      <parameter name="password">upgs5423</parameter>
      <parameter name="host">localhost</parameter>
      <parameter name="port">1414</parameter>
      <parameter name="queueManager">queueManager</parameter>
      <parameter name="queue">myqueue</parameter>
      <parameter name="channel">PASSWORD.SVRCONN</parameter>
      <parameter name="connectionNamelist">12.0.0.1/1414,127.0.0.1/1414</parameter>
      <parameter name="reconnectTimeout">10000</parameter>
      <parameter name="maxConnections">75</parameter>
      <parameter name="maxUnusedConnections">50</parameter>
      <parameter name=timeout">3600</parameter>
   </parameters>
</inboundEndpoint>

```

#### Sample inbound configuration for non durable subscription
```
<inboundEndpoint name="class" sequence="{inject handler sequence}" onError="fault"
                            class="org.wso2.carbon.inbound.custom.poll.MQPollingInbound" suspend="false">
   <parameters>
      <parameter name="sequential">true</parameter>
      <parameter name="interval">2000</parameter>
      <parameter name="coordination">true</parameter>
      <parameter name="username">mqm</parameter>
      <parameter name="password">upgs5423</parameter>
      <parameter name="host">localhost</parameter>
      <parameter name="port">1414</parameter>
      <parameter name="queueManager">queueManager</parameter>
      <parameter name="channel">PASSWORD.SVRCONN</parameter>
      <parameter name="topicString">mytopic</parameter>
      <parameter name="topicName">topic</parameter>
      <parameter name="connectionNamelist">12.0.0.1/1414,127.0.0.1/1414</parameter>
      <parameter name="reconnectTimeout">10</parameter>
      <parameter name="maxConnections">75</parameter>
      <parameter name="maxUnusedConnections">50</parameter>
      <parameter name=timeout">3600</parameter>
   </parameters>
</inboundEndpoint>

```

#### Sample inbound configuration for durable subscription
```
<inboundEndpoint name="class" sequence="{inject handler sequence}" onError="fault"
                            class="org.wso2.carbon.inbound.custom.poll.MQPollingInbound" suspend="false">
   <parameters>
      <parameter name="username">mqm</parameter>
      <parameter name="password">upgs5423</parameter>
      <parameter name="host">localhost</parameter>
      <parameter name="port">1414</parameter>
      <parameter name="queueManager">queueManager</parameter>
      <parameter name="channel">PASSWORD.SVRCONN</parameter>
      <parameter name="topicString">mytopic</parameter>
      <parameter name="topicName">topic</parameter>
      <parameter name="durability">CMQC.MQSO_DURABLE</parameter>
      <parameter name="subscriptionName">mySubscription</parameter>
      <parameter name="connectionNamelist">12.0.0.1/1414,127.0.0.1/1414</parameter>
      <parameter name="reconnectTimeout">10</parameter>
      <parameter name="maxConnections">75</parameter>
      <parameter name="maxUnusedConnections">50</parameter>
      <parameter name=timeout">3600</parameter>
   </parameters>
</inboundEndpoint>

```

#### Sample inbound configuration with ssl
```
<inboundEndpoint name="class" sequence="{inject handler sequence}" onError="fault"
                            class="org.wso2.carbon.inbound.custom.poll.MQPollingInbound" suspend="false">
   <parameters>
      <parameter name="sequential">true</parameter>
      <parameter name="interval">2000</parameter>
      <parameter name="coordination">true</parameter>
      <parameter name="username">mqm</parameter>
      <parameter name="password">upgs5423</parameter>
      <parameter name="host">localhost</parameter>
      <parameter name="port">1414</parameter>
      <parameter name="queueManager">queueManager</parameter>
      <parameter name="queue">myqueue</parameter>
      <parameter name="channel">PASSWORD.SVRCONN</parameter>
      <parameter name="connectionNamelist">12.0.0.1/1414,127.0.0.1/1414</parameter>
      <parameter name sslEnable>true</parameter>
      <parameter name="cipherSuite">SSL_RSA_WITH_3DES_EDE_CBC_SHA</parameter>
      <parameter name="flipsRequired">false</parameter>
      <parameter name="trustStore">wso2carbon.jks</parameter>
      <parameter name="trustPassword">wso2carbon</parameter>
      <parameter name="keyStore">wso2carbon.jks</parameter>
      <parameter name="keyPassword">wso2carbon</parameter>
      <parameter name="reconnectTimeout">10000</parameter>
      <parameter name="maxConnections">75</parameter>
      <parameter name="maxUnusedConnections">50</parameter>
      <parameter name=timeout">3600</parameter>
   </parameters>
</inboundEndpoint>

```