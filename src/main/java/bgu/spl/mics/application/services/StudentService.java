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
    public StudentService(String name) {
        super("Change_This_Name");
        // TODO Implement this
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
}
