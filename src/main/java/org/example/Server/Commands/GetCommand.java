package org.example.Server.Commands;

import com.google.gson.*;

// TODO : when given nested key, get is returning the last present key if the latter nested one does not exist.

public class GetCommand implements Command {
    private final JsonObject db;
    private static JsonArray key;

    public GetCommand(JsonObject dbObj, JsonArray key) {
        db = dbObj;
        GetCommand.key = key;
    }

    static int count = 0;
    static JsonElement result = null;
    public static JsonElement traverse(JsonObject object){
        Gson gson = new GsonBuilder().create();
        String[] keys = gson.fromJson(key, String[].class);
        String Key = keys[count].trim();
        JsonElement value = object.get(Key);

        if (value.isJsonObject()){
            count++;
            if (keys.length > count) traverse((JsonObject) value);
            else {
                result = value;
                count = 0;
            }
        } else{
            result = value;
            count = 0;
        }
        return result;
    }

    @Override
    public String execute() {
        try {
            lock.readLock().lock();
            Gson gson = new GsonBuilder().create();
            String[] keys = gson.fromJson(key, String[].class);

            if (!db.has(keys[0])){
                return "{\"response\":\"ERROR\",\"reason\":\"No such key\"}";
            } else {
                JsonElement value = traverse(db);
                if (value.isJsonObject()) {
                    return "{\"response\":\"OK\",\"value\":" + value.getAsJsonObject() + "}";
                } else if (value.isJsonPrimitive()) {
                    var actual = value.getAsJsonPrimitive();
                    if (actual.isString()){
                        return "{\"response\":\"OK\",\"value\":" + "\"" + actual.getAsString() + "\"}";
                    }
                    if (actual.isNumber()){
                        return "{\"response\":\"OK\",\"value\":" + "\"" + actual.getAsNumber() + "\"}";
                    }
                } else if (value.isJsonArray()) {
                    return "{\"response\":\"OK\",\"value\":" + value.getAsJsonArray() + "}";
                }
            }
        } catch (IndexOutOfBoundsException e){
            return "{\"response\":\"ERROR\",\"reason\":\"No such key\"}";
        } finally {
            lock.readLock().unlock();
        }
        return "not done";
    }
}
