package acmemedical.rest.resource;

import acmemedical.ejb.ACMEMedicalService;
import acmemedical.entity.Medicine;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.xml.ws.WebServiceContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

import static acmemedical.utility.MyConstants.*;

@Path(MEDICINE_SUBRESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MedicineResource {

    private static final Logger LOG = LogManager.getLogger();

    @EJB
    protected ACMEMedicalService service;

    @Inject
    protected SecurityContext sc;

    @GET
    @RolesAllowed({ADMIN_ROLE})
    public Response getAllMedicines() {
        LOG.debug("retrieving all medicines...");
        List<Medicine> medicines = service.getAllMedicines();
        return Response.ok(medicines).build();
    }

    @GET
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response getMedicineById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
        LOG.debug("retrieving medicine by id: {}", id);
        Medicine medicine = service.getMedicineById(id);
        return Response.status(medicine == null ? Response.Status.NOT_FOUND : Response.Status.OK)
                .entity(medicine)
                .build();    }

    @POST
    @RolesAllowed({ADMIN_ROLE})
    public Response addMedicine(Medicine medicine) {
        LOG.debug("adding new medicine: {}", medicine);
        Medicine newMedicine = service.persistMedicine(medicine);
        return Response.ok(newMedicine).build();
    }



    @DELETE
    @RolesAllowed({ADMIN_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response deleteMedicine(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
        LOG.debug("deleting medicine by id: {}", id);
        service.deleteMedicineByid(id);
        return Response.noContent().build();
    }
}

