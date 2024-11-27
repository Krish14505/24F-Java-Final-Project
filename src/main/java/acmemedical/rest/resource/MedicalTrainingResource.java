package acmemedical.rest.resource;

import acmemedical.ejb.ACMEMedicalService;
import acmemedical.entity.MedicalTraining;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.ws.rs.core.Response.Status;
import java.util.List;

import static acmemedical.utility.MyConstants.*;
import static org.hibernate.id.SequenceMismatchStrategy.LOG;

@Path(MEDICAL_TRAINING_RESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MedicalTrainingResource {

    private static final Logger LOG = LogManager.getLogger();

    @EJB
    protected ACMEMedicalService service;

    @GET
    public Response getMedicalTrainings() {
        LOG.debug("Retrieving all Medical Trainings...");
        List<MedicalTraining> training = service.getAllMedicalTrainings();
        LOG.debug("Medical Trainings found: {}",training);
        return Response.ok(training).build();
    }

    @GET
    @Path((RESOURCE_PATH_TRAINING_ID_PATH))
    public Response getMedicalTrainingById(@PathParam(RESOURCE_PATH_ID_ELEMENT)int medicalTrainingId) {
       LOG.debug("Try to retrieve specific medical training: {} ", medicalTrainingId);
        MedicalTraining training = service.getMedicalTrainingById(medicalTrainingId);
        return Response.ok(training).build();
    }

    @DELETE
    @Path(RESOURCE_PATH_TRAINING_ID_PATH)
    public Response deleteMedicalTraining(@PathParam(TRAINING_ID_RESOURCE_NAME)int medicalTrainingId) {
        LOG.debug("Try to delete specific medical training: {}", medicalTrainingId);
        MedicalTraining training = service.deleteMedicalTraining();
        Response response = Response.ok(training).build();
        return response;
    }

    @POST
    public Response addMedicalTraining(MedicalTraining newTraining) {
        LOG.debug("Try to add medical training: {}", newTraining);

        if(service.isDuplicatedTraining(newTraining)){
            HttpErrorResponse err = new HttpErrorResponse(Status.CONFLICT.getStatusCode(), "Entity already exists");
            return Response.status(Status.CONFLICT).entity(err).build();
        }else {
            MedicalTraining tempMedicalTraining = service.persistMedicalTraining(newTraining);
            return Response.ok(tempMedicalTraining).build();
        }

    }

    @PUT
    @Path(RESOURCE_PATH_TRAINING_ID_PATH)
    public Response updateMedicalTraining(@PathParam(TRAINING_ID_RESOURCE_NAME) int MedicalTrainingId, MedicalTraining updatingMedicalTraining ) {
        LOG.debug("Updating a specific medical training: {}",updatedTraining);

        MedicalTraining updatedMedicalTraining = service.updateMedicalTraining(MedicalTrainingId, updatingMedicalTraining);
        return Response.ok(updatedMedicalTraining).build();
    }





}
