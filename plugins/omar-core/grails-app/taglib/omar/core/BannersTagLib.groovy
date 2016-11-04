package omar.core
import org.springframework.beans.factory.annotation.*
import groovy.xml.StreamingMarkupBuilder


class BannersTagLib {
    static namespace = "o2"
    static defaultEncodeAs = [taglib:'raw']
    //static encodeAsForTags = [tagName: [taglib:'html'], otherTagName: [taglib:'none']]

 	@Value('${classificationBanner.backgroundColor}')
    def backgroundColor

    @Value('${classificationBanner.classificationType}')
    def classificationType

    def classificationBanner = { attrs, body ->

    	// out << "<div class='navbar navbar-default navbar-fixed-${attrs.position ?: 'top'} text-center' style='background-color: ${backgroundColor};'><p style='font-size: 19px'>${classificationType}</p></div>"

    	def x = {
    		div ("class": "navbar navbar-default navbar-fixed-${attrs.position ?: 'top'} text-center",
    			 style: "background: ${backgroundColor};")
    		{

    			p (style: 'margin-top: 3px; text-size: 19px; color: black; text-shadow: 0px .5px .5px #fff;') {
    				strong(classificationType)
    			}
    		}
    	}


    	def text = new StreamingMarkupBuilder().bind(x).toString()
    	out << text
    }
}
