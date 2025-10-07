package org.example.Server.message;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class Message {
    private String type;
    private JsonArray key;
    private String value;
    private JsonObject jsonValue;
    private JsonArray array;
    private Integer number;

    public Message(String type, JsonArray key, String message, JsonObject jsonValue, JsonArray array, Integer number){
        this.type = type;
        this.key = key;
        this.value = message;
        this.jsonValue = jsonValue;
        this.array = array;
        this.number = number;
    }

    public Message(String type, JsonArray key, String value, JsonObject jsonValue){
        this(type, key, value, jsonValue, null, null);
    }

    public Object getActualValue(){
        if (!(jsonValue == null || jsonValue.isJsonNull())) {
            return jsonValue;
        } else if (value != null) {
            return value;
        } else if (array != null){
            return array;
        } else if (number != null){
            return number;
        }
        return null;
    }

    @Override
    public String toString(){
        return type + " " + key + " " + value;
    }

    public Message(String type, JsonArray key, String value){
        this(type, key, value, null, null, null);
    }

    public Message(String type, JsonArray key, JsonObject jsonValue){
        this(type, key, null, jsonValue, null, null);
    }

    public Message(String type, JsonArray key, JsonArray array){
        this(type, key, null, null, array, null);
    }

    public Message(String type, JsonArray key, Integer number){
        this(type, key, null, null, null, number);
    }

    public String getType() {return type;}

    public JsonArray getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public void setType(String type){
        this.type = type;
    }

    public void setKey(JsonArray key){
        this.key = key;
    }

    public void setValue(String value){
        this.value = value;
    }

    public JsonObject getJsonValue() {return jsonValue;}

    public void setJsonValue(JsonObject jsonValue) {this.jsonValue = jsonValue;}

    public JsonArray getArray() {
        return array;
    }

    public void setArray(JsonArray array) {
        this.array = array;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }
}

