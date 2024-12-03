/********************************************************************************************************
 * File:  ACMEMedicalService.java Course Materials CST 8277
 *
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * @author Harmeet Matharoo
 * @date December 03, 2024
 */
package acmemedical.ejb;

import static acmemedical.utility.MyConstants.DEFAULT_KEY_SIZE;
import static acmemedical.utility.MyConstants.DEFAULT_PROPERTY_ALGORITHM;
import static acmemedical.utility.MyConstants.DEFAULT_PROPERTY_ITERATIONS;
import static acmemedical.utility.MyConstants.DEFAULT_SALT_SIZE;
import static acmemedical.utility.MyConstants.DEFAULT_USER_PASSWORD;
import static acmemedical.utility.MyConstants.DEFAULT_USER_PREFIX;
import static acmemedical.utility.MyConstants.PARAM1;
import static acmemedical.utility.MyConstants.PROPERTY_ALGORITHM;
import static acmemedical.utility.MyConstants.PROPERTY_ITERATIONS;
import static acmemedical.utility.MyConstants.PROPERTY_KEY_SIZE;
import static acmemedical.utility.MyConstants.PROPERTY_SALT_SIZE;
import static acmemedical.utility.MyConstants.PU_NAME;
import static acmemedical.utility.MyConstants.USER_ROLE;
import static acmemedical.entity.Physician.ALL_PHYSICIANS_QUERY_NAME;
import static acmemedical.entity.MedicalCertificate.ID_CARD_QUERY_NAME;
import static acmemedical.entity.MedicalTraining.FIND_BY_ID;
import static acmemedical.entity.MedicalSchool.ALL_MEDICAL_SCHOOLS_QUERY_NAME;
import static acmemedical.entity.MedicalSchool.IS_DUPLICATE_QUERY_NAME;
import static acmemedical.entity.MedicalSchool.SPECIFIC_MEDICAL_SCHOOL_QUERY_NAME;


import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.ejb.Singleton;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.security.enterprise.identitystore.Pbkdf2PasswordHash;
import jakarta.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import acmemedical.entity.MedicalTraining;
import acmemedical.entity.Patient;
import acmemedical.entity.MedicalCertificate;
import acmemedical.entity.Medicine;
import acmemedical.entity.Prescription;
import acmemedical.entity.PrescriptionPK;
import acmemedical.entity.SecurityRole;
import acmemedical.entity.SecurityUser;
import acmemedical.entity.Physician;
import acmemedical.entity.MedicalSchool;

@SuppressWarnings("unused")

/**
 * Stateless Singleton EJB Bean - ACMEMedicalService
 */
