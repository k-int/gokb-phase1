package org.gokb

import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware

class KabalogService implements ApplicationContextAware {

  ApplicationContext applicationContext
  def titleLookupService

  @javax.annotation.PostConstruct
  def init() {
    log.debug("KabalogService::init");
  }

  def loadCatalogRecord(record) {
    log.debug("Request to upload catalog file....");
    loadMODSRecord(record);
  }

  def loadMODSRecord(mods_record) {
    log.debug("loadMODSRecord rec: ${mods_record}");
    log.debug("loadMODSRecord title: ${mods_record.titleInfo.title}");

    def title_metadata = [
      title:mods_record.titleInfo.title,
      subtitle:mods_record.titleInfo.subTitle,
      identifiers:[
        // [type:mods_record.recordInfo.recordIdentifier.@source.value, value:mods_record.recordInfo.recordIdentifier.value]
      ],
      publisher_name:''
    ]

    mods_record.recordInfo.recordIdentifier.each {
      log.debug("Adding identifier 1 ${it.value}");
      title_metadata.identifiers.add([type:it.@source, value:it])
    }

    mods_record.identifier.each {
      log.debug("Adding identifier 2 ${it.value}");
      title_metadata.identifiers.add([type:it.@type, value:it])
    }

    log.debug("Extracted title data: ${title_metadata}");

    // titleLookupService.find(title_metadata,user,null,'org.gokb.cred.BookInstance');
  }

}
