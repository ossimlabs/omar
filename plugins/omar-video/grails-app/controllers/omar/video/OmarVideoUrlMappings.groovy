package omar.video

class OmarVideoUrlMappings {

	static mappings = {
		"/dataManager/addVideo"(controller: 'videoDataSet', action: 'addVideo')
		"/dataManager/removeVideo"(controller: 'videoDataSet', action: 'removeVideo')
	}
}
