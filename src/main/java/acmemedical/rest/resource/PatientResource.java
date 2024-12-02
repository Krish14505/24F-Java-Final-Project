package acmemedical.rest.resource;

import acmemedical.ejb.ACMEMedicalService;
import acmemedical.entity.Patient;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

import static acmemedical.utility.MyConstants.*;

@Path(PATIENT_RESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PatientResource {

    private static final Logger LOG = LogManager.getLogger();

    @EJB
    protected ACMEMedicalService service;

    @GET
    @RolesAllowed({ADMIN_ROLE})
    public Response getAllPatients(){
        LOG.debug("Retrieving all patients...");
        List<Patient> patients = service.getAllPatients();
        return Response.ok(patients).build();
    }


    @GET
    @RolesAllowed({ADMIN_ROLE,USER_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response getPatientById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id){
        LOG.debug("Retrieving patient by id: {}", id);
        Patient patient = service.getPatientById(id);
        return Response.status(patient == null ? Response.Status.NOT_FOUND: Response.Status.OK).entity(patient).build();

    }

    @POST
    @RolesAllowed({ADMIN_ROLE}) //Only admin can add a new patient
    public Response addPatient(Patient patient){
        LOG.debug("Adding patient: {}", patient);
        Patient newPatient = service.persistPatient(patient);
        return Response.ok(newPatient).build();
    }



    @DELETE
    @RolesAllowed({ADMIN_ROLE}) // only admin role can delete a patient
    @Path(RESOURCE_PATH_ID_PATH)
    public Response deletePatient(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id){
        LOG.debug("Deleting patient with id: {}", id);
        service.deletePatientById(id);
        return Response.noContent().build();
    }


}
