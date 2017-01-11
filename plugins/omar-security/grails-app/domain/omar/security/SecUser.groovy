package omar.security

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.access.annotation.Secured

@EqualsAndHashCode(includes='username')
@ToString(includes='username', includeNames=true, includePackage=false)
class SecUser implements Serializable {

	private static final long serialVersionUID = 1

	transient springSecurityService
	String username
	String password
	String userRealName
	String email
	boolean enabled = true
	boolean accountExpired
	boolean accountLocked
	boolean passwordExpired

	SecUser(String username, String password) {
		this.username = username
		this.password = password
	}

	Set<SecRole> getAuthorities() {
		SecUserSecRole.findAllBySecUser(this)*.secRole
	}

	UserDetails toUserDetails()
	{
		UserDetails result
		Collection<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();

		this.authorities?.each{secRole->
			grantedAuthorities.add(new SimpleGrantedAuthority(secRole.authority));
		}

		result = new UserDetails(
				username:username,
				password:password,
				enabled:enabled,
				accountNonExpired: (accountExpired!=null?!accountExpired:true),
				credentialsNonExpired:(passwordExpired!=null?!passwordExpired:true),
				accountNonLocked:(accountLocked!=null?accountLocked:false),
				authorities:grantedAuthorities,
				id:id,
				userRealName:userRealName,
				email:email)

		result
	}
	def beforeInsert() {
		encodePassword()
	}

	def beforeUpdate() {
		if (isDirty('password')) {
			encodePassword()
		}
	}
	
	protected void encodePassword() {
		password = springSecurityService?.passwordEncoder ? springSecurityService.encodePassword(password) : password
	}

	static transients = ['springSecurityService']

	static constraints = {
		username blank: false, unique: true
		password blank: false
		email nullable:true
		userRealName nullable:true
	}

	static mapping = {
      		cache true
      		id generator: 'identity'
		password column: '`password`'
		username index: 'sec_user_username_idx'
	}
}
