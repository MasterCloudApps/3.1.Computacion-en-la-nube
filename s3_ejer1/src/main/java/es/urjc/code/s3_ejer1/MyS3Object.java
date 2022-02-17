package es.urjc.code.s3_ejer1;

import java.time.Instant;

public class MyS3Object {
    
    private String name;

    private Instant createdAt;

    public MyS3Object() {
    }

    public MyS3Object(String name, Instant createdAt) {
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
