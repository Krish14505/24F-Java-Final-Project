package acmemedical.rest.resource;

import acmemedical.ejb.ACMEMedicalService;
import acmemedical.entity.Prescription;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;


import static acmemedical.utility.MyConstants.*;

/**
 * This is the resource class for the Prescription class
 * @author krish Chaudhary
 * @version 1
 */
@Path(PRESCRIPTION_RESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PrescriptionResource {

    protected static final Logger LOG = LogManager.getLogger();

    @EJB
    protected ACMEMedicalService service;

    @GET
    @RolesAllowed({ADMIN_ROLE,USER_ROLE}) // Both admin and user role can getPrescriptions.
    public Response getAllPrescriptions(){
        LOG.debug("Retrieving All prescription...");
        List<Prescription> prescriptionList = service.getAllPrescriptions();
        return Response.ok(prescriptionList).build();
    }

    @GET
    @RolesAllowed({ADMIN_ROLE,USER_ROLE}) // Both admin and user role can retrieve the specific prescription instance.
    public Response getPrescriptionById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id){
        LOG.debug("Retrieving Prescription by id..."+ id);
        Prescription prescription = service.getPrescriptionById(id);
        return prescription !=null? Response.ok(prescription).build():Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    @RolesAllowed({ADMIN_ROLE}) //Only admin can add the prescription.
    public Response addPrescription(Prescription prescription){
        LOG.debug("Adding Prescription..."+ prescription);
        Prescription prescription1 = service.persistPrescription(prescription);
        return Response.ok(prescription1).build();
    }

    @PUT
    @RolesAllowed({ADMIN_ROLE}) // only admin can updated the prescription
    public Response updatePrescription(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id,Prescription updatedPrescription){
        Prescription prescription = service.updatePrescription(id,updatedPrescription);
        return prescription != null? Response.ok(prescription).build():Response.status(Response.Status.NOT_FOUND).build();
    }

    public Response deletePrescription(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id){
        LOG.debug("Deleting Prescription..."+ id);
        service.deletePrescription(id);
        return Response.ok().build();
    }





}
