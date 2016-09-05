package org.gokb.kabalog

import javax.persistence.Transient

class Item {

  Tenant owner

  static hasMany = [
  //  roles: RefdataValue,
  ]

  static mapping = {
    tablePerHierarchy false
    id column:'item_id'
    owner column:'item_owner'
  }

  static constraints = {
    owner(nullable:false, blank:false)
  }

}
