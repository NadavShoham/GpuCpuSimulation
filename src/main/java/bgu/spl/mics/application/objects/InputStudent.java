package bgu.spl.mics.application.objects;

public class InputStudent {
    String name;
    String department;
    String status;
    InputModel[] models;

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

    public InputModel[] getModels() {
        return models;
    }

    public void setModels(InputModel[] models) {
        this.models = models;
    }
}
