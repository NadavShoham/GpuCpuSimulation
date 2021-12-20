package bgu.spl.mics.application.objects;


import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Passive object representing the cluster.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Cluster {
	private static class SingletonHolder {
		private static final Cluster instance = new Cluster();
	}

	private final Object gpuLock;
	private final Object cpuLock;
	private List<GPU> gpus;
	private List<CPU> cpus;
	private final Statistics statistics;
	private final ConcurrentHashMap<Integer, BlockingQueue<DataBatch>> gpuQueueMap;
	private final BlockingQueue<DataBatch> cpuImagesQueue;
	private final BlockingQueue<DataBatch> cpuTextQueue;
	private final BlockingQueue<DataBatch> cpuTabularQueue;

	public Cluster() {
		this.statistics = new Statistics();
		this.gpuQueueMap = new ConcurrentHashMap<>();
		this.cpuImagesQueue = new LinkedBlockingQueue<>();
		this.cpuTextQueue = new LinkedBlockingQueue<>();
		this.cpuTabularQueue = new LinkedBlockingQueue<>();
		gpuLock = new Object();
		cpuLock = new Object();
	}

	public void setGpus(List<GPU> gpus) {
		this.gpus = gpus;
		initiateGPUQueueMap();
	}

	public void setCpus(List<CPU> cpus) {
		this.cpus = cpus;
		initiateSpeed();
	}

	private void initiateSpeed() {

	}

	private void initiateGPUQueueMap() {
		for (GPU gpu: gpus) {
			gpuQueueMap.put(gpu.getId(), new LinkedBlockingQueue<>());
		}
	}

	public void receiveDataFromGPU(Queue<DataBatch> dataBatches, Data.Type type) {
		synchronized (gpuLock) {
			switch (type) {
				case Images:
					cpuImagesQueue.addAll(dataBatches);
					break;
				case Text:
					cpuTextQueue.addAll(dataBatches);
					break;
				case Tabular:
					cpuTabularQueue.addAll(dataBatches);
					break;
			}
		}
	}

	public void receiveDataFromCPU(int GPUid, Collection<DataBatch> dataBatches) {
		synchronized (gpus.get(GPUid)) {
			gpuQueueMap.get(GPUid).addAll(dataBatches);
		}
		dataBatches.clear();
	}

	public Queue<DataBatch> sendDataToGPU(int GPUid, int GPUCapacity) {
		Queue<DataBatch> toSend = new LinkedList<>();
		BlockingQueue<DataBatch> gpuQueue = gpuQueueMap.get(GPUid);

		// building the list to send according to GPU limit and batches available, if none available will return an empty list
		for (int i = 0; i < Math.min(gpuQueue.size(), GPUCapacity); i++) toSend.add(gpuQueue.poll());
		return toSend;
	}

	public DataBatch sendDataToCPU(int cores) {
		synchronized (cpuLock) {
			DataBatch batch;
			if (cores >= 32) {
				batch = cpuImagesQueue.poll();
				if (batch == null) batch = cpuTextQueue.poll();
				if (batch == null) batch = cpuTabularQueue.poll();
				return batch;
			} else if (cores >= 4) {
				batch = cpuTextQueue.poll();
				if (batch == null) batch = cpuTabularQueue.poll();
				if (batch == null) batch = cpuImagesQueue.poll();
				return batch;
			} else {
				batch = cpuTabularQueue.poll();
				if (batch == null) batch = cpuTextQueue.poll();
				if (batch == null) batch = cpuImagesQueue.poll();
				return batch;
			}
		}
	}

	public Statistics getStatistics() {
		return statistics;
	}

	public void updateStatisticsCPU(int CpuTime, int numOfDBProcessed) {
		statistics.incrementCpuTime(CpuTime);
		statistics.incrementDBProcessed(numOfDBProcessed);
	}

	public void updateStatisticsGPU(int GpuTime, List<String> trainedModels) {
		statistics.incrementGpuTime(GpuTime);
		statistics.addToModelNames(trainedModels);
	}

	/**
	 * gets the instance of the thread safe singleton
	 * @return singleton instance*/
	public static Cluster getInstance() {
		return Cluster.SingletonHolder.instance;
	}



	// TODO delete after fixing tests
	public void TransferDataToGPU(int numOfBatches, int GPUid) {
		Collection<DataBatch> processedData;
	}

	public void TransferDataToCPU(int CPUid, int GPUid) {
		Collection<DataBatch> unProcessedData;
	}


}
