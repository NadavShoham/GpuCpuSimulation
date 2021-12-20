package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;

/**
 * GPU service is responsible for handling the
 * {@link TrainModelEvent} and {@link TestModelEvent},
 * in addition to sending the {@link DataPreProcessEvent}.
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
        // TODO Implement this

    }
}
