package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.services.ConferenceService;

import java.util.LinkedList;
import java.util.List;

/**
 * Passive object representing information on a conference.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class ConfrenceInformation {

    private String name;
    private int date;
    List<Model> results;
    private int currTime;
    private ConferenceService conferenceService;
    private OutputConference outputConference;

    public OutputConference getOutputConference() {
        return outputConference;
    }

    public void setOutputConference(OutputConference outputConference) {
        this.outputConference = outputConference;
    }

    public ConfrenceInformation(String name, int date) {
        this.name = name;
        this.date = date;
        this.results = new LinkedList<>();
        this.currTime = 1;
    }

    public void setConferenceService(ConferenceService conferenceService) {
        this.conferenceService = conferenceService;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void aggregateSuccessfulModels(Model model){
        results.add(model);
    }

    public void publishResults(){
        conferenceService.publishResults(results.size());
    };

    public void updateTime(int tickTime) {
        currTime = tickTime;
        if (currTime == date)
            publishResults();
    }

    public void prepareForTermination(){
        OutputConference outputConference = new OutputConference();
        outputConference.setName(name);
        outputConference.setDate(date);
        OutputModel[] tmpOutputPublications = new OutputModel[results.size()];
        int i = 0;
        for (Model model: results){
            OutputModel modelToAdd = new OutputModel();
            if (model.getStatus() == Model.Status.Trained || model.getStatus() == Model.Status.Tested) {
                modelToAdd.setName(model.getName());
                OutputData outputData = new OutputData();
                outputData.setType(model.getData().getType().toString());
                outputData.setSize(model.getData().getSize());
                modelToAdd.setStatus(model.getStatus().toString());
                modelToAdd.setResults(model.getResults().toString());
                modelToAdd.setData(outputData);
                tmpOutputPublications[i++] = modelToAdd;
            }
        }
        outputConference.setPublications(tmpOutputPublications);
        this.outputConference = outputConference;
    }
}
