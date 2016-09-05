package org.gokb.kabalog

import javax.persistence.Transient

class Location {

  Tenant owner
  String code

  static hasMany = [
  //  roles: RefdataValue,
  ]

  static mapping = {
    //         id column:'org_id'
    //    version column:'org_version'
    //mission column:'org_mission_fk_rv'
    code column:'loc_code'
    owner column:'loc_owner'
  }

  static constraints = {
    code(nullable:false, blank:false)
    owner(nullable:false, blank:false)
  }

}
