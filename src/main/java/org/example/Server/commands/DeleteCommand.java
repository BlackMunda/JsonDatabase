package org.example.Server.commands;

import com.google.gson.*;
import org.example.Server.commands.response.Response;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class DeleteCommand implements Command {
    private static JsonObject db;
    private static JsonArray key;

    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public DeleteCommand(JsonObject dbObject, JsonArray Key) {
        db = dbObject;
        key = Key;
    }

    @Override
    public String execute() {
        try (FileReader reader = new FileReader("src/main/java/org/example/Server/Data/db.json");
             FileWriter writer = new FileWriter("src/main/java/org/example/Server/Data/db.json")){
            lock.writeLock().lock();

            String[] keys = gson.fromJson(key, String[].class);

            if (!db.has(keys[0])){
                return gson.toJson(new Response("ERROR", "no such key!!"));
            }
            else {

                traverse(db);

                gson.toJson(db, writer);
                writer.flush();
                db = gson.fromJson(reader, JsonObject.class);
            }
            return gson.toJson(new Response("Ok", null));
        } catch (IndexOutOfBoundsException | IOException e){
            return gson.toJson(new Response("ERROR", "no such key!!"));
        } finally {
            lock.writeLock().unlock();
        }
    }


    static int count = 0;
    static String prevKey = null;
    static JsonObject lastJsonObj;
    public static void traverse(JsonObject object){
        Gson gson = new GsonBuilder().create();
        String[] keys = gson.fromJson(key, String[].class);
        String Key = keys[count].trim();
        if (lastJsonObj == null) lastJsonObj = db;

        try {
            JsonElement Value = object.get(Key);
            prevKey = Key;

            if (Value.isJsonObject()){
                lastJsonObj = Value.getAsJsonObject();
                count++;
                if (keys.length > count) traverse((JsonObject) Value);
                else {
                    lastJsonObj.remove(Key);
                    count = 0;
                }
            } else{
                lastJsonObj.remove(Key);
                count = 0;
            }
        } catch (Exception e) {
            lastJsonObj.remove(prevKey);
            count = 0;
        }
    }
}
