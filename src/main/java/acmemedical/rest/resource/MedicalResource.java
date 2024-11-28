package acmemedical.rest.resource;

import acmemedical.ejb.ACMEMedicalService;
import acmemedical.entity.Medicine;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
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
    @RolesAllowed({ADMIN_ROLE,USER_ROLE})
    public Response getAllMedicines(){
        List<Medicine> medicines = service.getAllMedicines();
    }

}
