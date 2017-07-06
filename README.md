# esb-ibm-mq-polling-inbound

### Steps for testing the inbound

1. Build the project using <b>mvn clean install -Dmaven.test.skip=true</b><br>
2. You will get the .Jar file copy file and paste it into lib folder $CARBON_HOME/repository/components/lib
3. Copy the following three jars at <b>{basedir}/src/main/resources/lib/</b>  to <b>$CARBON_HOME/repository/components/lib</b>

* com.ibm.mq.allclient.jar
* providerutil.jar
* fscontext.jar

5. Test using following sample configuration through proxy

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
   </parameters>
</inboundEndpoint>

```