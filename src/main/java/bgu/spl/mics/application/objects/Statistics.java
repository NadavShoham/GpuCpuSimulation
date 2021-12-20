package bgu.spl.mics.application.objects;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Statistics {
    private BlockingQueue<String> namesOfModelsTrained;
    private AtomicInteger numOfProcessedDataBatches;
    private AtomicInteger numOfCPUTimeUnits;
    private AtomicInteger numOfGPUTimeUnits;

    public Statistics() {
        namesOfModelsTrained = new LinkedBlockingQueue<>();
        numOfProcessedDataBatches = new AtomicInteger(0);
        numOfGPUTimeUnits = new AtomicInteger(0);
        numOfCPUTimeUnits = new AtomicInteger(0);
    }

    public BlockingQueue<String> getNamesOfModelsTrained() {
        return namesOfModelsTrained;
    }

    public AtomicInteger getNumOfProcessedDataPatches() {
        return numOfProcessedDataBatches;
    }

    public AtomicInteger getNumOfCPUTimeUnits() {
        return numOfCPUTimeUnits;
    }

    public AtomicInteger getNumOfGPUTimeUnits() {
        return numOfGPUTimeUnits;
    }

    public void incrementCpuTime(int cpuTime) {
        int val;
        do {
            val = numOfCPUTimeUnits.get();
        } while (!numOfCPUTimeUnits.compareAndSet(val, val + cpuTime));
    }

    public void incrementGpuTime(int gpuTime){
        int val;
        do{
            val = numOfGPUTimeUnits.get();
        } while (!numOfGPUTimeUnits.compareAndSet(val, val + gpuTime));
    }

    public void incrementDBProcessed(int DBCounter) {
        int val;
        do {
            val = numOfProcessedDataBatches.get();
        } while (!numOfProcessedDataBatches.compareAndSet(val, val + DBCounter));
    }

    public void addToModelNames(List<String> namesOfModels) {
        namesOfModelsTrained.addAll(namesOfModels);
    }


}
