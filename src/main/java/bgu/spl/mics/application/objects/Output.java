package bgu.spl.mics.application.objects;

public class Output {
    OutputStudent[] Students;
    OutputConference[] Conferences;
    int cpuTimedUsed;
    int gpuTimedUsed;
    int batchesProcessed;

    public OutputStudent[] getStudents() {
        return Students;
    }

    public void setStudents(OutputStudent[] students) {
        Students = students;
    }

    public OutputConference[] getConferences() {
        return Conferences;
    }

    public void setConferences(OutputConference[] conferences) {
        Conferences = conferences;
    }

    public int getCpuTimedUsed() {
        return cpuTimedUsed;
    }

    public void setCpuTimedUsed(int cpuTimedUsed) {
        this.cpuTimedUsed = cpuTimedUsed;
    }

    public int getGpuTimedUsed() {
        return gpuTimedUsed;
    }

    public void setGpuTimedUsed(int gpuTimedUsed) {
        this.gpuTimedUsed = gpuTimedUsed;
    }

    public int getBatchesProcessed() {
        return batchesProcessed;
    }

    public void setBatchesProcessed(int batchesProcessed) {
        this.batchesProcessed = batchesProcessed;
    }
}
