package org.example.Client;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.google.gson.*;
import org.example.Server.message.Message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;


// TODO : EXECUTES ONLY ONE COMMAND AND DISCONNECTS
// TODO : parses string for getting json elements (custom jcommander)

public class Main {
    private static final String address = "127.0.0.1";
    private static final int port = 23456;

    @Parameter(names = "-t")
    String type;

    @Parameter(names = "-k")
    String key;

    @Parameter(names = "-v")
    String value = null;

    @Parameter(names = "-in")
    String path = null;

    public static void main(String[] args) throws InterruptedException {
        Main main = new Main();
        JCommander.newBuilder().addObject(main).build().parse(args);
        main.client();
    }

    public void client() throws InterruptedException {
        System.out.println("Client started!");
        try (Socket socket = new Socket(address, port);
             DataOutputStream output = new DataOutputStream(socket.getOutputStream());
             DataInputStream input = new DataInputStream(socket.getInputStream())) {

            Message msg = null;
            JsonArray keyArray = null;
            if (key != null) keyArray = JsonParser.parseString(key).getAsJsonArray();

            if (value != null) {
                JsonElement parsed = JsonParser.parseString(value);

                if (parsed.isJsonObject()) {
                    msg = new Message(type, keyArray, parsed.getAsJsonObject());
                } else if (parsed.isJsonArray()) {
                    msg = new Message(type, keyArray, parsed.getAsJsonArray());
                } else if (parsed.isJsonPrimitive()) {
                    JsonPrimitive primitive = parsed.getAsJsonPrimitive();
                    if (primitive.isNumber()) {
                        msg = new Message(type, keyArray, primitive.getAsInt());
                    } else if (primitive.isString()) {
                        msg = new Message(type, keyArray, primitive.getAsString());
                    }
                }
            } else {
                msg = new Message(type, keyArray, (String) null);
            }

            // for reading input from a file
            if (path != null){
                msg = inputFileRead(path);
            }


            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String msgJson = gson.toJson(msg);

            output.writeUTF(msgJson);
            output.flush();

            System.out.println("Sent: " + msgJson);
            System.out.println("Received: " + input.readUTF());

        } catch (IOException e) {
            System.out.println("Exception occurred in main");
        } finally {
            Thread.sleep(20000);
            System.out.println("Client disconnected!");
        }
    }

    public Message inputFileRead(String path){

        Message message = new Message(null, null, null, null);

        try (FileInputStream reader = new FileInputStream(path)){
            String[] command = new String(reader.readAllBytes(), StandardCharsets.UTF_8).split(" ");
            JCommander.newBuilder().addObject(this).build().parse(command);
            JsonArray arr = JsonParser.parseString(key).getAsJsonArray();
            message.setType(type);
            message.setKey(arr);
            if (value != null) {
                JsonElement parsed = JsonParser.parseString(value);
                if (parsed.isJsonObject()) {
                    message.setJsonValue(parsed.getAsJsonObject());
                } else if (parsed.isJsonArray()) {
                    message.setArray(parsed.getAsJsonArray());
                } else if (parsed.isJsonPrimitive()) {
                    JsonPrimitive primitive = parsed.getAsJsonPrimitive();
                    if (primitive.isNumber()) {
                        message.setNumber(primitive.getAsInt());
                    } else if (primitive.isString()) {
                        message.setValue(primitive.getAsString());
                    }
                }
            }
            else message.setValue(null);
            return message;
        } catch (Exception e){
            System.out.println("Exception occurred in client");
        }
        return message;
    }
}
