package org.gokb

import java.text.SimpleDateFormat
import java.net.InetAddress;

import org.elasticsearch.client.Client
import org.elasticsearch.node.Node
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.transport.InetSocketTransportAddress

import static groovy.json.JsonOutput.*

class ESWrapperService {

  static transactional = false

  def grailsApplication
  TransportClient esclient = null;

  @javax.annotation.PostConstruct
  def init() {
    log.debug("init ES wrapper service");

    Settings settings = Settings.builder().put("cluster.name", "elasticsearch").build();
    esclient = new org.elasticsearch.transport.client.PreBuiltTransportClient(settings);
    esclient.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));

    log.debug("ES wrapper service init completed OK");
  }

  def index(index,typename,record_id,record) {
    log.debug("index... ${typename},${record_id},...");
    def result=null;
    try {
      def future = esclient.prepareIndex(index,typename,record_id).setSource(record)
      result=future.get()
    }
    catch ( Exception e ) {
      log.error("Error processing ${toJson(record)}",e);
      e.printStackTrace()
    }
    log.debug("Index complete");
    result
  }


  def getClient() {
    return esclient
  }

  @javax.annotation.PreDestroy
  def destroy() {
    log.debug("Destroy");
  }


}
