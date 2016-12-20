<table class = "table table-condensed table-striped">
	<thead>
		<tr>
			<g:each in="${domainProperties}" var="p" status="i">
				<g:set var="propTitle">${domainClass.propertyName}.${p.name}.label</g:set>
				<g:sortableColumn property="${p.name}" title="${message(code: propTitle, default: p.naturalName)}" />
			</g:each>
		</tr>
	</thead>
	<tbody>
		<g:each in="${collection}" var="bean" status="i">
			<tr>
				<g:each in="${domainProperties}" var="p" status="j">
					<td>${bean[p.name]}</td>
				</g:each>
			</tr>
		</g:each>
	</tbody>
</table>
