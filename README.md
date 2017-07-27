# esb-ibm-mq-polling-inbound

### Steps for testing the inbound

1. Build the project using <b>mvn clean install -Dmaven.test.skip=true</b><br>
2. You will get the .Jar file copy file and paste it into lib folder $CARBON_HOME/repository/components/lib
3. Copy the following three jars at <b>{basedir}/src/main/resources/lib/</b>  to <b>$CARBON_HOME/repository/components/lib</b>

* com.ibm.mq.allclient.jar
* providerutil.jar
* fscontext.jar

5. Test using following sample configuration through proxy
6. For ssl import the certificate to the wso2carbon.jks using following command.
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

1. username - Username of the IBM MQ user group
2. password - Password of the IBM MQ user group
3. port - Port allowing IBM MQ for TCP/IP connections
4. qmanager - Name of the IBM MQ queue manager
5. channel - Name of the IBM MQ remote channel
6. queue - Name of the queue
7. maxconnections - number of maximum connections managed by the customized [connection pool](https://www.ibm.com/support/knowledgecenter/en/SSFKSJ_7.5.0/com.ibm.mq.dev.doc/q031110_.htm) for ibm mq connections 
8. maxunusedconnections - the number of mamximum unused connections in the customized connection pool for ibm mq connections
9. timeout - Ends connections that are not used for this time in customized connection pool for ibm mq connections
10. sslenabled - whether or not the ssl connection is needed or not (true/false)
11. ciphersuit - cipher suit specification for ibm mq connections.For further understanding refer [here](https://www.ibm.com/support/knowledgecenter/en/SSFKSJ_7.5.0/com.ibm.mq.dev.doc/q031290_.htm)Note that IBM MQ versions below 8.0.0.3 does not support many cipher specs.Update the IBM MQ using fix packs as mentioned in [this](http://www-01.ibm.com/support/docview.wss?uid=swg27006037) tutorial. 
12. trustStore - wso2carbon.jks
13. trustpassword - wso2carbon
14. keyStore - wso2carbon.jks
15. keyPassword - wso2carbon
16. [correlationID](https://www.ibm.com/support/knowledgecenter/en/SSFKSJ_7.5.0/com.ibm.mq.dev.doc/q033280_.htm#q033280___s1)-The CorrelationId to be included in the MQMD of a message when put on a queue. Also the ID to be matched against when getting a message from a queue.
17. [messageID](https://www.ibm.com/support/knowledgecenter/en/SSFKSJ_7.5.0/com.ibm.mq.dev.doc/q033280_.htm#q033280___s1)-The MessageId to be included in the MQMD of a message when put on a queue. Also the ID to be matched against when getting a message from a queue.Its initial value is all nulls.
18. [groupID](https://www.ibm.com/support/knowledgecenter/en/SSFKSJ_7.5.0/com.ibm.mq.dev.doc/q033280_.htm#q033280___s1)-This is a byte string that is used to identify the particular message group or logical message to which the physical message belongs.
19. connectionNamelist - Reconnection parameters in case of connection failure.Add the list of hosts and ports here to connector to retry for the connections.
20. channelList - Reconnection parameters in case of connection failure.Add list of to connector to retry for the connections.
21. reconnectionTimeout - Reconnection parameters in case of connection failure .Add reconnection timeout for the reconnection.

#### Sample configuration
```
<inboundEndpoint name="class" sequence="{inject handler sequence}" onError="fault"
                            class="org.wso2.carbon.inbound.custom.poll.MQPollingInbound" suspend="false">
   <parameters>
      <parameter name="sequential">true</parameter>
      <parameter name="interval">2000</parameter>
      <parameter name="coordination">true</parameter>
      <parameter name="username">{ibm mq username}</parameter>
      <parameter name="password">{ibm mq password}</parameter>
      <parameter name="host">localhost</parameter>
      <parameter name="port">1414</parameter>
      <parameter name="qmanager">{queue manager name}</parameter>
      <parameter name="queue">{queue name}</parameter>
      <parameter name="channel">{channel name}</parameter>
      <parameter name="connectionNamelist">12.0.0.1/1414,127.0.0.1/1414</parameter>
      <parameter name="channelList">PASSWORD.SVRCONN,PASSWORD.SVRCONN,PASSWORD.SVRCONN</parameter>
      <parameter name="reconnectTimeout">10000</parameter>
      <parameter name="maxconnections">75</parameter>
      <parameter name="maxunusedconnections">50</parameter>
      <parameter name=timeout">3600000</parameter>
      <parameter name="messageID">MessageID@IBMMQ123</parameter>
      <parameter name="correlationID">CorrelationID@IBMMQ123</parameter>
   </parameters>
</inboundEndpoint>

```