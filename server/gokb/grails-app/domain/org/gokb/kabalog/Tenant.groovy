package org.gokb.kabalog

import javax.persistence.Transient

class Tenant {

  String code

  static hasMany = [
  //  roles: RefdataValue,
  ]

  static mapping = {
    //         id column:'org_id'
    //    version column:'org_version'
    //mission column:'org_mission_fk_rv'
    code column:'tn_code'
  }

  static constraints = {
    code(nullable:false, blank:false)
  }

}
