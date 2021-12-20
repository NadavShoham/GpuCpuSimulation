package bgu.spl.mics.application.objects;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU {

    private final int cores;
    private Collection<DataBatch> data;
    private DataBatch dataBatch;
    private final Cluster cluster;
    private final int id;
    private int startTime;
    private int timeUsed;
    private int currTime;
    private int speed;
    private int currGPUID;
    private int DBCounter;

    public CPU(int cores, Cluster cluster, int id){
        this.cores = cores;
        this.cluster = cluster;
        this.startTime = 1;
        this.id = id;
        this.timeUsed = 0;
        this.DBCounter = 0;
        this.data = new LinkedList<>();
    }

    public int getStartTime() {
        return startTime;
    }

    public DataBatch getDataBatch() {
        return dataBatch;
    }

    public void setData(Collection<DataBatch> data) {
        this.data = data;
    }

    public void setDataBatch(DataBatch dataBatch) {
        this.dataBatch = dataBatch;
    }

    public Collection<DataBatch> getData() {
        return data;
    }

    public int getCurrTime() {
        return currTime;
    }

    /**
     * send data batches to cluster
     * @INV: none
     * @PRE: none
     * @POST: getData().isEmpty() == true
     * */
    public void sendData(){
        cluster.receiveDataFromCPU(currGPUID, data);
    }

    /**
     * main method of CPU, used to initiate a new dataBatch process and
     * to process the data according to the computation limit of the CPU
     * and finally end process
     * @INV: none
     * @PRE: none
     * @POST: none
     * */
    public void process() {
        if (dataBatch == null) initiateDB();
        else if (currTime >= startTime + speed) finishDB();
        else increaseTimeUsed();
    }

    /**
     * increases the time used counter of CPU
     * @INV: none
     * @PRE: getTimeUsed() >= 0
     * @POST: getTimeUsed() > 0 && getTimeUsed = @pre getTimeUsed() + 1
     **/
    public void increaseTimeUsed() {
        timeUsed++;
    }

    /**
     * initiates a process of a new dataBatch if available
     * @INV: none
     * @PRE: getDataBatch() == null
     * @POST: none
     **/
    public void initiateDB() {
        // Starting process of a new batch
        dataBatch = cluster.sendDataToCPU(cores);

        // no dataBatch available
        if (dataBatch == null) {
            sendData();
            return;
        }

        // initializing
        startTime = currTime;

        // change of GPU, send all batches of processed data back
        if (currGPUID != dataBatch.getGPUId()) {
            sendData();
            // Initializing speed
            switch (dataBatch.getData().getType()) {
                case Images:
                    speed = (32 / cores) * 4;
                    break;
                case Text:
                    speed = (32 / cores) * 2;
                    break;
                case Tabular:
                    speed = (32 / cores);
            }
        }
        currGPUID = dataBatch.getGPUId();
    }

    /**
     * ends the process of current DataBatch
     * @INV: none
     * @PRE: getDataBatch() != null
     * @POST: getDBCounter() = @pre getDBCounter + 1
     *        getDataBatch() == null
     *        data.contains(@pre: getDataBatch())
     **/
    public void finishDB() {
        // finished batch
        increaseTimeUsed();
        DBCounter++;
        dataBatch.getData().incrementProcessed();
        data.add(dataBatch);
        dataBatch = null;
    }

    /**
     * updates the time of the CPU
     * @param tickTime the current time
     * @INV: currTime > 0
     * @INV: tickTime > currTime
     * @PRE: none
     * @POST: none
     * */
    public void updateTime(int tickTime) {
        currTime = tickTime;
        process();
    }

    public void prepareForTermination(){
        cluster.updateStatisticsCPU(timeUsed, DBCounter);
    }

    public int getTimeUsed() {
        return timeUsed;
    }

    public int getDBCounter() {
        return DBCounter;
    }
}
