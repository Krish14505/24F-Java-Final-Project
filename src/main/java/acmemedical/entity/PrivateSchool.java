/********************************************************************************************************
 * File:  PrivateSchool.java Course Materials CST 8277
 *
 * @author Teddy Yap
 * 
 */
package acmemedical.entity;

import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.io.Serializable;

//TODO PRSC01 - Add missing annotations, please see Week 9 slides page 15.  Value 1 is "public" and value 0 is "private".
@Entity
@DiscriminatorValue("0") // 0 for private school
//TODO PRSC02 - Is a JSON annotation needed here?
@JsonTypeName("false")
public class PrivateSchool extends MedicalSchool implements Serializable {
	private static final long serialVersionUID = 1L;

	public PrivateSchool() {
		super(false);

	}
}