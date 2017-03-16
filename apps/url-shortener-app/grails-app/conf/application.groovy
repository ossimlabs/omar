grails.gorm.default.mapping = {
	cache true
	id generator: 'identity'
}

// Grails-url-shortener
shortener {
    characters = ('0'..'9') + ('a'..'h') + ('j'..'k') + ('m'..'z') + ('A'..'H') + ('J'..'K') + ('M'..'Z')
    minLength = 5
    shortDomain = http://rad.bl.us
}
