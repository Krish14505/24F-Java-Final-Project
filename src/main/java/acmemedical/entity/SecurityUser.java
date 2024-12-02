/********************************************************************************************************
 * File:  SecurityUser.java Course Materials CST 8277
 *
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * 
 */
package acmemedical.entity;

import acmemedical.rest.serializer.SecurityRoleSerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;

import java.io.Serializable;
import java.security.Principal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@SuppressWarnings("unused")

/**
 * User class used for (JSR-375) Jakarta EE Security authorization/authentication
 */

//TODO SU01 - Make this into JPA entity and add all the necessary annotations inside the class.
@Entity
@Table(name = "security_user")
@NamedQuery(name = "SecurityUser.findAll", query = "SELECT su FROM SecurityUser su")
@NamedQuery(name = "SecurityUser.findByPhysicianId", query = "SELECT su FROM SecurityUser su WHERE su.physician.id = :physicianId")
@NamedQuery(name = "SecurityUser.userByName", query = "SELECT su FROM SecurityUser su WHERE su.username = :param1")
public class SecurityUser implements Serializable, Principal {
    /** Explicit set serialVersionUID */
    private static final long serialVersionUID = 1L;

    //TODO SU02 - Add annotations.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id",nullable = false)
    protected int id;
    
    //TODO SU03 - Add annotations.
    @Basic(optional = false)
    @Column(name = "user_name",nullable = false)
    protected String username;
    
    //TODO SU04 - Add annotations.
    @Basic(optional = false)
    @Column(name = "password_hash",nullable = false)
    protected String pwHash;
    
    //TODO SU05 - Add annotations.
    @OneToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinColumn(name = "physician_id",referencedColumnName = "id")
    @JsonManagedReference("physician-user")
    protected Physician physician;
    
    //TODO SU06 - Add annotations.
    @ManyToMany(cascade = {CascadeType.PERSIST})
    @JoinTable(name = "user_has_roles",
            joinColumns = @JoinColumn(referencedColumnName = "user_id", name = "user_id"), // This entity is SecurityUser
            inverseJoinColumns = @JoinColumn(referencedColumnName = "role_id", name = "role_id") // The other entity is SecurityRole
    )
    @JsonIgnoreProperties({"users","password"})
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
    @JsonSerialize(using= SecurityRoleSerializer.class)
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
        // i.e. if variables like version/updated/name/etc. change throughout an object's lifecycle,
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
            // See comment (above) in hashCode():  Compare using only member variables that are
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
