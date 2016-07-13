<!DOCTYPE html>
<html>
<head>
<meta name="layout" content="sb-admin" />
<title>GOKb: Configuration</title>
</head>
<body>
  <h1 class="page-header">Configuration</h1>
  <table class="table table-bordered">
    <thead>
      <tr>
        <th>Key</th>
        <th>Value</th>
      </tr>
    </thead>
    <tbody>
      <g:each in="${config}" var="k,v">
        <tr>
          <td>${k}</td>
          <td>${v}</td>
        </tr>
      </g:each>
    </tbody>
  </table>
</body>
</html>
