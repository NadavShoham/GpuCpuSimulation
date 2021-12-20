package bgu.spl.mics.application.objects;

public class OutputStudent {
    String name;
    String department;
    String status;
    int publications;
    int papersRead;
    OutputModel[] models;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getPublications() {
        return publications;
    }

    public void setPublications(int publications) {
        this.publications = publications;
    }

    public int getPapersRead() {
        return papersRead;
    }

    public void setPapersRead(int papersRead) {
        this.papersRead = papersRead;
    }

    public OutputModel[] getModels() {
        return models;
    }

    public void setModels(OutputModel[] models) {
        this.models = models;
    }
}
