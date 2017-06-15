package org.gokb

import grails.converters.*
import org.codehaus.groovy.grails.commons.GrailsClassUtils
import org.gokb.cred.*
import grails.plugin.gson.converters.GSON
import au.com.bytecode.opencsv.CSVReader


class KBartValidationController {

  def genericOIDService

  def index() {
    log.debug("KBartValidationController::validate");
  }

  def validate() {

    def result = [:];

    log.debug("KBartValidationController::validate");

    if ( request.method == 'POST' ) {
      try {
        def temp_file
        def upload_file = request.getFile("kbart_file")
        def upload_mime_type = upload_file?.contentType
        def upload_filename = upload_file?.getOriginalFilename()
        def name_of_kbart_file_to_validate = java.util.UUID.randomUUID().toString();
        temp_file = copyUploadedFile(upload_file, name_of_kbart_file_to_validate);

        // Validate kbart temp_file
        result = validateKbart(temp_file);
      }
      catch ( Exception e ) {
        log.debug("Problem",e);
      }
    }
    else {
    }
  }

  private validateKbart(kbart_file) {
    def result = [:]
    log.debug("KBartValidationController::validateKbart");

    result.globalReports = []
    result.rowReports = []

    char del = '\t'
    char quote = '"'

    def r = new CSVReader( new InputStreamReader(kbart_file.newInputStream(), 'UTF-8'), del, quote )

    String [] nl = r.readNext()

    // Test # 1 - The first row must be the header and define at least the minimum set of KBART mandatory fields
    log.debug("Processing header line : ${nl}");

    int rowctr = 0
    def ret = [:]

    // Read the first line of content -- it must not be a blank line. If it is - issue a warning, but try and parse the rest of the file.
    nl = r.readNext()
    if ( nl.length == 0 ) {
      result.globalReports.add( [ type:'warn', message:'The file appears to have a blank line after header line. This is incorrect. Please remove any blank line between the header and main content'] )
    }


    while ( nl != null) {
      log.debug("Processing content line : ${nl}");
      rowctr ++
      try {
        // if (nl.length >= col_positions.size() && cleanData (nl[col_positions.'name']) ) {
        // }
      }
      catch ( Exception e ) {
      }
      nl = r.readNext()
    }

    result;
  }

  private File copyUploadedFile(inputfile, deposit_token) {
    def baseUploadDir = grailsApplication.config.project_dir ?: '.'
    log.debug("copyUploadedFile...");
    def sub1 = deposit_token.substring(0,2);
    def sub2 = deposit_token.substring(2,4);
    validateUploadDir("${baseUploadDir}");
    validateUploadDir("${baseUploadDir}/${sub1}");
    validateUploadDir("${baseUploadDir}/${sub1}/${sub2}");
    def temp_file_name = "${baseUploadDir}/${sub1}/${sub2}/${deposit_token}";
    def temp_file = new File(temp_file_name);

    // Copy the upload file to a temporary space
    inputfile.transferTo(temp_file);

    temp_file
  }

  private def validateUploadDir(path) {
    File f = new File(path);
    if ( ! f.exists() ) {
      log.debug("Creating upload directory path")
      f.mkdirs();
    }
  }

}
