package acmemedical.rest.resource;

import acmemedical.ejb.ACMEMedicalService;
import acmemedical.entity.Medicine;
import acmemedical.utility.MyConstants;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import static acmemedical.utility.MyConstants.ADMIN_ROLE;
import static acmemedical.utility.MyConstants.USER_ROLE;

import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * REST resource class for managing Medicines.
 *
 * <p>This class provides endpoints for CRUD operations on Medicine entities.</p>
 * 
 * @author: Harmeet Matharoo
 * @date: 2024-12-03
 */
@Path(MyConstants.MEDICINE_RESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MedicineResource {

    private static final Logger LOG = LogManager.getLogger(MedicineResource.class);

    @Inject
    private ACMEMedicalService acmeMedicalService;

    /**
     * Retrieves all medicines.
     *
     * @return Response containing a list of all medicines or a NO_CONTENT status if none are found.
     */
    @GET
    @RolesAllowed({ADMIN_ROLE})
    public Response getAllMedicines() {
        LOG.info("Received request to get all medicines");
        List<Medicine> medicines = acmeMedicalService.getAllMedicines();
        if (medicines != null && !medicines.isEmpty()) {
            LOG.info("Number of medicines found: {}", medicines.size());
            return Response.ok(medicines).build();
        } else {
            LOG.warn("No medicines found");
            return Response.status(Response.Status.NO_CONTENT).build();
        }
    }

    /**
     * Retrieves a medicine by its ID.
     *
     * @param id The ID of the medicine to retrieve.
     * @return Response containing the medicine or a NOT_FOUND status if it does not exist.
     */
    @GET
    @Path("/{id}")
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
    public Response getMedicineById(@PathParam("id") int id) {
        LOG.info("Received request to get medicine with id: {}", id);
        Medicine medicine = acmeMedicalService.getMedicineById(id);
        if (medicine != null) {
            LOG.info("Medicine found: {}", medicine.getDrugName());
            return Response.ok(medicine).build();
        } else {
            LOG.warn("Medicine with id {} not found", id);
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
    
    /**
     * Adds a new medicine.
     *
     * @param newMedicine The medicine entity to add.
     * @return Response containing the created medicine with a CREATED status or an error status if the operation fails.
     */
    @POST
    @RolesAllowed({ADMIN_ROLE})
    public Response addMedicine(Medicine newMedicine) {
        LOG.info("Received request to add a new medicine: {}", newMedicine.getDrugName());
        try {
            Medicine createdMedicine = acmeMedicalService.persistMedicine(newMedicine);
            LOG.info("Medicine added successfully with id: {}", createdMedicine.getId());
            return Response.status(Response.Status.CREATED).entity(createdMedicine).build();
        } catch (Exception e) {
            LOG.error("Error adding medicine", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error adding medicine").build();
        }
    }

    /**
     * Deletes a medicine by its ID.
     *
     * @param id The ID of the medicine to delete.
     * @return Response indicating the result of the delete operation. Returns NO_CONTENT if successful or an error status if the operation fails.
     */
    @DELETE
    @Path("/{id}")
    @RolesAllowed({ADMIN_ROLE})
    public Response deleteMedicine(@PathParam("id") int id) {
        LOG.info("Received request to delete medicine with id: {}", id);
        try {
            acmeMedicalService.deleteMedicineById(id);
            return Response.noContent().build(); // 204 No Content            
        } catch (Exception e) {
            LOG.error("Error deleting medicine", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error deleting medicine").build();
        }
    }
    
    
    /**
     * Updates a medicine by its ID.
     *
     * @param id The ID of the medicine to update.
     * @param updatedMedicine The medicine entity with updated information.
     * @return Response containing the updated medicine or appropriate error status.
     */
    @PUT
    @Path("/{id}")
    @RolesAllowed({ADMIN_ROLE})
    public Response updateMedicine(@PathParam("id") int id, Medicine updatedMedicine) {
        LOG.info("Received request to update medicine with ID: {}", id);
        try {
            Medicine medicine = acmeMedicalService.updateMedicineById(id, updatedMedicine);
            if (medicine != null) {
                LOG.info("Medicine with ID {} updated successfully.", id);
                return Response.ok(medicine).build();
            } else {
                LOG.warn("Medicine with ID {} not found.", id);
                return Response.status(Response.Status.NOT_FOUND).entity("Medicine not found").build();
            }
        } catch (Exception e) {
            LOG.error("Error updating medicine with ID: {}", id, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error updating medicine").build();
        }
    }

}
