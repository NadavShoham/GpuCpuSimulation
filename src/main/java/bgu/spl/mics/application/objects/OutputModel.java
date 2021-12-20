package bgu.spl.mics.application.objects;

public class OutputModel {
    String name;
    OutputData data;
    String status;
    String results;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OutputData getData() {
        return data;
    }

    public void setData(OutputData data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResults() {
        return results;
    }

    public void setResults(String results) {
        this.results = results;
    }
}
