<g:set var="ctxoid" value="${org.gokb.cred.KBComponent.deproxy(d).class.name}:${d.id}"/>

<table class="table table-striped table-bordered">
  <thead>
    <tr>
      <g:each in="${cols}" var="ch">
        <th>${ch.colhead}</th>
      </g:each>
      <th>Actions</th>
    </tr>
  </thead>
  <tbody>
    <g:each in="${d[property]}" var="row">
     <g:set var="rowoid" value="${org.gokb.cred.KBComponent.deproxy(row).class.name}:${row.id}"/>
      <tr>
        <g:each in="${cols}" var="c">
          <td>
            <g:if test="${c.action=='link'}">
              <g:link controller="resource" action="show" id="${rowoid}">${groovy.util.Eval.x(row, 'x.' + c.expr)}</g:link>
            </g:if>
            <g:else>${groovy.util.Eval.x(row, 'x.' + c.expr)}</g:else>
          </td>
        </g:each>
        <td>
          <g:link controller='ajaxSupport' 
                  action='unlinkManyToMany' 
                  params="${[__context:ctxoid,__property:property,__itemToRemove:rowoid]}">Unlink</g:link>
        </td>
      </tr>
    </g:each>
  </tbody>
</table>
