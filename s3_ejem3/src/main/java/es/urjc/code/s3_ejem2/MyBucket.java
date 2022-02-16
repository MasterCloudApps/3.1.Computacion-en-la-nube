package es.urjc.code.s3_ejem2;

import java.time.Instant;

public class MyBucket {

    private String name;

    private Instant createdAt;

    public MyBucket() {
    }

    public MyBucket(String name, Instant createdAt) {
        this.name = name;
        this.createdAt = createdAt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    
    
}
