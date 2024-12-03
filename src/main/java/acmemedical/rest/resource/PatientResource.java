package acmemedical.rest.resource;

import acmemedical.ejb.ACMEMedicalService;
import acmemedical.entity.Patient;
import acmemedical.utility.MyConstants;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import static acmemedical.utility.MyConstants.ADMIN_ROLE;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * REST resource class for managing Patients.
 *
 * <p>This class provides endpoints for CRUD operations on Patient entities.</p>
 * 
 * @author: Harmeet Matharoo</p>
 * @date: 2024-12-03</p>
 */
@Path(MyConstants.PATIENT_RESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PatientResource {

    private static final Logger LOG = LogManager.getLogger(PatientResource.class);

    @Inject
    private ACMEMedicalService acmeMedicalService;

    /**
     * Retrieves a patient by their ID.
     *
     * @param id The ID of the patient to retrieve.
     * @return Response containing the patient entity or a NOT_FOUND status if the patient does not exist.
     */
    @GET
    @Path("/{id}")
    @PermitAll
    public Response getPatientById(@PathParam("id") int id) {
        LOG.info("Received request to get patient with id: {}", id);
        Patient patient = acmeMedicalService.getPatientById(id);
        if (patient != null) {
            LOG.info("Patient found: {}", patient.getFirstName());
            return Response.ok(patient).build();
        } else {
            LOG.warn("Patient with id {} not found", id);
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    /**
     * Retrieves all patients.
     *
     * @return Response containing a list of all patients or a NO_CONTENT status if none are found.
     */
    @GET
    @RolesAllowed({ADMIN_ROLE})
    public Response getAllPatients() {
        LOG.info("Received request to get all patients");
        List<Patient> patients = acmeMedicalService.getAllPatients();
        if (patients != null && !patients.isEmpty()) {
            LOG.info("Number of patients found: {}", patients.size());
            return Response.ok(patients).build();
        } else {
            LOG.warn("No patients found");
            return Response.status(Response.Status.NO_CONTENT).build();
        }
    }
    
    /**
     * Adds a new patient.
     *
     * @param newPatient The patient entity to add.
     * @return Response containing the created patient with a CREATED status or an error status if the operation fails.
     */
    @POST
    @RolesAllowed({ADMIN_ROLE})
    public Response addPatient(Patient newPatient) {
        LOG.info("Received request to add a new patient: {}", newPatient.getFirstName());
        try {
            Patient createdPatient = acmeMedicalService.persistPatient(newPatient);
            LOG.info("Patient added successfully with id: {}", createdPatient.getId());
            return Response.status(Response.Status.CREATED).entity(createdPatient).build();
        } catch (Exception e) {
            LOG.error("Error adding patient", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error adding patient").build();
        }
    }
    
    /**
     * Deletes a patient by their ID.
     *
     * @param id The ID of the patient to delete.
     * @return Response indicating the result of the delete operation. Returns NO_CONTENT if successful,
     *         NOT_FOUND if the patient does not exist, or an error status if the operation fails.
     */
    @DELETE
    @Path("/{id}")
    @RolesAllowed({ADMIN_ROLE})
    public Response deletePatient(@PathParam("id") int id) {
        LOG.info("Received request to delete patient with id: {}", id);
        try {
            Patient patient = acmeMedicalService.getPatientById(id);
            if (patient != null) {
                acmeMedicalService.deletePatientById(id);
                LOG.info("Patient with id {} deleted successfully", id);
                return Response.noContent().build();
            } else {
                LOG.warn("Patient with id {} not found", id);
                return Response.status(Response.Status.NOT_FOUND).entity("Patient not found").build();
            }
        } catch (Exception e) {
            LOG.error("Error deleting patient", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error deleting patient").build();
        }
    }
    
    /**
     * Updates a patient by their ID.
     *
     * @param id The ID of the patient to update.
     * @param updatedPatient The patient entity with updated information.
     * @return Response containing the updated patient or appropriate error status.
     */
    @PUT
    @Path("/{id}")
    @RolesAllowed({ADMIN_ROLE})
    public Response updatePatient(@PathParam("id") int id, Patient updatedPatient) {
        LOG.info("Received request to update patient with ID: {}", id);
        try {
            Patient patient = acmeMedicalService.updatePatientById(id, updatedPatient);
            if (patient != null) {
                LOG.info("Patient with ID {} updated successfully.", id);
                return Response.ok(patient).build();
            } else {
                LOG.warn("Patient with ID {} not found.", id);
                return Response.status(Response.Status.NOT_FOUND).entity("Patient not found").build();
            }
        } catch (Exception e) {
            LOG.error("Error updating patient with ID: {}", id, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error updating patient").build();
        }
    }

}
