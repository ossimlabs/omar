function pageLoad() {
	initializeLoadingDialog();

	disableMenuButtons();

	enableKeyboardShortcuts();

	$(window).resize(function() { updateMapSize(); });
}

$(document).ready(function() { pageLoad(); });
