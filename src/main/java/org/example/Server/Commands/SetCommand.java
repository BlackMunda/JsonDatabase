package org.example.Server.Commands;

import com.google.gson.*;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

// setting nested values but setting new for json objects (FIXED)
// TODO : addValue values in the array (if not internal key) if the key has array(Json) as value instead of overwriting

public class SetCommand implements Command {
    private static JsonObject db;
    private static JsonArray key;
    private static String value;
    private static JsonObject jsonValue;
    private static JsonArray array;
    private static Integer number;


    public SetCommand(JsonObject dbObject, JsonArray Key, String Value, JsonObject jSonValue, JsonArray Array, Integer Number) {
        db = dbObject;
        key = Key;
        value = Value;
        jsonValue = jSonValue;
        array = Array;
        number = Number;
    }

    public SetCommand(JsonObject dbObject, JsonArray key, String value) {
        this(dbObject, key, value, null, null, null);
    }

    public SetCommand(JsonObject dbObject, JsonArray key, JsonObject jsonValue) {
        this(dbObject, key, null, jsonValue, null, null);
    }

    public SetCommand(JsonObject dbObject, JsonArray key, JsonArray array) {
        this(dbObject, key, null, null, array, null);
    }

    public SetCommand(JsonObject dbObject, JsonArray key, Integer number) {
        this(dbObject, key, null, null, null, number);
    }

    @Override
    public String execute() {

        try (FileReader reader = new FileReader("/home/dracarys/IdeaProjects/javaD" +
                "/DatabaseJSON/src/main/java/org/example/Server/Data/db.json");
             FileWriter writer = new FileWriter("/home/dracarys/IdeaProjects/javaD" +
                     "/DatabaseJSON/src/main/java/org/example/Server/Data/db.json")){

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            lock.writeLock().lock();

            traverse(db);

            gson.toJson(db, writer);
            writer.flush();
            db = gson.fromJson(reader, JsonObject.class);
            return "{\"response\":\"OK\"}";
        } catch (IndexOutOfBoundsException | IOException e) {
            return "{\"response\":\"ERROR\",\"reason\":\"No such keys\"}";
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
                if (!(keys.length > 1)) lastJsonObj = object;
                count++;
                if (keys.length > count && keys.length > 1) traverse((JsonObject) Value);
                else {
                    addValue(Key);
                    count = 0;
                }
            } else{
                addValue(Key);
                count = 0;
            }
        } catch (Exception e) {
            JsonObject newJsonObj = new JsonObject();
            JsonElement Value = object.get(Key);
            newJsonObj.add(prevKey, Value);
            count++;

            if (keys.length == count){
                addValue(prevKey);
            } else {
                lastJsonObj.add(prevKey, newJsonObj);
                lastJsonObj = newJsonObj;
                traverse(lastJsonObj);
            }
            count = 0;
        }
    }

    public static void addValue(String Key){
        if (!(jsonValue == null || jsonValue.isJsonNull())) {
            lastJsonObj.add(Key, jsonValue);
        } else if (value != null) {
            lastJsonObj.addProperty(Key, value);
        } else if (number != null) {
            lastJsonObj.addProperty(Key, number);
        } else if (array != null) {
            lastJsonObj.add(Key, array);
        }
    }
}
