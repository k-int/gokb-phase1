package org.gokb

import grails.converters.*
import org.springframework.security.acls.model.NotFoundException
import grails.plugins.springsecurity.Secured
import org.codehaus.groovy.grails.commons.GrailsClassUtils
import org.gokb.cred.*
import grails.plugin.gson.converters.GSON
import org.springframework.web.multipart.MultipartHttpServletRequest
import com.k_int.ConcurrencyManagerService;
import com.k_int.ConcurrencyManagerService.Job
import java.security.MessageDigest
import grails.converters.JSON

import org.hibernate.ScrollMode
import org.hibernate.ScrollableResults
import org.hibernate.type.*
import org.hibernate.Hibernate



class TitleListController {

  def genericOIDService
  def springSecurityService
  def concurrencyManagerService
  def TSVIngestionService
  def ESWrapperService
  def grailsApplication
  def sessionFactory

  def update() {
    ref result = [:]
    result
  }

}

