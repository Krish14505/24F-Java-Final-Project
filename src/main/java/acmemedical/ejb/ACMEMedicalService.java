/********************************************************************************************************
 * File:  ACMEMedicalService.java Course Materials CST 8277
 *
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * 
 */
package acmemedical.ejb;

import static acmemedical.entity.MedicalTraining.ALL_MEDICAL_TRAINING_QUERY_NAME;
import static acmemedical.entity.MedicalTraining.IS_DUPLICATE_TRAINING;
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
//import static acmemedical.entity.Physician.ALL_PHYSICIANS_QUERY_NAME;
import static acmemedical.entity.MedicalSchool.ALL_MEDICAL_SCHOOLS_QUERY_NAME;
import static acmemedical.entity.MedicalSchool.IS_DUPLICATE_QUERY_NAME;
import static acmemedical.entity.MedicalSchool.SPECIFIC_MEDICAL_SCHOOL_QUERY_NAME;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import acmemedical.entity.*;
import acmemedical.rest.resource.MedicalTrainingResource;
import jakarta.ejb.Singleton;
import jakarta.enterprise.inject.Typed;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.security.enterprise.identitystore.Pbkdf2PasswordHash;
import jakarta.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


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
//    @Inject
//    private MedicalTrainingResource medicalTrainingResource;

    /**
     * The method that is used to fetch all the physicians(security users)
     * @return physician
     */
    public List<Physician> getAllPhysicians() {
//        CriteriaBuilder cb = em.getCriteriaBuilder();
//        CriteriaQuery<Physician> cq = cb.createQuery(Physician.class);
//        cq.select(cq.from(Physician.class));
//        return em.createQuery(cq).getResultList();
        TypedQuery<Physician> query = em.createNamedQuery(Physician.ALL_PHYSICIAN_QUERY_NAME, Physician.class);
        return query.getResultList();
    }

    /**
     * The method that returns the specific physician by id
     * @param id of the physician
     * @return physician
     */
    public Physician getPhysicianById(int id) {
        return em.find(Physician.class, id);
    }

    /**
     * The method used to persist the physician into the table physician in the database.
     * @param newPhysician newPhysician being added!
     * @return newPhysician
     */
    @Transactional
    public Physician persistPhysician(Physician newPhysician) {
        em.persist(newPhysician);
        return newPhysician;
    }

    /**
     * The method used to build the new physician user
     * @param newPhysician newPhysician being added !
     */
    @Transactional
    public void buildUserForNewPhysician(Physician newPhysician) {
        SecurityUser userForNewPhysician = new SecurityUser();
        userForNewPhysician.setUsername(
            DEFAULT_USER_PREFIX + "_" + newPhysician.getFirstName() + "." + newPhysician.getLastName());
        Map<String, String> pbAndjProperties = new HashMap<>();
        pbAndjProperties.put(PROPERTY_ALGORITHM, DEFAULT_PROPERTY_ALGORITHM);
        pbAndjProperties.put(PROPERTY_ITERATIONS, DEFAULT_PROPERTY_ITERATIONS);
        pbAndjProperties.put(PROPERTY_SALT_SIZE, DEFAULT_SALT_SIZE);
        pbAndjProperties.put(PROPERTY_KEY_SIZE, DEFAULT_KEY_SIZE);
        pbAndjPasswordHash.initialize(pbAndjProperties);
        String pwHash = pbAndjPasswordHash.generate(DEFAULT_USER_PASSWORD.toCharArray());
        userForNewPhysician.setPwHash(pwHash);
        userForNewPhysician.setPhysician(newPhysician);

        //Implementation on find the role of the user
        TypedQuery<SecurityRole> query = em.createNamedQuery("SecurityRole.findByName", SecurityRole.class);
        query.setParameter("roleName",USER_ROLE);
        SecurityRole userRole = query.getSingleResult();/* TODO ACMECS01 - Use NamedQuery on SecurityRole to find USER_ROLE */

        userForNewPhysician.getRoles().add(userRole);
        userRole.getUsers().add(userForNewPhysician);
        em.persist(userForNewPhysician);
    }

    /**
     * The method used to set the medicine for physicianPatient
     * @param physicianId physician ID
     * @param patientId patient id
     * @param newMedicine newMedicine
     * @return Medicine object
     */
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
            TypedQuery<SecurityUser> findUser = em.createNamedQuery("SecurityUser.findByPhysicianId", SecurityUser.class);
            /* TODO ACMECS02 - Use NamedQuery on SecurityRole to find this related Student
               so that when we remove it, the relationship from SECURITY_USER table
               is not dangling
            */
            findUser.setParameter("physicianId",physician.getId());
            SecurityUser sUser = findUser.getSingleResult();
            em.remove(sUser);
            em.remove(physician);
        }
    }

    /**
     * Get the records of all the MedicalSchool table
     * @return list of query result list
     */
    public List<MedicalSchool> getAllMedicalSchools() {
//        CriteriaBuilder cb = em.getCriteriaBuilder();
//        CriteriaQuery<MedicalSchool> cq = cb.createQuery(ALL_MEDICAL_SCHOOLS_QUERY_NAME,MedicalSchool.class);
//        cq.select(cq.from(MedicalSchool.class));
//        return em.createQuery(cq).getResultList();
        TypedQuery<MedicalSchool> query = em.createNamedQuery(ALL_MEDICAL_SCHOOLS_QUERY_NAME, MedicalSchool.class);
        return query.getResultList();
    }

    // Why not use the build-in em.find?  The named query SPECIFIC_MEDICAL_SCHOOL_QUERY_NAME
    // includes JOIN FETCH that we cannot add to the above API

    /**
     * fetch the specific Medical school by its id
     * @param id id of the MedicalSchool
     * @return get the specific Result
     */
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
        return allQuery.getSingleResult();
    }

    /**
     * delete the medical school
     * @param id id of the medicalSchool
     * @return medicalSchool Instance
     */
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
        }
        return null;
    }
    
    // Please study & use the methods below in your test suites

    /**
     * Find checking that the newMedicalSchool should not be duplicated.
     * @param newMedicalSchool new instance
     * @return boolean value either true or false
     */
    public boolean isDuplicated(MedicalSchool newMedicalSchool) {
        TypedQuery<Long> allMedicalSchoolsQuery = em.createNamedQuery(IS_DUPLICATE_QUERY_NAME, Long.class);
        allMedicalSchoolsQuery.setParameter(PARAM1, newMedicalSchool.getName());
        return (allMedicalSchoolsQuery.getSingleResult() >= 1);
    }

    /**
     * save the medical school to the table
     * @param newMedicalSchool instance
     * @return MedicalInstance
     */
    @Transactional
    public MedicalSchool persistMedicalSchool(MedicalSchool newMedicalSchool) {
        em.persist(newMedicalSchool);
        return newMedicalSchool;
    }

    /**
     *  updated the medical school
     * @param id id
     * @param updatingMedicalSchool updatingMedicalSchool
     * @return MedicalSchoolTobeUpdated
     */
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

    /**
     * persist the medicalTraining to the table in the database
     * @param newMedicalTraining newMedicalTraining
     * @return newMedicalTraining
     */
    @Transactional
    public MedicalTraining persistMedicalTraining(MedicalTraining newMedicalTraining) {
        em.persist(newMedicalTraining);
        return newMedicalTraining;
    }

    /**
     * return the specific MedicalTraining record by its id
     * @param mtId id of the table
     * @return one single result
     */
    public MedicalTraining getMedicalTrainingById(int mtId) {
        TypedQuery<MedicalTraining> allMedicalTrainingQuery = em.createNamedQuery(MedicalTraining.FIND_BY_ID, MedicalTraining.class);
        allMedicalTrainingQuery.setParameter(PARAM1, mtId);
        return allMedicalTrainingQuery.getSingleResult();
    }

    /**
     * Update the medical training with id and the newly updated value of the instance .
     * @param id id of the medicalTraining
     * @param medicalTrainingWithUpdates updated with valued
     * @return updated
     */
    @Transactional
    public MedicalTraining updateMedicalTraining(int id, MedicalTraining medicalTrainingWithUpdates) {
    	MedicalTraining medicalTrainingToBeUpdated = getMedicalTrainingById(id);
        if (medicalTrainingToBeUpdated != null) {
            em.refresh(medicalTrainingToBeUpdated);
            em.merge(medicalTrainingWithUpdates);
            em.flush();
        }
        return medicalTrainingToBeUpdated;
    }

    //The following methods are added by Krish Chaudhary to completed the requirements of the other entity's Resources

    /**
     * get All the medicalTraining with the list
     * @return result list consisting of MedicalTraining records
     */
    public List<MedicalTraining> getAllMedicalTrainings(){
        TypedQuery<MedicalTraining>  query = em.createNamedQuery(ALL_MEDICAL_TRAINING_QUERY_NAME,MedicalTraining.class);
        return query.getResultList();
    }

    /**
     * delete the medicalTraining instance
     * @param id id of the training
     * @return MedicalTraining
     */
    @Transactional
    public MedicalTraining deleteMedicalTraining(int id) {

       MedicalTraining mt = getMedicalTrainingById(id);
       if(mt != null){
           em.remove(mt);
       }
       return mt;
    }

    /**
     * This method is used to check the persisting record has to be unique that should not be existed!
     * @param newTraining newTraining instance ready to be persisted to the database.
     * @return either true or false
     */
    public boolean isDuplicatedTraining(MedicalTraining newTraining){
        TypedQuery<Long> query = em.createNamedQuery(IS_DUPLICATE_TRAINING, Long.class);
        query.setParameter(PARAM1,newTraining.getId());
        return query.getSingleResult() >= 1 ;
    }

    /*
     *  Now the following method will be used to do the CRUD functionality for the Patient Resource.
    */

    /**
     * Method used to fetch all the patients stored in the database.
     * @return patientList
     */
    public List<Patient> getAllPatients(){
        TypedQuery<Patient> query = em.createNamedQuery("Patient.findAll", Patient.class);
        return query.getResultList();
    }


    /**
     * Method will be used to get the specific Patient by id.
     * @param id id of the patient
     * @return specific instance selected patient.
     */
    public Patient getPatientById(int id) {
        TypedQuery<Patient> query = em.createNamedQuery("Patient.findById", Patient.class);
        query.setParameter(PARAM1, id);
        return query.getSingleResult();
    }


    /**
     * method will be used to persist Patient
     * @param newPatient newInstance of the patient
     * @return newPatient
     */
    @Transactional
    public Patient persistPatient(Patient newPatient) {
        em.persist(newPatient);
        return newPatient;
    }

    /**
     * method used to update the patient
     * @param id id of the patient
     * @param updatingPatient updating the patient
     * @return patientToBeUpdated.
     */
    @Transactional
    public Patient updatePatient(int id, Patient updatingPatient) {
        Patient patientToBeUpdated = getPatientById(id);
        if (patientToBeUpdated != null) {
            em.merge(updatingPatient);
            em.flush();
        }
        return patientToBeUpdated;
    }

    /**
     * Method that avoid inserting the same existing record.
     * @param newPatient new adding instance of the patient.
     * @return boolean value
     */
    public boolean isDuplicatedPatient(Patient newPatient) {
        TypedQuery<Long> query = em.createNamedQuery("Patient.isDuplicate", Long.class);
        query.setParameter(PARAM1, newPatient.getId());
        return query.getSingleResult() >= 1;
    }

    /**
     * delete patient method
     * @param id id of the patient
     */
    @Transactional
    public void deletePatient(int id) {
        Patient patient = getPatientById(id);
        if(patient != null){
            em.remove(patient);
        }
    }






}