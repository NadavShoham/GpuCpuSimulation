package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

public class TickBroadcast implements Broadcast {
    private int currTime;

    public TickBroadcast(int currTime){
        this.currTime = currTime;
    }

    public int getCurrTime() {
        return currTime;
    }

    public void setCurrTime(int currTime) {
        this.currTime = currTime;
    }
}
