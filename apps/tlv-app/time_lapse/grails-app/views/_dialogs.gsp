<div class = "alert alert-danger" id = "errorDialog" role = "alert">
	<button aria-label = "Close" class = "close" onclick = hideErrorDialog() type = "button">
		<span aria-hidden = "true">&times;</span>
	</button>
	<div>Cheese</div>
</div>

<div class = "modal" id = "loadingDialog" role = "dialog" style = "z-index: 2147483647" tabindex = "-1">
	<div class = "modal-dialog">
		<div class = "modal-content">
			<div class = "modal-header"><h4>Please wait...</h4></div>
			<div class = "modal-body">
				<div id = "loadingDialogMessageDiv"></div>
				<br>
				<div class = "progress progress-striped">
					<div class = "progress-bar progress-bar-info" role = "progressbar" style = "width: 100%"></div>
				</div>
			</div>
		</div>
	</div>
</div>

<g:render template = "/time-lapse-dialogs"/>

<g:render template = "/menus/annotations-menu-dialogs"/>
<g:render template = "/menus/layers-menu-dialogs"/>
<g:render template = "/menus/search-menu-dialogs"/>
<g:render template = "/menus/time-lapse-menu-dialogs"/>
<g:render template = "/menus/view-menu-dialogs"/>

<g:render plugin = "networkSpecific" template = "/login-dialogs"/>
