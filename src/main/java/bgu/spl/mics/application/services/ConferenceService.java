package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.PublishConfrenceBroadcast;
import bgu.spl.mics.application.messages.PublishResultsEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.ConfrenceInformation;

import java.util.List;

/**
 * Conference service is in charge of
 * aggregating good results and publishing them via the {@link PublishConfrenceBroadcast},
 * after publishing results the conference will unregister from the system.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ConferenceService extends MicroService {
    private final ConfrenceInformation conference;

    public ConferenceService(String name, ConfrenceInformation conference) {
        super(name);
        this.conference = conference;
    }

    @Override
    protected void initialize() {
        // Subscribe to publishResult
        subscribeEvent(PublishResultsEvent.class, (PublishResultsEvent event)-> {conference.aggregateSuccessfulModels(event.getModel());});

        // Subscribe to Ticks
        subscribeBroadcast(TickBroadcast.class, (TickBroadcast b)-> {conference.updateTime(b.getCurrTime());});

        // Subscribe to Terminate
        subscribeBroadcast(TerminateBroadcast.class, (TerminateBroadcast b)->{
            terminateConference();});
    }

    public void publishResults(int numOfPublications) {
        sendBroadcast(new PublishConfrenceBroadcast(numOfPublications));
        // TODO print
        //System.out.println(getName() + " sending conference ");
        terminateConference();
    }

    private void terminateConference() {
        conference.prepareForTermination();
        terminate();
        // TODO print
        //System.out.println(getName() + " terminating");
    }
}
