package time_lapse


class DocsController {


	def index() { 
		// attempt to find the adoc file
		def adocFile = new File("../docs/tlv.adoc") // if running from "grails run-app"
		if (!adocFile.exists()) { 
			def adocFileUrl = getClass().getResource("/tlv.adoc") // if running as stand alone artifact
			if (adocFileUrl) { 	
				adocFile = new File("tlv.adoc")
				adocFile.write(adocFileUrl.getText()) }
		}

		// attempt to find the html file
		def htmlFile = new File("../docs/tlv.html") // if running from "grails run-app"
		if (!htmlFile.exists()) {
			def htmlFileUrl = getClass().getResource("/tlv.html") // if running as a stand alone artifact
			if (htmlFileUrl) { 
				htmlFile = new File("tlv.html")
				htmlFile.write(htmlFileUrl.getText()) }
		}


		if (adocFile.exists()) {
			// create the corresponding html if it doesn't exist
			if (!htmlFile.exists()) { 
				def command = "asciidoctor ${adocFile.absolutePath}"
				command.execute().waitFor()
				htmlFile = new File("${adocFile.absolutePath.replaceAll(".adoc", ".html")}")
			}

			if (htmlFile.exists()) {
				response.contentType = "text/html"
				render htmlFile.getText()
			}
			else { render "Oh no! There was a problem while generating the docs." }
		}
		else if (htmlFile.exists()) {
			response.contentType = "text/html"
			render htmlFile.getText()
		}
		else { render "There doesn't appear to be any docs!" }
	}
}
