package omar.security

import grails.gorm.DetachedCriteria
import groovy.transform.ToString

import org.apache.commons.lang.builder.HashCodeBuilder

@ToString(cache=true, includeNames=true, includePackage=false)
class SecUserSecRole implements Serializable {

	private static final long serialVersionUID = 1

	SecUser secUser
	SecRole secRole

	SecUserSecRole(SecUser u, SecRole r) {
		secUser = u
		secRole = r
	}

	@Override
	boolean equals(other) {
		if (!(other instanceof SecUserSecRole)) {
			return false
		}

		other.secUser?.id == secUser?.id && other.secRole?.id == secRole?.id
	}

	@Override
	int hashCode() {
		def builder = new HashCodeBuilder()
		if (secUser) builder.append(secUser.id)
		if (secRole) builder.append(secRole.id)
		builder.toHashCode()
	}

	static SecUserSecRole get(long secUserId, long secRoleId) {
		criteriaFor(secUserId, secRoleId).get()
	}

	static boolean exists(long secUserId, long secRoleId) {
		criteriaFor(secUserId, secRoleId).count()
	}

	private static DetachedCriteria criteriaFor(long secUserId, long secRoleId) {
		SecUserSecRole.where {
			secUser == SecUser.load(secUserId) &&
			secRole == SecRole.load(secRoleId)
		}
	}

	static SecUserSecRole create(SecUser secUser, SecRole secRole, boolean flush = false) {
		def instance = new SecUserSecRole(secUser: secUser, secRole: secRole)
		instance.save(flush: flush, insert: true)
		instance
	}

	static boolean remove(SecUser u, SecRole r, boolean flush = false) {
		if (u == null || r == null) return false

		int rowCount = SecUserSecRole.where { secUser == u && secRole == r }.deleteAll()

		if (flush) { SecUserSecRole.withSession { it.flush() } }

		rowCount
	}

	static void removeAll(SecUser u, boolean flush = false) {
		if (u == null) return

		SecUserSecRole.where { secUser == u }.deleteAll()

		if (flush) { SecUserSecRole.withSession { it.flush() } }
	}

	static void removeAll(SecRole r, boolean flush = false) {
		if (r == null) return

		SecUserSecRole.where { secRole == r }.deleteAll()

		if (flush) { SecUserSecRole.withSession { it.flush() } }
	}

	static constraints = {
		secRole validator: { SecRole r, SecUserSecRole ur ->
			if (ur.secUser == null || ur.secUser.id == null) return
			boolean existing = false
			SecUserSecRole.withNewSession {
				existing = SecUserSecRole.exists(ur.secUser.id, r.id)
			}
			if (existing) {
				return 'userRole.exists'
			}
		}
	}

	static mapping = {
		id composite: ['secUser', 'secRole']
		version false
	}
}
