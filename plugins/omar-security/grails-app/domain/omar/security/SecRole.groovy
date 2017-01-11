package omar.security

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@EqualsAndHashCode(includes='authority')
@ToString(includes='authority', includeNames=true, includePackage=false)
class SecRole implements Serializable {

	private static final long serialVersionUID = 1

	String authority
	String description

	SecRole(String authority) {
		this.authority = authority
	}

	static constraints = {
		authority blank: false, unique: true
		description nullable:true
	}

	static mapping = {
      		cache true
      		id generator: 'identity'
		authority index: 'sec_role_authority_idx'
	}
}
