package org.gokb

import org.elasticsearch.client.Client
import org.elasticsearch.node.Node
import static org.elasticsearch.node.NodeBuilder.nodeBuilder
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.groovy.*
import org.elasticsearch.common.transport.InetSocketTransportAddress
import static org.elasticsearch.node.NodeBuilder.nodeBuilder


class ESWrapperService {

  static transactional = false
  def grailsApplication

  def esclient = null;

  @javax.annotation.PostConstruct
  def init() {
    
    log.debug("Init");

    def clus_nm = grailsApplication.config.gokb_es_cluster ?: "gokb"

    log.debug("Using ${clus_nm} as ES cluster name...");

    Settings settings = Settings.settingsBuilder()
                       .put("client.transport.sniff", true)
                       .put("cluster.name", "elasticsearch")
                       .build();
    esclient = TransportClient.builder().settings(settings).build();
    // add transport addresses
    esclient.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300 as int))


    log.debug("Init completed");
  }

  @javax.annotation.PreDestroy
  def destroy() {
    log.debug("Destroy");
  }

  def getClient() {
    log.debug("getNode()");
    esclient
  }

}
