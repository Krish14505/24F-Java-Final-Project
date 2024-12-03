/********************************************************************************************************
 * File:  PhysicianResource.java Course Materials CST 8277
 *
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * @author (original) Mike Norman
 * @author Harmeet Matharoo
 * @date 2024-12-03
 */
package acmemedical.rest.resource;

import static acmemedical.utility.MyConstants.ADMIN_ROLE;
import static acmemedical.utility.MyConstants.PHYSICIAN_PATIENT_MEDICINE_RESOURCE_PATH;
import static acmemedical.utility.MyConstants.PHYSICIAN_RESOURCE_NAME;
import static acmemedical.utility.MyConstants.RESOURCE_PATH_ID_ELEMENT;
import static acmemedical.utility.MyConstants.RESOURCE_PATH_ID_PATH;
import static acmemedical.utility.MyConstants.USER_ROLE;

import java.util.List;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.security.enterprise.SecurityContext;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.soteria.WrappingCallerPrincipal;

import acmemedical.ejb.ACMEMedicalService;
import acmemedical.entity.Medicine;
import acmemedical.entity.SecurityUser;
import acmemedical.entity.Physician;

@Path(PHYSICIAN_RESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PhysicianResource {

    private static final Logger LOG = LogManager.getLogger();

    @EJB
    protected ACMEMedicalService service;

    @Inject
    protected SecurityContext sc;

    @GET
    //Only a user with the SecurityRole ‘ADMIN_ROLE’ can get the list of all physicians.
    @RolesAllowed({ADMIN_ROLE})
    public Response getPhysicians() {
        LOG.debug("retrieving all physicians ...");
        List<Physician> physicians = service.getAllPhysicians();
        Response response = Response.ok(physicians).build();
        return response;
    }

    @GET
    //A user with either the role ‘ADMIN_ROLE’ or ‘USER_ROLE’ can get a specific physician.
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response getPhysicianById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
        LOG.debug("try to retrieve specific physician " + id);
        Response response = null;
        Physician physician = null;

        if (sc.isCallerInRole(ADMIN_ROLE)) {
        	physician = service.getPhysicianById(id);
            response = Response.status(physician == null ? Status.NOT_FOUND : Status.OK).entity(physician).build();
        } else if (sc.isCallerInRole(USER_ROLE)) {
            WrappingCallerPrincipal wCallerPrincipal = (WrappingCallerPrincipal) sc.getCallerPrincipal();
            SecurityUser sUser = (SecurityUser) wCallerPrincipal.getWrapped();
            physician = sUser.getPhysician();
            if (physician != null && physician.getId() == id) {
                response = Response.status(Status.OK).entity(physician).build();
            } else {
            	//disallows a ‘USER_ROLE’ user from getting a physician that is not linked to the SecurityUser.
                throw new ForbiddenException("User trying to access resource it does not own (wrong userid)");
            }
        } else {
            response = Response.status(Status.BAD_REQUEST).build();
        }
        return response;
    }

    @POST
    //Only a user with the SecurityRole ‘ADMIN_ROLE’ can add a new physician.
    @RolesAllowed({ADMIN_ROLE})
    public Response addPhysician(Physician newPhysician) {
        LOG.info("Received request to add a new physician: {} {}", newPhysician.getFirstName(), newPhysician.getLastName());
        try {
            Physician newPhysicianWithIdTimestamps = service.persistPhysician(newPhysician);
            // Build a SecurityUser linked to the new physician
            service.buildUserForNewPhysician(newPhysicianWithIdTimestamps);
            return Response.status(Response.Status.CREATED).entity(newPhysicianWithIdTimestamps).build();
        } catch (Exception e) {
            LOG.error("Error adding physician", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error adding physician").build();
        }
    }


    @PUT
    //Only an ‘ADMIN_ROLE’ user can associate a Medicine and/or Patient to a Physician.
    @RolesAllowed({ADMIN_ROLE})
    @Path(PHYSICIAN_PATIENT_MEDICINE_RESOURCE_PATH)
    public Response updateMedicineForPhysicianPatient(@PathParam("physicianId") int physicianId, @PathParam("patientId") int patientId, Medicine newMedicine) {
        Response response = null;
        Medicine medicine = service.setMedicineForPhysicianPatient(physicianId, patientId, newMedicine);
        response = Response.ok(medicine).build();
        return response;
    }
    
    @DELETE
    @Path("{id}")
    @RolesAllowed({ADMIN_ROLE})
    public Response deletePhysician(@PathParam("id") int id) {
        LOG.info("Received request to delete physician with id: {}", id);
        try {
            service.deletePhysicianById(id);
            return Response.noContent().build(); // 204 No Content
        } catch (Exception e) {
            LOG.error("Error deleting physician", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error deleting physician").build();
        }
    }

    // **New PUT Endpoint for Updating Physician's Basic Information**
    @PUT
    @RolesAllowed({ADMIN_ROLE})
    @Path("/{id}")
    public Response updatePhysician(@PathParam("id") int id, Physician updatedPhysician) {
        LOG.info("Received request to update physician with ID: {}", id);
        try {
            Physician physician = service.updatePhysicianById(id, updatedPhysician);
            if (physician != null) {
                return Response.ok(physician).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("Physician not found").build();
            }
        } catch (Exception e) {
            LOG.error("Error updating physician with ID: {}", id, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error updating physician").build();
        }
    }
    
}