package acmemedical.rest.resource;

import acmemedical.ejb.ACMEMedicalService;
import acmemedical.entity.Medicine;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

import static acmemedical.utility.MyConstants.*;

@Path(MEDICINE_SUBRESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MedicalResource {

    @EJB
    protected ACMEMedicalService service;

    @GET
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
    public Response getAllMedicines() {
        List<Medicine> medicines = service.getAllMedicines();
        return Response.ok(medicines).build();
    }

    @GET
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response getMedicineById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
        Medicine medicine = service.getMedicineById(id);
        return medicine != null ? Response.ok(medicine).build() : Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    @RolesAllowed({ADMIN_ROLE})
    public Response addMedicine(@Valid Medicine medicine) {
        Medicine newMedicine = service.persistMedicine(medicine);
        return Response.ok(newMedicine).build();
    }

    @PUT
    @RolesAllowed({ADMIN_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response updateMedicine(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id, Medicine medicine) {
        Medicine updatingMedicine = service.updateMedicine(id, medicine);
        return medicine != null ? Response.ok(updatingMedicine).build() : Response.status(Response.Status.NOT_FOUND).build();
    }

    @DELETE
    @RolesAllowed({ADMIN_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response deleteMedicine(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
        service.deleteMedicine(id);
        return Response.noContent().build();
    }
}

