package es.codeurjc.serverless.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserRepository {

    private final List<User> users = new ArrayList<>();

    public UserRepository() {
        users.add(new User(UUID.randomUUID().toString() + "-1", "franrobles", "Francisco", "Robles"));
        users.add(new User(UUID.randomUUID().toString() + "-2", "anajohnson", "Ana", "Johnson"));
    }

    public List<User> getAllUsers() {
        return users;
    }

}
