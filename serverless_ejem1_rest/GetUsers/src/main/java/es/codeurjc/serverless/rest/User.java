package es.codeurjc.serverless.rest;

class User {

    private String userId;
    private String username;
    private String name;
    private String lastname;

    public User() {
    }

    public User(String userId, String username, String name, String lastname) {
        this.userId = userId;
        this.username = username;
        this.name = name;
        this.lastname = lastname;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

}
