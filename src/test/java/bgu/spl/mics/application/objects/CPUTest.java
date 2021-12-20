package bgu.spl.mics.application.objects;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CPUTest {

    private static CPU cpu;
    private Cluster cluster;
    private Collection<DataBatch> dataToCluster;
    private Data bigData;
    private DataBatch dataBatch1;
    private DataBatch dataBatch2;
    private DataBatch dataBatch3;
    private GPU gpu;

    @Before
    public void setUp() throws Exception {

        bigData = new Data("Images", 3000);
        dataBatch1 = new DataBatch(bigData, 0, 0);
        dataBatch2 = new DataBatch(bigData, 1000,0);
        dataBatch3 = new DataBatch(bigData, 2000,0);
        dataToCluster = new LinkedList<>();
        cluster = Cluster.getInstance();
        cpu = new CPU(4, cluster,0);
        dataToCluster.add(dataBatch1);
        dataToCluster.add(dataBatch2);
        dataToCluster.add(dataBatch3);
        gpu = new GPU("RTX3090", cluster, 0);
        LinkedList<GPU> gpus = new LinkedList<>();
        gpus.add(gpu);
        cluster.setGpus(gpus);
    }

    @Test
    public void testSendData(){
        cpu.setData(dataToCluster);
        cpu.sendData();
        assertTrue("cpu didnt send data", cpu.getData().isEmpty());
    }

    @Test
    public void testIncreaseTimeUsed(){
        int before = cpu.getTimeUsed();
        cpu.increaseTimeUsed();
        assertEquals("did not increment",before + 1, cpu.getTimeUsed());
    }

    @Test
    public void testFinishDB(){
        cpu.setDataBatch(dataBatch1);
        int before = cpu.getDBCounter();
        cpu.finishDB();
        assertEquals("did not increment",before + 1, cpu.getDBCounter());
        assertTrue("databatch was not added correctly", cpu.getData().contains(dataBatch1));
        assertNull("databatch didnt become null", cpu.getDataBatch());
    }

    @Test
    public void testUpdateTime() {
        int before = cpu.getCurrTime();
        cpu.updateTime(2);
        assertTrue(before < cpu.getCurrTime());
    }


}