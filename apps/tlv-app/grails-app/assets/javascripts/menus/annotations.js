function anAnnotationHasBeenAdded(event) {}

function deleteAnnotations() {
	if (getCurrentDimension() == 3) { displayErrorDialog("Annotations can only be deleted in 2D. :("); }
	else { deleteAnnotationsMap(); }
}

function drawAnnotation(type) {
	if (getCurrentDimension() == 3) { displayErrorDialog("Annotations can only be added in 2D. :("); }
	else { drawAnnotationMap(type); }
}

function modifyAnnotations() {
	if (getCurrentDimension() == 3) { displayErrorDialog("Annotations can only be modified in 2D. :("); }
	else { modifyAnnotationsMap(); }
}
