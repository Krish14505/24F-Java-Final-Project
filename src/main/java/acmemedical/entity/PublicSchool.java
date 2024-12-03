/********************************************************************************************************
 * File:  PublicSchool.java Course materials CST 8277
 *
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * @author Harmeet Matharoo
 * @date December 03, 2024
 */
package acmemedical.entity;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonTypeName;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

//TODO PUSC01 - Add missing annotations, please see Week 9 slides page 15.  Value 1 is public and value 0 is private.
@Entity // PUSC01 - Marks this class as a JPA entity
@DiscriminatorValue("1") // PUSC01 - Value 1 indicates public school
//TODO PUSC02 - Is a JSON annotation needed here?
@JsonTypeName("public_school")
 // PUSC02 - Provides a JSON type name for serialization
public class PublicSchool extends MedicalSchool implements Serializable {
	private static final long serialVersionUID = 1L;

	public PublicSchool() {
		super();
	}
}
