package org.gokb

class KabalogService implements ApplicationContextAware {

  def grailsApplication

  @javax.annotation.PostConstruct
  def init() {
    log.debug("KabalogService::init");
  }

  def loadCatalogRecord(catlog_file) {
    log.debug("Request to upload catalog file....");
  }

}
