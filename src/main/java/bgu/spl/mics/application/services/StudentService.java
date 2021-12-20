package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Student is responsible for sending the {@link TrainModelEvent},
 * {@link TestModelEvent} and {@link PublishResultsEvent}.
 * In addition, it must sign up for the conference publication broadcasts.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class StudentService extends MicroService {
    private final Student student;
    private Iterator<Model> iter;
    private Model currModel;


    public StudentService(String name, Student student) {
        super(name);
        this.student = student;
        iter = student.getModels().iterator();
        currModel = iter.next();
    }

    @Override
    protected void initialize() {
        // Subscribe to publishConference
        subscribeBroadcast(PublishConfrenceBroadcast.class, (PublishConfrenceBroadcast b)->{student.updatePapersRead(b.getResults());});

        // Subscribe to activateNextModel
        subscribeBroadcast(ActivateNextModelBroadcast.class, (ActivateNextModelBroadcast b)-> {activateModelProcess();});

        // Subscribe to Terminate
        subscribeBroadcast(TerminateBroadcast.class, (TerminateBroadcast b)->{
            student.prepareForTermination();
            // TODO print
            //System.out.println(getName() + " terminating");
            terminate();
        });
    }

    public void activateModelProcess() {
        if (currModel != null){
            Future <Model> f = sendEvent(new TrainModelEvent(currModel));
            // TODO print
            //System.out.println(getName() + " sending event " + currModel.getName() + " to train");
            Object result = f.get();
            if (result == null) {
                currModel = null;
                return;
            }
            Future<String> fTest = sendEvent(new TestModelEvent(currModel));
            // TODO print
            //System.out.println(getName() + " sending event " + currModel.getName() + " to test");
            result = fTest.get();
            if (result == null) {
                currModel = null;
                return;
            }
            else if ("Good".equals(result)){
                student.incrementPublications();
                f = sendEvent(new PublishResultsEvent(currModel));
                // TODO print
                //System.out.println(getName() + " sending event " + currModel.getName() + " to publish");
            }
            try {
                currModel = iter.next();
            } catch (NoSuchElementException e) {
                currModel = null;
            }
            sendBroadcast(new ActivateNextModelBroadcast());
        }

    }
}
