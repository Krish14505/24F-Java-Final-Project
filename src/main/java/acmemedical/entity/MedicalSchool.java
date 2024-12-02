/********************************************************************************************************
 * File:  MedicalSchool.java Course Materials CST 8277
 *
 * @author Teddy Yap
 * 
 */
package acmemedical.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * The persistent class for the medical_school database table.
 */
//TODO MS01 - Add the missing annotations.
	@Entity
	@Table(name = "medical_school")

//TODO MS02 - MedicalSchool has subclasses PublicSchool and PrivateSchool.  Look at Week 9 slides for InheritanceType.
	@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
//TODO MS03 - Do we need a mapped super class?  If so, which one?
	@DiscriminatorColumn(name = "public",discriminatorType = DiscriminatorType.INTEGER)
	// Add the NamedQuery to resolve the issue of isDuplicate() method in service class
			@NamedQuery(name= MedicalSchool.IS_DUPLICATE_QUERY_NAME, query = "SELECT COUNT(ms) FROM MedicalSchool ms WHERE lower(ms.name) = lower(:param1) ")
			@NamedQuery(name= MedicalSchool.SPECIFIC_MEDICAL_SCHOOL_QUERY_NAME, query = "SELECT ms FROM MedicalSchool ms  WHERE ms.id = :id")
			@NamedQuery(name= MedicalSchool.ALL_MEDICAL_SCHOOLS_QUERY_NAME, query="SELECT distinct ms FROM MedicalSchool ms")


//TODO MS04 - Add in JSON annotations to indicate different sub-classes of MedicalSchool
	@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY,property = "isPublic")
	@JsonSubTypes( {
			@JsonSubTypes.Type(value=PublicSchool.class, name = "true"),
			@JsonSubTypes.Type(value= PrivateSchool.class, name = "false")
	})
	@AttributeOverride(name="id",column= @Column(name = "school_id"))
public abstract class MedicalSchool extends PojoBase implements Serializable {
	private static final long serialVersionUID = 1L;


	//Variable store the name of the query
	public static final String IS_DUPLICATE_QUERY_NAME = "MedicalSchool.isDuplicate";

	//variable store the name of the query
	public static final String SPECIFIC_MEDICAL_SCHOOL_QUERY_NAME = "MedicalSchool.findById";

	//variable representing all the medical school
	public static final String 	ALL_MEDICAL_SCHOOLS_QUERY_NAME = "MedicalSchool.findAll";

	// TODO MS05 - Add the missing annotations.
	@Basic(optional=false)
	@Column(name = "name",nullable = false)
	private String name;

	// TODO MS06 - Add the 1:M annotation.  What should be the cascade and fetch types?
	@OneToMany(mappedBy = "school", cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
	@JsonIgnore
	private Set<MedicalTraining> medicalTrainings = new HashSet<>();

	// TODO MS07 - Add missing annotation.
	@Column(name = "public", insertable=false, updatable=false)
	private boolean isPublic;

	public MedicalSchool() {
		super();
	}

    public MedicalSchool(boolean isPublic) {
        this();
        this.isPublic = isPublic;
    }

	// TODO MS08 - Is an annotation needed here?
	//No we don't need the annotation here because it's already mentioned in the field.
	public Set<MedicalTraining> getMedicalTrainings() {
		return medicalTrainings;
	}

	public void setMedicalTrainings(Set<MedicalTraining> medicalTrainings) {
		this.medicalTrainings = medicalTrainings;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	//Inherited hashCode/equals is NOT sufficient for this entity class
	
	/**
	 * Very important:  Use getter's for member variables because JPA sometimes needs to intercept those calls<br/>
	 * and go to the database to retrieve the value
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		// Only include member variables that really contribute to an object's identity
		// i.e. if variables like version/updated/name/etc. change throughout an object's lifecycle,
		// they shouldn't be part of the hashCode calculation
		
		// The database schema for the MEDICAL_SCHOOL table has a UNIQUE constraint for the NAME column,
		// so we should include that in the hash/equals calculations
		
		return prime * result + Objects.hash(getId(), getName());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		
		if (obj instanceof MedicalSchool otherMedicalSchool) {
			// See comment (above) in hashCode():  Compare using only member variables that are
			// truly part of an object's identity
			return Objects.equals(this.getId(), otherMedicalSchool.getId()) &&
				Objects.equals(this.getName(), otherMedicalSchool.getName());
		}
		return false;
	}
}
