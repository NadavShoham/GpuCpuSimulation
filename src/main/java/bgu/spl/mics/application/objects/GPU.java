package bgu.spl.mics.application.objects;


import bgu.spl.mics.application.services.GPUService;

import java.util.*;

/**
 * Passive object representing a single GPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class GPU {
    /**
     * Enum representing the type of the GPU.
     */
    enum Type {RTX3090, RTX2080, GTX1080}
    private boolean available;
    private Type type;
    private Model model;
    private final Cluster cluster;
    private int GPUCapacity;
    private final int id;
    private int numOfBatchesLeftToTrain;
    private Queue<DataBatch> unProcessedDataBatches;
    private Queue<DataBatch> processedDataBatches;
    private DataBatch dataBatch;
    private int startTime;
    private int timeUsed;
    private int currTime;
    private int speed;
    private GPUService gpuService;
    private final List<String> nameOfTrainedModels;

    public GPU(String type, Cluster cluster, int id) {
        this.cluster = cluster;
        this.id = id;
        this.available = true;
        this.startTime = 1;
        this.timeUsed = 0;
        this.processedDataBatches = new LinkedList<>();
        this.nameOfTrainedModels = new LinkedList<>();

        switch (type) {
            case "RTX3090":
                this.type = Type.RTX3090;
                this.GPUCapacity = 32;
                this.speed = 1;
                break;
            case "RTX2080":
                this.type = Type.RTX2080;
                this.GPUCapacity = 16;
                this.speed = 2;
                break;
            case "GTX1080":
                this.type = Type.GTX1080;
                this.GPUCapacity = 8;
                this.speed = 4;
                break;
        }
    }

    public int getId() {
        return id;
    }

    public Collection<DataBatch> getProcessedDataBatches() {
        return processedDataBatches;
    }

    public Model getModel() {
        return model;
    }

    public Type getType() {
        return type;
    }

    public int getCurrTime() {
        return currTime;
    }

    public boolean isAvailable() {
        return available;
    }

    public boolean isFull(){
        return getNumOfProcessedDataBatches() >= GPUCapacity;
    }

    public int UnProcessedDataSize(){
        return unProcessedDataBatches.size();
    }

    public int getNumOfProcessedDataBatches() {
        return processedDataBatches.size();
    }

    public int getNumOfBatchesLeftToTrain() {
        return numOfBatchesLeftToTrain;
    }

    public void setGpuService(GPUService gpuService) {
        this.gpuService = gpuService;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public void setNumOfBatchesLeftToTrain(int numOfBatchesLeftToTrain) {
        this.numOfBatchesLeftToTrain = numOfBatchesLeftToTrain;
    }

    /**
     * divide data to batches in the unProcessDataBatches
     * @INV: getModel().getData() != null
     * @PRE: none
     * @POST: getModel().getData().size() == UnProcessedDataSize() * 1000
     * */
    public void prepareBatches() {
        Data data = model.getData();
        Queue<DataBatch> dataBatches = new LinkedList<>();

        // Creates the dataBatches and sets the unProcessedDataBatches list
        for (int i = 0; i < data.getSize(); i = i + 1000) dataBatches.add(new DataBatch(data, i, id));
        unProcessedDataBatches = dataBatches;
        numOfBatchesLeftToTrain = dataBatches.size();
    }

    /**
     * send data batches to cluster according to capacity
     * @INV: none
     * @PRE: UnProcessedDataSize() > 0
     * @POST: UnProcessedDataSize() >= @Pre: UnProcessedDataSize()
     * */
    public void sendData(){
        // TODO decide whats x value
        int x = GPUCapacity * 3;
        Queue<DataBatch> toSend = new LinkedList<>();
        for (int i = 0; i < Math.min(UnProcessedDataSize(), x); i++) toSend.add(unProcessedDataBatches.poll());
        cluster.receiveDataFromGPU(toSend, model.getData().getType());
    }

    /**
     * start train model process
     * @INV: none
     * @PRE: model.getStatus() == Model.Status.PreTrained
     *       isAvailable() == true
     * @POST: model.getStatus() == Model.Status.Training
     *        isAvailable() == true
     * */
    public void activateTrainModel(Model nextModel) {
        available = false;
        model = nextModel;
        model.setStatus(Model.Status.Training);
        prepareBatches();
        sendData();
    }

    /**
     * determines whether the test of the model was successful or not
     * @return a string with the result of the test
     * @INV: none
     * @PRE: model.getStatus() == Model.Status.Trained
     *       model.getResults() == Model.Results.None
     * @POST: model.getStatus() == Model.Status.Tested
     *        model.getResults() != Model.Results.None
     * */
    public String activateTestModel(Model nextModel) {
        available = false;
        model = nextModel;
        String result;
        double random = Math.random();

        if (model.getStudent().getStatus() == Student.Degree.MSc){
            if (random <= 0.6)
                result =  "Good";
            else
                result =  "Bad";
        } else{
            if (random <= 0.8)
                result = "Good";
            else
                result = "Bad";
        }
        model.setStatus(Model.Status.Tested);
        model.setResults(result);
        available = true;
        activateNext();
        return result;
    }

    /**
     * updates the time of the GPU
     * @param tickTime the current time
     * @INV: currTime > 0
     * @INV: tickTime > currTime
     * @PRE: none
     * @POST: none
     * */
    public void updateTime(int tickTime) {
        currTime = tickTime;
        train();
    }

     /**
     * trains the processed data
     * @INV: none
     * @PRE: none
     * @POST: none
     * */
    public void train() {
        if (dataBatch == null) initiateTrain();
        else if (currTime >= startTime + speed) finishTrain();
        else increaseTimeUsed();
    }

    /**
     * initiates a training session of a new dataBatch if available
     * @INV: none
     * @PRE: getDataBatch() == null
     * @POST: none
     * */
    public void initiateTrain(){
        // Starting train of a new batch
        dataBatch = processedDataBatches.poll();

        // no data in queue try to refill queue from cluster
        if (dataBatch == null) {
            processedDataBatches = cluster.sendDataToGPU(id, GPUCapacity);
            dataBatch = processedDataBatches.poll();
            // no dataBatch available
            if (dataBatch == null) return;

            // we received more batches, time to send more to CPU
            sendData();
        }
        // initializing
        startTime = currTime;
    }

    /**
     * finishes the training session of the dataBatch
     * @INV: none
     * @PRE: getDataBatch() != null
     * @POST: getNumOfBatches() == @pre: getNumOfBatches() + 1
     * */
    public void finishTrain(){
        // finished batch
        timeUsed++;
        dataBatch = null;
        numOfBatchesLeftToTrain -= 1;
        if (numOfBatchesLeftToTrain <= 0) {

            // finished training of currModel
            complete();
            nameOfTrainedModels.add(model.getName());
            model.setStatus(Model.Status.Trained);
            available = true;
            activateNext();
        }
    }

    /**
     * increases the time used counter of CPU
     * @INV: none
     * @PRE: getTimeUsed() >= 0
     * @POST: getTimeUsed() > 0 && getTimeUsed = @pre getTimeUsed() + 1
     **/
    public void increaseTimeUsed() {
        // training
        timeUsed++;
    }

    private void activateNext() {
        gpuService.activateNext();
    }

    private void complete(){
        gpuService.completeTrain(model);
    }

    public void prepareForTermination(){
        cluster.updateStatisticsGPU(timeUsed, nameOfTrainedModels);
    }

    public int getTimeUsed() {
        return timeUsed;
    }

}
