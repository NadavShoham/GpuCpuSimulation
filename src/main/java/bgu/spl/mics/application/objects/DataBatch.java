package bgu.spl.mics.application.objects;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */

public class DataBatch {
    private Data data;
    private int start_index;
    private int GPUId;

    DataBatch(Data data, int start_index, int GPUId){
        this.data = data;
        this.start_index = start_index;
        this.GPUId = GPUId;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public int getStart_index() {
        return start_index;
    }

    public int getGPUId() {
        return GPUId;
    }
}
