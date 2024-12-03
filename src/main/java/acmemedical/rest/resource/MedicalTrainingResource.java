package acmemedical.rest.resource;

import java.util.List;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.security.enterprise.SecurityContext;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import static acmemedical.utility.MyConstants.ADMIN_ROLE;
import static acmemedical.utility.MyConstants.USER_ROLE;
import static acmemedical.utility.MyConstants.MEDICAL_TRAINING_RESOURCE_NAME;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import acmemedical.ejb.ACMEMedicalService;
import acmemedical.entity.MedicalTraining;
import acmemedical.entity.MedicalSchool;

/**
 * REST resource class for managing Medical Trainings.
 *
 * <p>This class provides endpoints for CRUD operations on MedicalTraining entities.</p>
 *
 * @author Harmeet Matharoo
 * @date 2024-12-03
 */
@Path(MEDICAL_TRAINING_RESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MedicalTrainingResource {

    private static final Logger LOG = LogManager.getLogger();

    @EJB
    protected ACMEMedicalService service;

    @Inject
    protected SecurityContext sc;

    /**
     * Retrieves all medical trainings.
     *
     * @return Response containing a list of all medical trainings.
     */
    @GET
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
    public Response getMedicalTrainings() {
        LOG.debug("Retrieving all medical trainings...");
        List<MedicalTraining> medicalTrainings = service.getAllMedicalTrainings();
        LOG.debug("Medical trainings found = {}", medicalTrainings);
        return Response.ok(medicalTrainings).build();
    }

    /**
     * Retrieves a medical training by its ID.
     *
     * @param medicalTrainingId The ID of the medical training to retrieve.
     * @return Response containing the medical training or a NOT_FOUND status.
     */
    @GET
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
    @Path("/{medicalTrainingId}")
    public Response getMedicalTrainingById(@PathParam("medicalTrainingId") int medicalTrainingId) {
        LOG.debug("Retrieving medical training with id = {}", medicalTrainingId);
        MedicalTraining medicalTraining = service.getMedicalTrainingById(medicalTrainingId);
        if (medicalTraining != null) {
            return Response.ok(medicalTraining).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("MedicalTraining not found").build();
        }
    }

    /**
     * Adds a new medical training.
     *
     * @param newMedicalTraining The medical training to add.
     * @return Response containing the added medical training.
     */
    @POST
    @RolesAllowed({ADMIN_ROLE})
    public Response addMedicalTraining(MedicalTraining newMedicalTraining) {
        LOG.debug("Adding a new medical training = {}", newMedicalTraining);

        // Ensure the MedicalSchool is properly set
        if (newMedicalTraining.getSchool() != null && newMedicalTraining.getSchool().getId() != 0) {
            MedicalSchool school = service.getMedicalSchoolById(newMedicalTraining.getSchool().getId());
            if (school == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("MedicalSchool not found").build();
            }
            newMedicalTraining.setSchool(school);
        } else {
            return Response.status(Response.Status.BAD_REQUEST).entity("MedicalSchool is required").build();
        }

        MedicalTraining tempMedicalTraining = service.persistMedicalTraining(newMedicalTraining);
        return Response.ok(tempMedicalTraining).build();
    }

    /**
     * Updates an existing medical training.
     *
     * @param mtId                   The ID of the medical training to update.
     * @param updatingMedicalTraining The updated medical training data.
     * @return Response containing the updated medical training or a NOT_FOUND status.
     */
    @PUT
    @RolesAllowed({ADMIN_ROLE})
    @Path("/{medicalTrainingId}")
    public Response updateMedicalTraining(@PathParam("medicalTrainingId") int mtId, MedicalTraining updatingMedicalTraining) {
        LOG.debug("Updating medical training with id = {}", mtId);
        MedicalTraining updatedMedicalTraining = service.updateMedicalTraining(mtId, updatingMedicalTraining);
        if (updatedMedicalTraining != null) {
            return Response.ok(updatedMedicalTraining).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("MedicalTraining not found").build();
        }
    }

    /**
     * Deletes a medical training by its ID.
     *
     * @param mtId The ID of the medical training to delete.
     * @return Response indicating the result of the delete operation.
     */
    @DELETE
    @RolesAllowed({ADMIN_ROLE})
    @Path("/{medicalTrainingId}")
    public Response deleteMedicalTraining(@PathParam("medicalTrainingId") int mtId) {
        LOG.debug("Deleting medical training with id = {}", mtId);
        MedicalTraining mt = service.deleteMedicalTraining(mtId);
        if (mt != null) {
            return Response.noContent().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("MedicalTraining not found").build();
        }
    }
}
