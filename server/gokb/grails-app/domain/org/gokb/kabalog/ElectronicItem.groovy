package org.gokb.kabalog

import org.gokb.cred.TitleInstancePlatform

import javax.persistence.Transient

class ElectronicItem extends Item {

  TitleInstancePlatform tip

  static hasMany = [
  ]

  static mapping = {
  }

  static constraints = {
  }

}
