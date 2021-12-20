package bgu.spl.mics.application.objects;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Data {
    /**
     * Enum representing the Data type.
     */
    enum Type {
        Images, Text, Tabular
    }

    private Type type;
    private AtomicInteger processed;
    private int size;

    public Data(String type, int size) {
        this.size = size;
        switch (type) {
            case "Images":
                this.type = Type.Images;
                break;
            case "Text":
                this.type = Type.Text;
                break;
            case "Tabular":
                this.type = Type.Tabular;
                break;
        }
        this.processed = new AtomicInteger(0);
    }

    public int getSize() {
        return size;
    }

    public AtomicInteger getProcessed() {
        return processed;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void incrementProcessed() {
        int val;
        do {
            val = processed.get();
        } while (!processed.compareAndSet(val, val + 1000));
    }



}
