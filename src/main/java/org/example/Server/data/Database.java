package org.example.Server.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.FileReader;

public class Database {

    private static JsonObject database;

    private Database() {}

    public static JsonObject getInstance() {
        try (FileReader reader = new FileReader("/home/dracarys/IdeaProjects/javaD" +
                "/DatabaseJSON/src/main/java/org/example/Server/data/db.json")){

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            database = gson.fromJson(reader, JsonObject.class);

        } catch (Exception e){
            System.out.println("Database file not found");
        }
        if (database == null) database = new JsonObject();
        if (database.entrySet().isEmpty() || database.isEmpty() || database.isJsonNull()) {
            database = new JsonObject();
        }
        return database;
    }
}

