package acmemedical. rest. resource;
import acmemedical.ejb.ACMEMedicalService;
import acmemedical.entity.MedicalTraining;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


import java.util.List;

import static acmemedical.utility.MyConstants.*;

@Path(MEDICAL_TRAINING_RESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MedicalTrainingResource {

    @EJB
    protected ACMEMedicalService service;

    @GET
    @RolesAllowed({ADMIN_ROLE,USER_ROLE}) // Any user can retrieve all medicalTrainings
    public Response getAllMedicalTrainings() {
        List<MedicalTraining> trainings = service.getAllMedicalTrainings();
        return Response.ok(trainings).build();
    }

    @GET
    @RolesAllowed({ADMIN_ROLE,USER_ROLE}) // Any user can retrieve a specific MedicalTraining
    @Path(RESOURCE_PATH_TRAINING_ID_PATH)
    public Response getMedicalTrainingById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
        MedicalTraining training = service.getMedicalTrainingById(id);
        return training !=null ? Response.ok(training).build() : Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    @RolesAllowed({ADMIN_ROLE}) // Only ADMIN_ROLE can add a new MedicalTraining
    public Response addMedicalTraining(MedicalTraining training){
        if(service.isDuplicatedTraining(training)){
            return Response.status(Response.Status.CONFLICT).entity("Entity Already exists").build();
        }else {
            MedicalTraining newTraining = service.persistMedicalTraining(training);
            return Response.ok(newTraining).build();
        }
    }

    @PUT
    @RolesAllowed({ADMIN_ROLE})
    @Path(RESOURCE_PATH_TRAINING_ID_PATH)
    public Response updateMedicalTraining(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id,MedicalTraining updatedTraining){
        MedicalTraining oldTraining = service.updateMedicalTraining(id, updatedTraining);
        return updatedTraining != null ? Response.ok(updatedTraining).build() : Response.status(Response.Status.NOT_FOUND).build();

    }

    @DELETE
    @RolesAllowed({ADMIN_ROLE})
    @Path(RESOURCE_PATH_TRAINING_ID_PATH)
    public Response deleteMedicalTraining(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id){
        MedicalTraining training = service.deleteMedicalTraining(id);
        return training != null ? Response.ok(training).build() : Response.status(Response.Status.NOT_FOUND).build();
    }


}