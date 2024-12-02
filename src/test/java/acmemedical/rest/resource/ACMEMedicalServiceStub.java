/**
 * Stub implementation of the ACMEMedicalService for testing purposes.
 * This class provides in-memory data and simple logic to simulate service methods.
 *
 * @author Harmeet Matharoo
 */
package acmemedical.rest.resource;

import java.util.ArrayList;
import java.util.List;

import acmemedical.ejb.ACMEMedicalService;
import acmemedical.entity.MedicalSchool;
import acmemedical.entity.PrivateSchool;
import acmemedical.entity.PublicSchool;

public class ACMEMedicalServiceStub extends ACMEMedicalService {

    private List<MedicalSchool> schools = new ArrayList<>();

    /**
     * Initializes the stub with sample data.
     * Adds one public and one private medical school.
     */
    public ACMEMedicalServiceStub() {
        PublicSchool publicSchool = new PublicSchool();
        publicSchool.setId(1);
        publicSchool.setName("Public Medical School");

        PrivateSchool privateSchool = new PrivateSchool();
        privateSchool.setId(2);
        privateSchool.setName("Private Medical School");

        schools.add(publicSchool);
        schools.add(privateSchool);
    }

    /**
     * Retrieves all medical schools.
     *
     * @return List of medical schools.
     */
    @Override
    public List<MedicalSchool> getAllMedicalSchools() {
        return schools;
    }

    /**
     * Retrieves a medical school by its ID.
     *
     * @param id ID of the medical school to retrieve.
     * @return The medical school with the given ID, or null if not found.
     */
    @Override
    public MedicalSchool getMedicalSchoolById(int id) {
        return schools.stream().filter(school -> school.getId() == id).findFirst().orElse(null);
    }

    /**
     * Persists a new medical school in memory.
     *
     * @param school The medical school to persist.
     * @return The persisted medical school with an assigned ID.
     */
    @Override
    public MedicalSchool persistMedicalSchool(MedicalSchool school) {
        school.setId(schools.size() + 1);
        schools.add(school);
        return school;
    }
}
