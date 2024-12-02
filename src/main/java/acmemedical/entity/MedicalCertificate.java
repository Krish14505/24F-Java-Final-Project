/********************************************************************************************************
 * File:  MedicalCertificate.java Course Materials CST 8277
 *
 * @author Teddy Yap
 * 
 */
package acmemedical.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.io.Serializable;

@SuppressWarnings("unused")

/**
 * Author: Teddy Yap
 * Modified By: Krish Chaudhary
 * The persistent class for the medical_certificate database table.
 */
//TODO MC01 - Add the missing annotations.
@Entity
//TODO MC02 - Do we need a mapped super class?  If so, which one?
@Table(name = "medical_certificate")
@NamedQuery(name="MedicalCertificate.findAll", query="SELECT mc FROM MedicalCertificate mc")
//added the NamedQuery to find the specific MedicalCertificate
@NamedQuery(name= MedicalCertificate.ID_CARD_QUERY_NAME, query="SELECT mc FROM MedicalCertificate mc WHERE mc.id = :id")
@AttributeOverride(name="id", column=@Column(name = "certificate_id"))
public class MedicalCertificate extends PojoBase implements Serializable {
	private static final long serialVersionUID = 1L;

	//add the variable that reference to the MedicalService class
	public static final String ID_CARD_QUERY_NAME = "MedicalCertificate.findByIdCard";

	// TODO MC03 - Add annotations for 1:1 mapping.  What should be the cascade and fetch types?
	@JsonManagedReference("certificate=training")
	@OneToOne
	@JoinColumn(name = "training_id")
	private MedicalTraining medicalTraining;

	// TODO MC04 - Add annotations for M:1 mapping.  What should be the cascade and fetch types?
	@ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
	@JoinColumn(name = "physician_id")
	private Physician physician;

	// TODO MC05 - Add annotations.
	@Basic
	@Column(name = "signed")
	private byte signed;

	public MedicalCertificate() {
		super();
	}
	
	public MedicalCertificate(MedicalTraining medicalTraining, Physician owner, byte signed) {
		this();
		this.medicalTraining = medicalTraining;
		this.physician = owner;
		this.signed = signed;
	}

	public MedicalTraining getMedicalTraining() {
		return medicalTraining;
	}

	public void setMedicalTraining(MedicalTraining medicalTraining) {
		this.medicalTraining = medicalTraining;
	}

	public Physician getOwner() {
		return physician;
	}

	public void setOwner(Physician owner) {
		this.physician = owner;
	}

	public byte getSigned() {
		return signed;
	}

	public void setSigned(byte signed) {
		this.signed = signed;
	}

	public void setSigned(boolean signed) {
		this.signed = (byte) (signed ? 0b0001 : 0b0000);
	}
	
	//Inherited hashCode/equals is sufficient for this entity class

}