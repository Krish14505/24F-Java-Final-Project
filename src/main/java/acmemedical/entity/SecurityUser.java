/********************************************************************************************************
 * File:  SecurityUser.java Course Materials CST 8277
 *
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * @author Harmeet Matharoo
 * @date December 03, 2024
 */
package acmemedical.entity;

import java.io.Serializable;
import java.security.Principal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@SuppressWarnings("unused")

/**
 * User class used for (JSR-375) Jakarta EE Security
 * authorization/authentication
 */

//TODO SU01 - Make this into JPA entity and add all the necessary annotations inside the class.
@Entity // SU01 - Marks this class as a JPA entity
@Table(name = "security_user") // Maps the class to the `security_user` table
@NamedQuery(name = "SecurityUser.userByName", query = "SELECT s FROM SecurityUser s WHERE s.username = :username")
@NamedQuery(name = "SecurityUser.findByPhysician", query = "SELECT su FROM SecurityUser su WHERE su.physician.id = :physicianId")
public class SecurityUser implements Serializable, Principal {
	/** Explicit set serialVersionUID */
	private static final long serialVersionUID = 1L;

	// TODO SU02 - Add annotations.
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id", nullable = false)
	protected int id;

	// TODO SU03 - Add annotations.
	@Column(name = "username", nullable = false, unique = true, length = 100)
	protected String username;

	// TODO SU04 - Add annotations.
	@Column(name = "password_hash", nullable = false, length = 256)
	protected String pwHash;

	// TODO SU05 - Add annotations.
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "physician_id", referencedColumnName = "id", nullable = true)
	@JsonIgnore // SU07 - Prevents physician information from being serialized into JSON
	protected Physician physician;

	// TODO SU06 - Add annotations.
	@ManyToMany(cascade = { CascadeType.MERGE, CascadeType.PERSIST })
	@JoinTable(name = "user_has_role", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "role_id"))
	protected Set<SecurityRole> roles = new HashSet<SecurityRole>();

	public SecurityUser() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPwHash() {
		return pwHash;
	}

	public void setPwHash(String pwHash) {
		this.pwHash = pwHash;
	}

	// TODO SU07 - Setup custom JSON serializer
	public Set<SecurityRole> getRoles() {
		return roles;
	}

	public void setRoles(Set<SecurityRole> roles) {
		this.roles = roles;
	}

	public Physician getPhysician() {
		return physician;
	}

	public void setPhysician(Physician physician) {
		this.physician = physician;
	}

	// Principal
	@Override
	public String getName() {
		return getUsername();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		// Only include member variables that really contribute to an object's identity
		// i.e. if variables like version/updated/name/etc. change throughout an
		// object's lifecycle,
		// they shouldn't be part of the hashCode calculation
		return prime * result + Objects.hash(getId());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (obj instanceof SecurityUser otherSecurityUser) {
			// See comment (above) in hashCode(): Compare using only member variables that
			// are
			// truly part of an object's identity
			return Objects.equals(this.getId(), otherSecurityUser.getId());
		}
		return false;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SecurityUser [id = ").append(id).append(", username = ").append(username).append("]");
		return builder.toString();
	}

}
