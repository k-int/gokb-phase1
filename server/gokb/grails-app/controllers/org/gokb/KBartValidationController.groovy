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

    result.globalReports = [
      overallResult:'pass',
      validRowCount:0,
      warnRowCount:0,
      errorRowCount:0,
      columnCount:0,
      kbartColumnCount:0,
      otherColumnCount:0,
      errorRowCount:0,
      messages:[]
    ]
    result.rowReports = []

    char del = '\t'
    char quote = '"'

    def r = new CSVReader( new InputStreamReader(kbart_file.newInputStream(), 'UTF-8'), del, quote )

    String [] nl = r.readNext()
    def header_map = [
      'publication_title' : [type:'string', mandatory:true],
      'print_identifier': [type:'string'],
      'online_identifier': [type:'string'],
      'date_first_issue_online': [type:'isodate'],
      'num_first_vol_online': [type:'string'],
      'num_first_issue_online': [type:'string'],
      'date_last_issue_online': [type:'isodate'],
      'num_last_vol_online': [type:'string'],
      'num_last_issue_online': [type:'string'],
      'title_url': [type:'string'],
      'first_author': [type:'string'],
      'title_id': [type:'string'],
      'embargo_info': [type:'ISO8601'],
      'coverage_depth': [type:'string', constrained:['fulltext','selected articles','abstracts']],
      'notes': [type:'string'],
      'publisher_name': [type:'string'],
      'publication_type': [type:'string', constrained:['serial','monograph']],
      'date_monograph_published_print': [type:'string'],
      'date_monograph_published_online': [type:'string'],
      'monograph_volume': [type:'string'],
      'monograph_edition': [type:'string'],
      'first_editor': [type:'string'],
      'parent_publication_title_id': [type:'string'],
      'preceding_publication_title_id': [type:'string'],
      'access_type': [type:'string', constrained:['p','f']]
    ]

    // Test # 1 - The first row must be the header and define at least the minimum set of KBART mandatory fields
    log.debug("Processing header line : ${nl}");
    log.debug("header map keys : ${header_map.keySet()}");

    int rowctr = 0
    def ret = [:]

    boolean proceed = true;
    def file_columns = []

    int header_counter = 0;
    nl.each { hdr ->
      def cleaned_column_name = hdr.toLowerCase().trim()
      log.debug("Check column: ${cleaned_column_name}");
      result.globalReports.columnCount++
      if ( header_map.keySet().contains(cleaned_column_name) ) {
        header_map[cleaned_column_name].idx = header_counter++
        result.globalReports.kbartColumnCount++
      }
      else {
        log.debug("Register unknown column ${cleaned_column_name}");
        header_map[cleaned_column_name] = [idx:header_counter++]
        result.globalReports.messages.add( [ type:'info', message:'Detected non KBART field in header: '+hdr+'('+cleaned_column_name+') (this is valid, message for information only)'] )
        result.globalReports.otherColumnCount++
      }

      // note down which mapping we are using at this column index for easy note later on
      file_columns.add(cleaned_column_name);
    }

    log.debug("header map: ${header_map}");

    // Check that header_row contains at least the mandatory columns
    def mandatory_fields = header_map.findAll { it.value.mandatory == true }.collect{it.key}
    log.debug("Check mandatory fields : ${mandatory_fields}");
    mandatory_fields.each { mandatory_column_name ->
      if ( ! header_map.keySet().contains(mandatory_column_name) ) {
        result.globalReports.messages.add( [ type:'error', message:'Missing mandatory column: '+mandatory_column_name +'(Test is case insensitive)'] )
        proceed = false;
        result.globalReports.overallResult='fail'
      }
    }

    // Read the first line of content -- it must not be a blank line. If it is - issue a warning, but try and parse the rest of the file.
    nl = r.readNext()
    if ( nl.length == 0 ) {
      result.globalReports.add( [ type:'warn', message:'The file appears to have a blank line after header line. This is incorrect. Please remove any blank line between the header and main content'] )
    }

    while ( ( nl != null) && ( proceed ) ) {
      log.debug("Processing content line : ${nl}");
      rowctr ++
      try {
        proceed &= validateRow(nl, header_map, result, rowctr);
        // if (nl.length >= col_positions.size() && cleanData (nl[col_positions.'name']) ) {
        // }
      }
      catch ( Exception e ) {
      }
      nl = r.readNext()
    }

    result;
  }

  private void validateRow(nl, header_map, result, rownum) {
    log.debug("Validate row ${nl}");
    result.globalReports.validRowCount++;
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
