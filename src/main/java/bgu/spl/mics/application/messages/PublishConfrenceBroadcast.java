package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

import java.util.List;

public class PublishConfrenceBroadcast implements Broadcast {
    int results;

    public PublishConfrenceBroadcast(int numOfPublications) {
        this.results = numOfPublications;
    }

    public int getResults() {
        return results;
    }

    public void setResults(int results) {
        this.results = results;
    }
}
