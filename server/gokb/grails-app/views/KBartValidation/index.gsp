<!DOCTYPE html>

<html>
  <head>
    <meta name="layout" content="sb-admin"/>
    <title>GOKb: KBART Validation Service</title>
  </head>
  <body>
  
    <h1 class="page-header">KBART Validation Endpoint</h1>

    <div id="mainarea" class="panel panel-default">
      <div class="panel-body">
        <g:form action="validate" method="post" enctype="multipart/form-data">
           <div class="input-group">
            <input type="file" name="kbart_file" id="kbart_file" class="form-control" value="${params.kbart_file}" placeholder="Upload KBART File..." />
            <span class="input-group-btn">
              <button type="submit" class="btn btn-default" >Validate</button>
            </span>
          </div>
         </g:form>
      </div>

      <div class="panel-footer" >
      </div>
    </div>

    <div class="row">
      <h2>This tool will validate an uploaded KBART file according to the following rules</h2>
      <p>
        This tool was developed against the KBART guidelines as set out here: http://www.niso.org/apps/group_public/download.php/16900/RP-9-2014_KBART.pdf
      </p>
      <table class="table table-striped">
        <thead>
          <tr>
            <th>Rule code</th>
            <th>Rule type</th>
            <th>Description</th>
          </tr>
        </thead>
        <tbody>

          <tr>
            <td>FILENAME</td>
            <td>SHOULD</td>
            <td>[KBART 3.4.1]/[KBART 6.5.1] The uploaded file should be named in accordance with KBART guidance 3.4.1 - [ProviderName]_[Region/Consortium]_[PackageName]_[YYYY-MM-DD].txt</td>
          </tr>

          <tr>
            <td>UTF8</td>
            <td>SHOULD</td>
            <td>[KBART 6.4.3] The uploaded file should use UTF8 encoding. If the file can be coerced into UTF8 validation may still pass and a warning issues. If the file cannot
                be coerced, validation will fail</td>
          </tr>

          <tr>
            <td>STRUCTURE</td>
            <td>MUST</td>
            <td>The uploaded file must be a syntactically correct TSV file.</td>
          </tr>

          <tr>
            <td>HEADER</td>
            <td>MUST</td>
            <td>[KBART 6.4.5] The uploaded file must contain a header row which at least defines the mandatory columns</td>
          </tr>

          <tr>
            <td>IDENTIFIERS</td>
            <td>SHOULD</td>
            <td>[KBART 6.4.7] identifier columns should conform to relevant standards<br/>
              <ul>
                <li>ISxN </li>
              </ul>
            </td>
          </tr>

        </tbody>
      </table>
    </div>

  </body>
</html>
