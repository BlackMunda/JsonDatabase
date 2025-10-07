package org.example.Server.commands;

import com.google.gson.*;
import org.example.Server.commands.response.Response;

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
                return gson.toJson(new Response("ERROR", "no such key!!"));
            } else {
                JsonElement value = traverse(db);
                if (value.isJsonObject()) {
                    return gson.toJson(new Response("OK", value.getAsString()));
                } else if (value.isJsonPrimitive()) {
                    var actual = value.getAsJsonPrimitive();
                    if (actual.isString()){
                        return gson.toJson(new Response("OK", actual.getAsString()));
                    }
                    if (actual.isNumber()){
                        return gson.toJson(new Response("OK", actual.getAsString()));
                    }
                } else if (value.isJsonArray()) {
                    return gson.toJson(new Response("OK", value.getAsString()));
                }
            }
        } catch (IndexOutOfBoundsException e){
            Gson gson = new Gson();
            return gson.toJson(new Response("ERROR", "no such key!!"));
        } finally {
            lock.readLock().unlock();
        }
        return "not done";
    }
}
