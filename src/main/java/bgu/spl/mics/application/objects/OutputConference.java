package bgu.spl.mics.application.objects;

public class OutputConference {
    String name;
    int date;
    OutputModel[] publications;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public OutputModel[] getPublications() {
        return publications;
    }

    public void setPublications(OutputModel[] publications) {
        this.publications = publications;
    }
}
