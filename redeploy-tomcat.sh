set -v
 
mvn -DskipTests clean package
 
# Change the line below to the location of Tomcat built from trunk
TOMCAT=~/dev/apache/tomcat/trunk/output/build
 
rm -rf $TOMCAT/webapps/spring-websocket-test*
 
cp target/spring-websocket-test.war $TOMCAT/webapps/
 
export CATALINA_PID=$TOMCAT/logs/tomcat.pid
 
$TOMCAT/bin/shutdown.sh -force
 
rm $TOMCAT/logs/*
 
$TOMCAT/bin/startup.sh