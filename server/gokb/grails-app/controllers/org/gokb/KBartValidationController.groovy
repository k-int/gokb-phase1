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

    result.globalReports = [
      filenameInfo:[:],
      overallResult:'pass',
      validRowCount:0,
      warnRowCount:0,
      errorRowCount:0,
      columnCount:0,
      kbartColumnCount:0,
      otherColumnCount:0,
      rowsWithColumnsBeyondHeader:0,
      errorRowCount:0,
      messages:[]
    ]
    result.rowReports = []

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
        result = validateKbart(result,upload_filename,temp_file);
      }
      catch ( Exception e ) {
        log.error("Problem processing validation result",e);
        result.globalReports.messages.add("Problem validating file: ${e.message}");
      }
    }
    else {
    }

    withFormat {
      html result
      json { render result as JSON }
      xml { render result as XML }
    }

  }

  private validateKbart(result,upload_filename,kbart_file) {
    log.debug("KBartValidationController::validateKbart");

    char del = '\t'
    char quote = '"'

    // Validate the upload filename
    String[] upload_filename_components = upload_filename.split('_')
    if ( upload_filename_components.length == 4 ) {
      result.globalReports.filenameInfo['providerName'] = upload_filename_components[0]
      result.globalReports.filenameInfo['region'] = upload_filename_components[1]
      result.globalReports.filenameInfo['package'] = upload_filename_components[2]
      result.globalReports.filenameInfo['datestr'] = upload_filename_components[3]
    }
    else {
      result.globalReports.messages.add("Expected 4 components in uploaded filename (Separated by _) but found ${upload_filename_components.length}. Unable to extract Provider Name / Region/Consortium / Package Name / Date");
    }

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
        proceed &= validateRow(nl, header_map, result, rowctr, file_columns);
        // if (nl.length >= col_positions.size() && cleanData (nl[col_positions.'name']) ) {
        // }
      }
      catch ( Exception e ) {
      }
      nl = r.readNext()
    }

    result;
  }

  private void validateRow(nl, header_map, result, rownum, file_columns) {
    log.debug("Validate row ${nl}");

    if ( nl.length > file_columns.size() ) {
      // Row seems to have more columns than header, probably nothing, but warn
      result.globalReports.rowsWithColumnsBeyondHeader++
    }

    def row_report = [:]
    checkRowDatatypes(row_report,nl, header_map,rownum,file_columns)

    checkRowIdentifierSemantics(row_report,nl, header_map,rownum,file_columns)

    switch( row_report.status ) {
      case 'WARN':
        result.globalReports.warnRowCount++;
        result.rowReports.add(row_report);
        break;
      case 'ERROR':
        result.globalReports.errorRowCount++;
        result.rowReports.add(row_report);
        break;
      case 'OK':
      default:
        result.globalReports.validRowCount++;
        break;
    }
  }

  private void checkRowDatatypes(row_report,nl, header_map,rownum,file_columns) {

    row_report.status = 'OK';
    row_report.messages = []
    row_report.rownum = rownum;
    row_report.errcount = 0;
    def idx = 0;
    nl.each { colval ->
      def col_name = file_columns.get(idx)
      def col_cfg = header_map[col_name]
      switch ( col_cfg.type ) {
        case 'string':
          break;
        case 'isodate':
          try {
            if ( colval.length() == 10 ) {
              def date_components = colval.split('-');
              if ( date_components.length == 3 ) {
                if ( ( date_components[0].length() == 4 ) &&
                     ( date_components[1].length() == 2 ) &&
                     ( date_components[2].length() == 2 ) ) {
                  def sdf = new java.text.SimpleDateFormat('yyyy-MM-dd')
                  def parsed_date = sdf.parse(colval)
                  log.debug("Parsed date ${colval} as ${parsed_date}");
                }
                else {
                  row_report.status = 'ERROR'
                  row_report.errcount++;
                  row_report.messages.add("expected ISO date in field ${col_name} at column position ${idx} with value ${colval} to be formatted YYYY-MM-DD");
                }
              }
              else {
                row_report.status = 'ERROR'
                row_report.errcount++;
                row_report.messages.add("expected ISO date in field ${col_name} at column position ${idx} with value ${colval} to be composed of 3 parts but found ${date_components.length}. Dates should be formatted YYYY-MM-DD");
              }
            }
            else {
              row_report.status = 'ERROR'
              row_report.errcount++;
              row_report.messages.add("expected ISO date in field ${col_name} at column position ${idx} with value ${colval} to have length 10, but it is length ${colval.length()}. Dates should be formatted YYYY-MM-DD");
            }
          }
          catch ( Exception e ) {
            row_report.status = 'ERROR'
            row_report.errcount++;
            row_report.messages.add("Unable to parse ISO date in field ${col_name} at column position ${idx} with value ${colval}. Dates should be formatted YYYY-MM-DD");
            row_report.messages.add(e.getMessage());
          }
          break;
        case 'ISO8601':
          break;
        default:
          log.debug("No type for column ${col_name}");
          break;
      }
      idx++
    }
  }

  private void checkRowIdentifierSemantics(row_report,nl, header_map,rownum,file_columns) {
    // 1. check that we can look up the identifiers - if both print and electronic are supplied, make sure that they 
    // match a common work
  
    def print_identifier_value = getValueIfPresent(nl,header_map,'print_identifier')
    def online_identifier_value = getValueIfPresent(nl,header_map,'online_identifier')

    def print_identifier = print_identifier_value ? Identifier.executeQuery('select i from Identifier as i where i.value = ?',[print_identifier_value]) : null
    def online_identifier = online_identifier_value ? Identifier.executeQuery('select i from Identifier as i where i.value = ?',[online_identifier_value]) : null

    
    if ( ( print_identifier_value != null ) && ( online_identifier_value != null ) ) {
      // Check that the print and the online identifier match
      if ( ( print_identifier.size() == 1 ) && ( online_identifier.size() == 1 )  ) {
      }
      else {
        row_report.status = 'WARN'
        row_report.messages.add("Unable to locate one or more of the supplied identifiers(${print_identifier_value} ${online_identifier_value}). The value might just be missing from GOKb, although this is unlikely");
        row_report.errcount++;
      }
    }
    else if ( print_identifier_value != null ) {
      if ( print_identifier.size() == 1 ) {
      }
      else {
        row_report.status = 'WARN'
        row_report.messages.add("Unable to locate value given for print identifier ${print_identifier_value}. The value might just be missing from GOKb, although this is unlikely");
        row_report.errcount++;
      }
    }
    else if ( online_identifier_value != null ) {
      if ( online_identifier.size() == 1 ) {
      }
      else {
        row_report.status = 'WARN'
        row_report.messages.add("Unable to locate value given for online identifier ${online_identifier_value}. The value might just be missing from GOKb, although this is unlikely");
        row_report.errcount++;
      }
    }
    else {
      row_report.status = 'ERROR'
      row_report.messages.add("Row has no usable identifier");
      row_report.errcount++;
    }

    // 2. Check that the dates match any available date range for the title identified
  }

  private def getValueIfPresent(nl,header_map,fld) {
    def result = null;
    if ( header_map[fld] != null ) {
      if ( header_map[fld].idx != null ) {
        result = nl[header_map[fld].idx]
      }
    }
    result
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
