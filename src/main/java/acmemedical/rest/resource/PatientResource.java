package acmemedical.rest.resource;

import acmemedical.ejb.ACMEMedicalService;
import acmemedical.entity.Patient;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

import static acmemedical.utility.MyConstants.*;

@Path(PATIENT_RESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PatientResource {

    @EJB
    protected ACMEMedicalService service;

    @GET
    @RolesAllowed({ADMIN_ROLE,USER_ROLE})
    public Response getAllPatients(){
        List<Patient> patients = service.getAllPatients();
        return Response.ok(patients).build();
    }


    @GET
    @RolesAllowed({ADMIN_ROLE,USER_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response getPatientById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id){
        Patient patient = service.getPatientById(id);
        return patient != null ? Response.ok(patient).build() : Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    @RolesAllowed({ADMIN_ROLE}) //Only admin can add a new patient
    public Response addPatient(Patient patient){
        Patient newPatient = service.persistPatient(patient);
        return Response.ok(newPatient).build();
    }

    @PUT
    @RolesAllowed({ADMIN_ROLE}) // only admin role can update a patient
    @Path(RESOURCE_PATH_ID_PATH)
    public Response updatePatient(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id,  Patient updatedPatient){
        Patient patient = service.updatePatient(id,updatedPatient);
        return patient != null ? Response.ok(patient).build() : Response.status(Response.Status.NOT_FOUND).build();
    }

    @DELETE
    @RolesAllowed({ADMIN_ROLE}) // only admin role can delete a patient
    @Path(RESOURCE_PATH_ID_PATH)
    public Response deletePatient(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id){
        service.deletePatient(id);
        return Response.noContent().build();
    }


}
