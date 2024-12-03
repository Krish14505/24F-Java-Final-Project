/********************************************************************************************************
 * File:  PrivateSchool.java Course Materials CST 8277
 *
 * @author Teddy Yap
 * @author Harmeet Matharoo
 * @date December 03, 2024
 */
package acmemedical.entity;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonTypeName;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

//TODO PRSC01 - Add missing annotations, please see Week 9 slides page 15.  Value 1 is public and value 0 is private.
@Entity // PRSC01 - Marks this class as a JPA entity
@DiscriminatorValue("0") // PRSC01 - Value 0 indicates private school
//TODO PRSC02 - Is a JSON annotation needed here?
@JsonTypeName("private_school")
// PRSC02 - Provides a JSON type name for serialization
public class PrivateSchool extends MedicalSchool implements Serializable {
	private static final long serialVersionUID = 1L;

	public PrivateSchool() {
		super();

	}
}