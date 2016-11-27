#!/bin/bash
JAVA_HOME=/usr/lib/jvm/java-7-oracle 
export JAVA_HOME
PATH="$JAVA_HOME/bin:$PATH"
export PATH
GRAILS_OPTS="-XX:MaxPermSize=1024m -Xms2G -Xmx3G -server"
export GRAILS_OPTS
sdk use grails 2.3.11
