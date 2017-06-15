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
        <g:form action="index" method="get">           
           <div class="input-group">
            <input type="text" name="q" id="q" class="form-control" value="${params.q}" placeholder="Search for..." />
            <span class="input-group-btn">
              <button type="submit" class="btn btn-default" >Search</button>
            </span>
          </div>
         </g:form>
      </div>

      <div class="panel-footer" >
      </div>
    </div>

  </body>
</html>
