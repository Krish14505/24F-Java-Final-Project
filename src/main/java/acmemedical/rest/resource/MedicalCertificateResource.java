package acmemedical.rest.resource;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.security.enterprise.SecurityContext;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import static acmemedical.utility.MyConstants.ADMIN_ROLE;
import static acmemedical.utility.MyConstants.USER_ROLE;

import java.util.List;

import static acmemedical.utility.MyConstants.MEDICAL_CERTIFICATE_RESOURCE_NAME;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import acmemedical.ejb.ACMEMedicalService;
import acmemedical.entity.MedicalCertificate;
import acmemedical.entity.MedicalTraining;
import acmemedical.entity.Physician;

/**
 * REST resource class for managing Medical Certificates.
 *
 * @author Harmeet Matharoo
 * @date 2024-12-03
 */
@Path(MEDICAL_CERTIFICATE_RESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MedicalCertificateResource {

    private static final Logger LOG = LogManager.getLogger();

    @EJB
    protected ACMEMedicalService service;

    @Inject
    protected SecurityContext sc;

    /**
     * Retrieves all medical certificates.
     *
     * @return Response containing list of all medical certificates.
     */
    @GET
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
    public Response getMedicalCertificates() {
        LOG.debug("Retrieving all medical certificates...");
        List<MedicalCertificate> medicalCertificates = service.getAllMedicalCertificates();
        LOG.debug("Medical certificates found = {}", medicalCertificates);
        return Response.ok(medicalCertificates).build();
    }

    /**
     * Retrieves a medical certificate by its ID.
     *
     * @param certificateId The ID of the medical certificate.
     * @return Response containing the medical certificate or a NOT_FOUND status.
     */
    @GET
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
    @Path("/{certificateId}")
    public Response getMedicalCertificateById(@PathParam("certificateId") int certificateId) {
        LOG.debug("Retrieving medical certificate with id = {}", certificateId);
        MedicalCertificate medicalCertificate = service.getMedicalCertificateById(certificateId);
        if (medicalCertificate != null) {
            return Response.ok(medicalCertificate).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("MedicalCertificate not found").build();
        }
    }

    /**
     * Adds a new medical certificate.
     *
     * @param newMedicalCertificate The medical certificate to add.
     * @return Response containing the added medical certificate.
     */
    @POST
    @RolesAllowed({ADMIN_ROLE})
    public Response addMedicalCertificate(MedicalCertificate newMedicalCertificate) {
        LOG.debug("Adding a new medical certificate = {}", newMedicalCertificate);

        // Ensure the MedicalTraining is properly set
        if (newMedicalCertificate.getMedicalTraining() != null && newMedicalCertificate.getMedicalTraining().getId() != 0) {
            MedicalTraining training = service.getMedicalTrainingById(newMedicalCertificate.getMedicalTraining().getId());
            if (training == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("MedicalTraining not found").build();
            }
            newMedicalCertificate.setMedicalTraining(training);
        } else {
            return Response.status(Response.Status.BAD_REQUEST).entity("MedicalTraining is required").build();
        }

        // Ensure the Physician (owner) is properly set
        if (newMedicalCertificate.getOwner() != null && newMedicalCertificate.getOwner().getId() != 0) {
            Physician owner = service.getPhysicianById(newMedicalCertificate.getOwner().getId());
            if (owner == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Physician (owner) not found").build();
            }
            newMedicalCertificate.setOwner(owner);
        } else {
            return Response.status(Response.Status.BAD_REQUEST).entity("Physician (owner) is required").build();
        }

        MedicalCertificate tempMedicalCertificate = service.persistMedicalCertificate(newMedicalCertificate);
        return Response.ok(tempMedicalCertificate).build();
    }

    /**
     * Updates an existing medical certificate.
     *
     * @param certificateId             The ID of the medical certificate to update.
     * @param updatingMedicalCertificate The updated medical certificate data.
     * @return Response containing the updated medical certificate or a NOT_FOUND status.
     */
    @PUT
    @RolesAllowed({ADMIN_ROLE})
    @Path("/{certificateId}")
    public Response updateMedicalCertificate(@PathParam("certificateId") int certificateId, MedicalCertificate updatingMedicalCertificate) {
        LOG.debug("Updating medical certificate with id = {}", certificateId);
        MedicalCertificate updatedMedicalCertificate = service.updateMedicalCertificate(certificateId, updatingMedicalCertificate);
        if (updatedMedicalCertificate != null) {
            return Response.ok(updatedMedicalCertificate).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("MedicalCertificate not found").build();
        }
    }

    /**
     * Deletes a medical certificate by its ID.
     *
     * @param certificateId The ID of the medical certificate to delete.
     * @return Response indicating the result of the delete operation.
     */
    @DELETE
    @RolesAllowed({ADMIN_ROLE})
    @Path("/{certificateId}")
    public Response deleteMedicalCertificate(@PathParam("certificateId") int certificateId) {
        LOG.debug("Deleting medical certificate with id = {}", certificateId);
        MedicalCertificate mc = service.deleteMedicalCertificate(certificateId);
        if (mc != null) {
            return Response.noContent().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("MedicalCertificate not found").build();
        }
    }
}
