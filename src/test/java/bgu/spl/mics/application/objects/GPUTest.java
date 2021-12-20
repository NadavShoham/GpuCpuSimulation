package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.services.GPUService;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.LinkedList;

import static org.junit.Assert.*;

public class GPUTest {

    private static GPU gpu;
    private static GPUService gpuService;
    private static Cluster cluster;
    private static Data data;
    private static Model model;
    private static Model model2;
    private static Student student;
    private Collection<DataBatch> dataFromCluster;
    private Data bigData;
    private DataBatch dataBatch1;
    private DataBatch dataBatch2;
    private DataBatch dataBatch3;
    private DataBatch dataBatch4;
    private DataBatch dataBatch5;
    private DataBatch dataBatch6;
    private DataBatch dataBatch7;
    private DataBatch dataBatch8;

    @Before
    public void setUp() throws Exception {
        cluster = Cluster.getInstance();
        gpu = new GPU("RTX1080", cluster, 0);
        LinkedList<GPU> gpus = new LinkedList<>();
        gpus.add(gpu);
        cluster.setGpus(gpus);
        gpuService = new GPUService("g", gpu);
        gpu.setGpuService(gpuService);
        data = new Data("Text", 1000);
        student = new Student("Shalom", "CS", "MSc");
        model = new Model("shalomModel", data, student);
        gpu.setModel(model);
        bigData = new Data("Images", 8000);
        model2 = new Model("shalomModel2", bigData, student);
        dataBatch1 = new DataBatch(bigData, 0,0);
        dataBatch2 = new DataBatch(bigData, 1000, 0 );
        dataBatch3 = new DataBatch(bigData, 2000, 0);
        dataBatch4 = new DataBatch(bigData, 3000, 0);
        dataBatch5 = new DataBatch(bigData, 4000, 0);
        dataBatch6 = new DataBatch(bigData, 5000, 0);
        dataBatch7 = new DataBatch(bigData, 6000, 0);
        dataBatch8 = new DataBatch(bigData, 7000, 0);
        dataFromCluster = new LinkedList<>();
        dataFromCluster.add(dataBatch1);
        dataFromCluster.add(dataBatch2);
        dataFromCluster.add(dataBatch3);
        dataFromCluster.add(dataBatch4);
        dataFromCluster.add(dataBatch5);
        dataFromCluster.add(dataBatch6);
        dataFromCluster.add(dataBatch7);

    }

    @Test
    public void testPrepareBatches() {
        gpu.prepareBatches();
        assertEquals("wrong num of batches",gpu.UnProcessedDataSize() * 1000, model.getData().getSize());
    }

    @Test
    public void testSendData() {
        gpu.prepareBatches();
        int before = gpu.UnProcessedDataSize();
        gpu.sendData();
        assertTrue("data not sent correctly",gpu.UnProcessedDataSize() >= before);
    }

    @Test
    public void testActivateTrainModel() {
        gpu.activateTrainModel(model2);
        assertEquals(model2.getStatus(), Model.Status.Training);
        assertFalse(gpu.isAvailable());
    }

    @Test
    public void testActivateTestModel() {
        gpu.getModel().setStatus(Model.Status.Trained);
        String result = gpu.activateTestModel(model2);
        assertEquals("wrong status for model", gpu.getModel().getStatus(), Model.Status.Tested);
        assertNotEquals("result didnt change", result, "None");
    }

    @Test
    public void testUpdateTime() {
        int before = gpu.getCurrTime();
        gpu.updateTime(2);
        assertTrue(before < gpu.getCurrTime());
    }

    @Test
    public void testFinishTrain() {
        gpu.setNumOfBatchesLeftToTrain(2);
        int before = gpu.getNumOfBatchesLeftToTrain();
        gpu.finishTrain();
        assertEquals(before, gpu.getNumOfBatchesLeftToTrain() + 1);
    }

    @Test
    public void testIncreaseTimeUsed(){
        int before = gpu.getTimeUsed();
        gpu.increaseTimeUsed();
        assertEquals("did not increment",before + 1, gpu.getTimeUsed());
    }

}