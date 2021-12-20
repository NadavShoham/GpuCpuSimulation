package bgu.spl.mics.application.services;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.GPU;
import bgu.spl.mics.application.objects.Model;

import java.util.LinkedList;
import java.util.Queue;

/**
 * GPU service is responsible for handling the
 * {@link TrainModelEvent} and {@link TestModelEvent},
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class GPUService extends MicroService {
    private final GPU gpu;
    private final Queue<TrainModelEvent> TrainWaitingQueue;
    private final Queue<TestModelEvent> TestWaitingQueue;
    private TrainModelEvent currTrainingModelEvent;
    private boolean receivedTerminate;
    private MessageBusImpl bus;

    public GPUService(String name, GPU gpu) {
        super(name);
        this.gpu = gpu;
        this.TrainWaitingQueue = new LinkedList<>();
        this.TestWaitingQueue = new LinkedList<>();
        receivedTerminate = false;
        bus = MessageBusImpl.getInstance();
    }


    @Override
    protected void initialize() {
        // Subscribe to test
        subscribeEvent(TestModelEvent.class, (TestModelEvent event) -> {
            if (gpu.isAvailable())
                activateTest(event);
            else
                TestWaitingQueue.add(event);
        });

        // Subscribe to train
        subscribeEvent(TrainModelEvent.class, (TrainModelEvent event) -> {
            if (gpu.isAvailable())
                activateTrain(event);
            else
                TrainWaitingQueue.add(event);
        });

        // Subscribe to ticks
        subscribeBroadcast(TickBroadcast.class, (TickBroadcast b)-> {gpu.updateTime(b.getCurrTime());});
        
        // Subscribe to terminate
        subscribeBroadcast(TerminateBroadcast.class, (TerminateBroadcast b)->{
            if (receivedTerminate){
                terminate();
                // TODO print
                //System.out.println(getName() + " terminating");
            }
            receivedTerminate = true;

            sendBroadcast(new TerminateBroadcast());
            // release all waiting studentServices from future.get()
            if (currTrainingModelEvent != null) completeTrain(null);
            for (TestModelEvent event: TestWaitingQueue) complete(event, null);
            for (TrainModelEvent event: TrainWaitingQueue) complete(event, null);
            TestWaitingQueue.clear();
            TrainWaitingQueue.clear();
            gpu.prepareForTermination();
        });


    }

    /**
     * The gpu uses this function to signal the gpuService to complete the train event
     * @param model the updated model, only the processed field in data field has changed and should be equal to data.size
     * */
    public void completeTrain(Model model) {
        complete(currTrainingModelEvent, model);
        currTrainingModelEvent = null;
    }

    /**
     * The gpu uses this function to signal the gpuService to activate the next event in queue
     * */
    public void activateNext(){
        TestModelEvent testEvent = TestWaitingQueue.poll();
        if (testEvent != null) {
            activateTest(testEvent);
        } else {
            TrainModelEvent trainEvent = TrainWaitingQueue.poll();
            if (trainEvent == null) return;
            activateTrain(trainEvent);
        }
    }

    /**
     * activates a train model event
     * @param event the event to be activated
     * */
    public void activateTrain(TrainModelEvent event) {
        // TODO print
        //System.out.println(getName() + " activating " + event.toString());
        currTrainingModelEvent = event;
        gpu.activateTrainModel(event.getModel());
    }

    /**
     * activates a test model event
     * @param event the event to be activated
     * */
    private void activateTest(TestModelEvent event) {
        // TODO print
        //System.out.println(getName() + " activating " + event.toString());
        String result = gpu.activateTestModel(event.getModel());
        complete(event, result);
    }


}
