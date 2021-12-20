package bgu.spl.mics.application.objects;

import java.util.List;

public class Input {
    InputStudent[] Students;
    String[] GPUS;
    int[] CPUS;
    InputConference[] Conferences;
    int TickTime;
    int Duration;

    public InputStudent[] getStudents() {
        return Students;
    }

    public void setStudents(InputStudent[] students) {
        Students = students;
    }

    public String[] getGPUS() {
        return GPUS;
    }

    public void setGPUS(String[] GPUS) {
        this.GPUS = GPUS;
    }

    public int[] getCPUS() {
        return CPUS;
    }

    public void setCPUS(int[] CPUS) {
        this.CPUS = CPUS;
    }

    public InputConference[] getConferences() {
        return Conferences;
    }

    public void setConferences(InputConference[] conferences) {
        Conferences = conferences;
    }

    public int getTickTime() {
        return TickTime;
    }

    public void setTickTime(int tickTime) {
        TickTime = tickTime;
    }

    public int getDuration() {
        return Duration;
    }

    public void setDuration(int duration) {
        Duration = duration;
    }
}

