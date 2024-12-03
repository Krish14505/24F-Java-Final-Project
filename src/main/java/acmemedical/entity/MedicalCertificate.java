/********************************************************************************************************
 * File:  MedicalCertificate.java Course Materials CST 8277
 *
 * @author Teddy Yap
 * @author Harmeet Matharoo
 * @date December 03, 2024
 */
package acmemedical.entity;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonBackReference;

import acmemedical.utility.MyConstants;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@SuppressWarnings("unused")

/**
 * The persistent class for the medical_certificate database table.
 */
//TODO MC01 - Add the missing annotations.
@Entity
//TODO MC02 - Do we need a mapped super class?  If so, which one?
@Table(name = "medical_certificate") // Maps the entity to the database table
@AttributeOverride(name = "id", column = @Column(name = "certificate_id"))
@NamedQuery(name = MedicalCertificate.ID_CARD_QUERY_NAME, query = "SELECT mc FROM MedicalCertificate mc WHERE mc.id = :param1")
@NamedQuery(name = MedicalCertificate.ALL_MEDICAL_CERTIFICATES_QUERY_NAME, query = "SELECT mc FROM MedicalCertificate mc")
public class MedicalCertificate extends PojoBase implements Serializable {
	private static final long serialVersionUID = 1L;

    public static final String ID_CARD_QUERY_NAME = "MedicalCertificate.findById";
    public static final String ALL_MEDICAL_CERTIFICATES_QUERY_NAME = "MedicalCertificate.findAll";

	// TODO MC03 - Add annotations for 1:1 mapping. What should be the cascade and
	// fetch types?
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "training_id", nullable = true, insertable = false, updatable = false)
    @JsonBackReference // Prevents infinite recursion with MedicalTraining
    private MedicalTraining medicalTraining;


	// TODO MC04 - Add annotations for M:1 mapping. What should be the cascade and
	// fetch types?
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "physician_id", nullable = false)
    private Physician owner;

	// TODO MC05 - Add annotations.
	@Column(name = "signed")
	private boolean signed;

	public MedicalCertificate() {
		super();
	}

	public MedicalCertificate(MedicalTraining medicalTraining, Physician owner, boolean signed) {
		this();
		this.medicalTraining = medicalTraining;
		this.owner = owner;
		this.signed = signed;
	}

	public MedicalTraining getMedicalTraining() {
		return medicalTraining;
	}

	public void setMedicalTraining(MedicalTraining medicalTraining) {
		this.medicalTraining = medicalTraining;
	}

	public Physician getOwner() {
		return owner;
	}

	public void setOwner(Physician owner) {
		this.owner = owner;
	}

	public boolean getSigned() {
		return signed;
	}

//	public void setSigned(byte signed) {
//		this.signed = signed;
//	}

	public boolean isSigned() {
		return signed;
	}

	public void setSigned(boolean signed) {
		this.signed = signed;
	}

//	public void setSigned(boolean signed) {
//		this.signed = (byte) (signed ? 0b0001 : 0b0000);
//	}

	// Inherited hashCode/equals is sufficient for this entity class

}