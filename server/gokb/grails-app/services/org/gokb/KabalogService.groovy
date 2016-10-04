package org.gokb

import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware

class KabalogService implements ApplicationContextAware {

  ApplicationContext applicationContext

  @javax.annotation.PostConstruct
  def init() {
    log.debug("KabalogService::init");
  }

  def loadCatalogRecord(record) {
    log.debug("Request to upload catalog file....");
    loadMODSRecord(record);
  }

  def loadMODSRecord(mods_record) {
    log.debug("loadMODSRecord");
  }

}