@Singleton
public class ACMEMedicalService implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private static final Logger LOG = LogManager.getLogger();
    
    @PersistenceContext(name = PU_NAME)
    protected EntityManager em;
    
    @Inject
    protected Pbkdf2PasswordHash pbAndjPasswordHash;

    public List<Physician> getAllPhysicians() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Physician> cq = cb.createQuery(Physician.class);
        cq.select(cq.from(Physician.class));
        return em.createQuery(cq).getResultList();
    }

    public Physician getPhysicianById(int id) {
        return em.find(Physician.class, id);
    }

    @Transactional
    public Physician persistPhysician(Physician newPhysician) {
        em.persist(newPhysician);
        return newPhysician;
    }

    @Transactional
    public void buildUserForNewPhysician(Physician newPhysician) {
        // Ensure the Physician entity is managed
        if (!em.contains(newPhysician)) {
            newPhysician = em.merge(newPhysician);
        }

        SecurityUser userForNewPhysician = new SecurityUser();
        userForNewPhysician.setUsername(
            DEFAULT_USER_PREFIX + "_" + newPhysician.getFirstName() + "." + newPhysician.getLastName()
        );

        Map<String, String> pbAndjProperties = new HashMap<>();
        pbAndjProperties.put(PROPERTY_ALGORITHM, DEFAULT_PROPERTY_ALGORITHM);
        pbAndjProperties.put(PROPERTY_ITERATIONS, DEFAULT_PROPERTY_ITERATIONS);
        pbAndjProperties.put(PROPERTY_SALT_SIZE, DEFAULT_SALT_SIZE);
        pbAndjProperties.put(PROPERTY_KEY_SIZE, DEFAULT_KEY_SIZE);
        pbAndjPasswordHash.initialize(pbAndjProperties);

        String pwHash = pbAndjPasswordHash.generate(DEFAULT_USER_PASSWORD.toCharArray());
        userForNewPhysician.setPwHash(pwHash);
        userForNewPhysician.setPhysician(newPhysician);

        /* TODO ACMECS01 - Use NamedQuery on SecurityRole to find USER_ROLE */
        // Fetch USER_ROLE using a NamedQuery
        TypedQuery<SecurityRole> query = em.createNamedQuery("SecurityRole.findByName", SecurityRole.class);
        query.setParameter("name", USER_ROLE);
        SecurityRole userRole = query.getSingleResult();

        userForNewPhysician.getRoles().add(userRole);
        userRole.getUsers().add(userForNewPhysician);

        em.persist(userForNewPhysician);
    }


    @Transactional
    public Medicine setMedicineForPhysicianPatient(int physicianId, int patientId, Medicine newMedicine) {
        Physician physicianToBeUpdated = em.find(Physician.class, physicianId);
        if (physicianToBeUpdated != null) { // Physician exists
            Set<Prescription> prescriptions = physicianToBeUpdated.getPrescriptions();
            prescriptions.forEach(p -> {
                if (p.getPatient().getId() == patientId) {
                    if (p.getMedicine() != null) { // Medicine exists
                        Medicine medicine = em.find(Medicine.class, p.getMedicine().getId());
                        medicine.setMedicine(newMedicine.getDrugName(),
                        				  newMedicine.getManufacturerName(),
                        				  newMedicine.getDosageInformation());
                        em.merge(medicine);
                    }
                    else { // Medicine does not exist
                        p.setMedicine(newMedicine);
                        em.merge(physicianToBeUpdated);
                    }
                }
            });
            return newMedicine;
        }
        else return null;  // Physician doesn't exists
    }

    /**
     * To update a physician
     * 
     * @param id - id of entity to update
     * @param physicianWithUpdates - entity with updated information
     * @return Entity with updated information
     */
    @Transactional
    public Physician updatePhysicianById(int id, Physician physicianWithUpdates) {
    	Physician physicianToBeUpdated = getPhysicianById(id);
        if (physicianToBeUpdated != null) {
            em.refresh(physicianToBeUpdated);
            em.merge(physicianWithUpdates);
            em.flush();
        }
        return physicianToBeUpdated;
    }

    /**
     * To delete a physician by id
     * 
     * @param id - physician id to delete
     */
    @Transactional
    public void deletePhysicianById(int id) {
        Physician physician = getPhysicianById(id);
        if (physician != null) {
            em.refresh(physician);
            
            /* TODO ACMECS02 - Use NamedQuery on SecurityRole to find this related Student
               so that when we remove it, the relationship from SECURITY_USER table
               is not dangling
            */
            
            try {
                TypedQuery<SecurityUser> query = em.createNamedQuery("SecurityUser.findByPhysician", SecurityUser.class);
                query.setParameter("physicianId", id);
                SecurityUser sUser = query.getSingleResult();
                sUser.getRoles().clear(); // Remove associations with roles
                em.remove(sUser);
            } catch (NoResultException e) {
                // No associated SecurityUser found; proceed to remove physician
            }

            em.remove(physician);
        }
    }
    
    public List<MedicalSchool> getAllMedicalSchools() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<MedicalSchool> cq = cb.createQuery(MedicalSchool.class);
        cq.select(cq.from(MedicalSchool.class));
        return em.createQuery(cq).getResultList();
    }

    // Why not use the build-in em.find?  The named query SPECIFIC_MEDICAL_SCHOOL_QUERY_NAME
    // includes JOIN FETCH that we cannot add to the above API
    public MedicalSchool getMedicalSchoolById(int id) {
        TypedQuery<MedicalSchool> specificMedicalSchoolQuery = em.createNamedQuery(SPECIFIC_MEDICAL_SCHOOL_QUERY_NAME, MedicalSchool.class);
        specificMedicalSchoolQuery.setParameter(PARAM1, id);
        return specificMedicalSchoolQuery.getSingleResult();
    }
    
    // These methods are more generic.

    public <T> List<T> getAll(Class<T> entity, String namedQuery) {
        TypedQuery<T> allQuery = em.createNamedQuery(namedQuery, entity);
        return allQuery.getResultList();
    }
    
    public <T> T getById(Class<T> entity, String namedQuery, int id) {
        TypedQuery<T> allQuery = em.createNamedQuery(namedQuery, entity);
        allQuery.setParameter(PARAM1, id);
        try {
            return allQuery.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }


    @Transactional
    public MedicalSchool deleteMedicalSchool(int id) {
        //MedicalSchool ms = getMedicalSchoolById(id);
        MedicalSchool ms = getById(MedicalSchool.class, MedicalSchool.SPECIFIC_MEDICAL_SCHOOL_QUERY_NAME, id);
        if (ms != null) {
            Set<MedicalTraining> medicalTrainings = ms.getMedicalTrainings();
            List<MedicalTraining> list = new LinkedList<>();
            medicalTrainings.forEach(list::add);
            list.forEach(mt -> {
                if (mt.getCertificate() != null) {
                    MedicalCertificate mc = getById(MedicalCertificate.class, MedicalCertificate.ID_CARD_QUERY_NAME, mt.getCertificate().getId());
                    mc.setMedicalTraining(null);
                }
                mt.setCertificate(null);
                em.merge(mt);
            });
            em.remove(ms);
            return ms;
        } else {
            // Medical school not found
            return null;
        }
    }
    
    // Please study & use the methods below in your test suites
    
    public boolean isDuplicated(MedicalSchool newMedicalSchool) {
        TypedQuery<Long> allMedicalSchoolsQuery = em.createNamedQuery(IS_DUPLICATE_QUERY_NAME, Long.class);
        allMedicalSchoolsQuery.setParameter(PARAM1, newMedicalSchool.getName());
        return (allMedicalSchoolsQuery.getSingleResult() >= 1);
    }

    @Transactional
    public MedicalSchool persistMedicalSchool(MedicalSchool newMedicalSchool) {
        em.persist(newMedicalSchool);
        return newMedicalSchool;
    }

    @Transactional
    public MedicalSchool updateMedicalSchool(int id, MedicalSchool updatingMedicalSchool) {
    	MedicalSchool medicalSchoolToBeUpdated = getMedicalSchoolById(id);
        if (medicalSchoolToBeUpdated != null) {
            em.refresh(medicalSchoolToBeUpdated);
            medicalSchoolToBeUpdated.setName(updatingMedicalSchool.getName());
            em.merge(medicalSchoolToBeUpdated);
            em.flush();
        }
        return medicalSchoolToBeUpdated;
    }
    
    @Transactional
    public MedicalTraining persistMedicalTraining(MedicalTraining newMedicalTraining) {
        em.persist(newMedicalTraining);
        return newMedicalTraining;
    }
    
    public MedicalTraining getMedicalTrainingById(int mtId) {
        TypedQuery<MedicalTraining> query = em.createNamedQuery(MedicalTraining.FIND_BY_ID, MedicalTraining.class);
        query.setParameter(PARAM1, mtId);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }


    @Transactional
    public MedicalTraining updateMedicalTraining(int id, MedicalTraining medicalTrainingWithUpdates) {
        MedicalTraining medicalTrainingToBeUpdated = getMedicalTrainingById(id);
        if (medicalTrainingToBeUpdated != null) {
            em.refresh(medicalTrainingToBeUpdated);

            // Update fields
            medicalTrainingToBeUpdated.setDurationAndStatus(medicalTrainingWithUpdates.getDurationAndStatus());

            // Update MedicalSchool if provided
            if (medicalTrainingWithUpdates.getSchool() != null && medicalTrainingWithUpdates.getSchool().getId() != 0) {
                MedicalSchool school = getMedicalSchoolById(medicalTrainingWithUpdates.getSchool().getId());
                if (school != null) {
                    medicalTrainingToBeUpdated.setSchool(school);
                }
            }

            em.merge(medicalTrainingToBeUpdated);
            em.flush();
        }
        return medicalTrainingToBeUpdated;
    }

    
    public List<MedicalTraining> getAllMedicalTrainings() {
        TypedQuery<MedicalTraining> query = em.createNamedQuery(MedicalTraining.ALL_MEDICAL_TRAININGS_QUERY_NAME, MedicalTraining.class);
        return query.getResultList();
    }
    
    @Transactional
    public MedicalTraining deleteMedicalTraining(int id) {
        MedicalTraining mt = getMedicalTrainingById(id);
        if (mt != null) {
            em.remove(mt);
            return mt;
        }
        return null;
    }


    public Patient getPatientById(int id) {
        return em.find(Patient.class, id);
    }

    public List<Patient> getAllPatients() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Patient> cq = cb.createQuery(Patient.class);
        cq.select(cq.from(Patient.class));
        List<Patient> patients = em.createQuery(cq).getResultList();
        return patients;
    }
    
    @Transactional
    public Patient persistPatient(Patient newPatient) {
        em.persist(newPatient);
        return newPatient;
    }

    @Transactional
    public void deletePatientById(int id) {
        Patient patient = em.find(Patient.class, id);
        if (patient != null) {
            em.remove(patient);
        }
    }

    /**
     * To update a patient
     * 
     * @param id - id of entity to update
     * @param patientWithUpdates - entity with updated information
     * @return Entity with updated information
     */
    @Transactional
    public Patient updatePatientById(int id, Patient patientWithUpdates) {
        Patient patientToBeUpdated = getPatientById(id);
        if (patientToBeUpdated != null) {
            em.refresh(patientToBeUpdated);
            
            // Update only non-null fields to allow partial updates
            if (patientWithUpdates.getFirstName() != null) {
                patientToBeUpdated.setFirstName(patientWithUpdates.getFirstName());
            }
            if (patientWithUpdates.getLastName() != null) {
                patientToBeUpdated.setLastName(patientWithUpdates.getLastName());
            }
            if (patientWithUpdates.getYear() != 0) {
                patientToBeUpdated.setYear(patientWithUpdates.getYear());
            }
            if (patientWithUpdates.getAddress() != null) {
                patientToBeUpdated.setAddress(patientWithUpdates.getAddress());
            }
            if (patientWithUpdates.getHeight() != 0) {
                patientToBeUpdated.setHeight(patientWithUpdates.getHeight());
            }
            if (patientWithUpdates.getWeight() != 0) {
                patientToBeUpdated.setWeight(patientWithUpdates.getWeight());
            }
            // Assuming 'smoker' is a byte, and '0' could be a valid value. Adjust as needed.
            patientToBeUpdated.setSmoker(patientWithUpdates.getSmoker());
            
            em.merge(patientToBeUpdated);
            em.flush();
        }
        return patientToBeUpdated;
    }


    
    public List<Medicine> getAllMedicines() {
        TypedQuery<Medicine> query = em.createNamedQuery("Medicine.findAll", Medicine.class);
        return query.getResultList();
    }

    public Medicine getMedicineById(int id) {
        return em.find(Medicine.class, id);
    }
    
    @Transactional
    public Medicine persistMedicine(Medicine newMedicine) {
        em.persist(newMedicine);
        return newMedicine;
    }

    @Transactional
    public void deleteMedicineById(int id) {
        Medicine medicine = em.find(Medicine.class, id);
        if (medicine != null) {
            em.remove(medicine);
        }
    }
    
    /**
     * To update a medicine
     * 
     * @param id - id of the entity to update
     * @param medicineWithUpdates - entity with updated information
     * @return Entity with updated information
     */
    @Transactional
    public Medicine updateMedicineById(int id, Medicine medicineWithUpdates) {
        Medicine medicineToBeUpdated = getMedicineById(id);
        if (medicineToBeUpdated != null) {
            em.refresh(medicineToBeUpdated);
            
            // Update only non-null fields to allow partial updates
            if (medicineWithUpdates.getDrugName() != null) {
                medicineToBeUpdated.setDrugName(medicineWithUpdates.getDrugName());
            }
            if (medicineWithUpdates.getManufacturerName() != null) {
                medicineToBeUpdated.setManufacturerName(medicineWithUpdates.getManufacturerName());
            }
            if (medicineWithUpdates.getDosageInformation() != null) {
                medicineToBeUpdated.setDosageInformation(medicineWithUpdates.getDosageInformation());
            }
            if (medicineWithUpdates.getGenericName() != null) {
                medicineToBeUpdated.setGenericName(medicineWithUpdates.getGenericName());
            }
            if (medicineWithUpdates.getChemicalName() != null) {
                medicineToBeUpdated.setChemicalName(medicineWithUpdates.getChemicalName());
            }
            
            em.merge(medicineToBeUpdated);
            em.flush();
        }
        return medicineToBeUpdated;
    }

    
    public List<MedicalCertificate> getAllMedicalCertificates() {
        TypedQuery<MedicalCertificate> query = em.createNamedQuery(MedicalCertificate.ALL_MEDICAL_CERTIFICATES_QUERY_NAME, MedicalCertificate.class);
        return query.getResultList();
    }

    public MedicalCertificate getMedicalCertificateById(int certificateId) {
        TypedQuery<MedicalCertificate> query = em.createNamedQuery(MedicalCertificate.ID_CARD_QUERY_NAME, MedicalCertificate.class);
        query.setParameter(PARAM1, certificateId);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Transactional
    public MedicalCertificate persistMedicalCertificate(MedicalCertificate newMedicalCertificate) {
        em.persist(newMedicalCertificate);
        return newMedicalCertificate;
    }

    @Transactional
    public MedicalCertificate updateMedicalCertificate(int id, MedicalCertificate medicalCertificateWithUpdates) {
        MedicalCertificate medicalCertificateToBeUpdated = getMedicalCertificateById(id);
        if (medicalCertificateToBeUpdated != null) {
            em.refresh(medicalCertificateToBeUpdated);

            // Update fields
            medicalCertificateToBeUpdated.setSigned(medicalCertificateWithUpdates.isSigned());

            // Update MedicalTraining if provided
            if (medicalCertificateWithUpdates.getMedicalTraining() != null && medicalCertificateWithUpdates.getMedicalTraining().getId() != 0) {
                MedicalTraining training = getMedicalTrainingById(medicalCertificateWithUpdates.getMedicalTraining().getId());
                if (training != null) {
                    medicalCertificateToBeUpdated.setMedicalTraining(training);
                }
            }

            // Update Owner (Physician) if provided
            if (medicalCertificateWithUpdates.getOwner() != null && medicalCertificateWithUpdates.getOwner().getId() != 0) {
                Physician owner = getPhysicianById(medicalCertificateWithUpdates.getOwner().getId());
                if (owner != null) {
                    medicalCertificateToBeUpdated.setOwner(owner);
                }
            }

            em.merge(medicalCertificateToBeUpdated);
            em.flush();
        }
        return medicalCertificateToBeUpdated;
    }

    @Transactional
    public MedicalCertificate deleteMedicalCertificate(int id) {
        MedicalCertificate mc = getMedicalCertificateById(id);
        if (mc != null) {
            em.remove(mc);
            return mc;
        }
        return null;
    }

}