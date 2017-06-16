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

  <pre>
    ${globalReports}
    ${rowReports}
  </pre>

  </body>
</html>
