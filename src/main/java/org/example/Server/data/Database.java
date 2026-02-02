package org.example.Server.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.nio.file.*;

import java.io.Reader;

public class Database {

    private static JsonObject database;

    private static final Path dbPath = Paths.get(System.getProperty("user.dir"))
            .resolve("data/db.json");

    private Database() {
    }

    public static JsonObject getInstance() {
        try (
                Reader reader = Files.newBufferedReader(dbPath);) {

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            database = gson.fromJson(reader, JsonObject.class);

        } catch (Exception e) {
            System.out.println("Database file not found");
        }
        if (database == null)
            database = new JsonObject();
        if (database.entrySet().isEmpty() || database.isEmpty() || database.isJsonNull()) {
            database = new JsonObject();
        }
        return database;
    }
}
