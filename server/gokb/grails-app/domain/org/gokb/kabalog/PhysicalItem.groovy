package org.gokb.kabalog

import org.gokb.cred.TitleInstance

import javax.persistence.Transient

class PhysicalItem extends Item {

  Location location
  TitleInstance titleInstance

  static hasMany = [
  ]

  static mapping = {
    location column:'phys_location'
  }

  static constraints = {
    location(nullable:false, blank:false)
  }

}
