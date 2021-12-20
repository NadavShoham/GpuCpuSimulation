package bgu.spl.mics.application.objects;

import java.util.List;

/**
 * Passive object representing single student.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Student {
    /**
     * Enum representing the Degree the student is studying for.
     */
    enum Degree {
        MSc, PhD
    }

    private String name;
    private String department;
    private Degree status;
    private int publications;
    private int papersRead;
    private List<Model> models;
    private OutputStudent outputStudent;

    public OutputStudent getOutputStudent() {
        return outputStudent;
    }

    public List<Model> getModels() {
        return models;
    }

    public void setModels(List<Model> models) {
        this.models = models;
    }

    public Student(String name, String department, String status) {
        this.name = name;
        this.department = department;
        switch (status) {
            case "MSc":
                this.status = Degree.MSc;
                break;
            case "PhD":
                this.status = Degree.PhD;
        }
        this.publications = 0;
        this.papersRead = 0;
    }

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

    public Degree getStatus() {
        return status;
    }

    public void setStatus(Degree status) {
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

    public void updatePapersRead(int size) {
        papersRead += size;
    }

    public void incrementPublications() {
        publications++;
    }

    public void prepareForTermination(){
        papersRead -= publications;
        OutputStudent outputStudent = new OutputStudent();
        outputStudent.setName(name);
        outputStudent.setDepartment(department);
        outputStudent.setStatus(status.toString());
        outputStudent.setPublications(publications);
        outputStudent.setPapersRead(papersRead);
        int size = 0;
        for (Model model: models) {
            if (model.getStatus() == Model.Status.Trained || model.getStatus() == Model.Status.Tested)size++;
        }
        OutputModel[] tmpOutputModels = new OutputModel[size];
        int i = 0;
        for (Model model: models){
            if (model.getStatus() == Model.Status.Trained || model.getStatus() == Model.Status.Tested) {
                OutputModel modelToAdd = new OutputModel();
                modelToAdd.setName(model.getName());
                OutputData outputData = new OutputData();
                outputData.setType(model.getData().getType().toString());
                outputData.setSize(model.getData().getSize());
                modelToAdd.setStatus(model.getStatus().toString());
                modelToAdd.setResults(model.getResults().toString());
                modelToAdd.setData(outputData);
                tmpOutputModels[i++] = modelToAdd;
            }
        }
        outputStudent.setModels(tmpOutputModels);
        this.outputStudent = outputStudent;
    }


}
