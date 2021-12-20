package bgu.spl.mics.application.objects;

/**
 * Passive object representing a Deep Learning model.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Model {
    enum Status {PreTrained, Training, Trained, Tested}
    enum Results {None, Good, Bad}

    private String name;
    private Data data;
    private Student student;
    private Status status;
    private Results results;

    public Model(String name, Data data, Student student) {
        this.name = name;
        this.data = data;
        this.student = student;
        this.status = Status.PreTrained;
        this.results = Results.None;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Results getResults() {
        return results;
    }

    public void setResults(String results) {
        Results result = null;
        switch (results){
            case "Good":
                result = Results.Good;
                break;
            case "Bad":
                result = Results.Bad;
        }
        this.results = result;
    }

    public Data getData() {
        return data;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Model{" +
                "name='" + name + '\'' +
                '}';
    }
}
