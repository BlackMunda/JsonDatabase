package org.example.Server.network;

import com.google.gson.*;
import org.example.Server.commands.Command;
import org.example.Server.commands.DeleteCommand;
import org.example.Server.commands.GetCommand;
import org.example.Server.commands.SetCommand;
import org.example.Server.execution.CommandInvoker;
import org.example.Server.message.Message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

import static org.example.Server.Main.db;
import static org.example.Server.Main.shouldExit;

public class ClientHandler {

    public static void runner(Socket socket){
        try (socket;
             DataInputStream input = new DataInputStream(socket.getInputStream());
             DataOutputStream output = new DataOutputStream(socket.getOutputStream())){

            String msgJson;
            try {
                msgJson = input.readUTF();
            } catch (EOFException e) {
                System.out.println("Not read anything");
                return;
            }
            Message msg = new GsonBuilder()
                    .setPrettyPrinting().create().fromJson(msgJson, Message.class);

            String type = msg.getType();

            String result;

            if (type.equals("exit")) {
                result = commandSetter(type, null, null);
            } else {
                JsonArray key = msg.getKey();
                result = commandSetter(type, key, msg.getActualValue());
            }
            try {
                output.writeUTF(result);
                output.flush();
            } catch (IOException e) {
                System.out.println("not written anything to client");
            }
            if (shouldExit) System.exit(0);
        } catch (Exception e){
            System.out.println("Exception in runner");
        }
    }

    static String commandSetter(String type, JsonArray key, Object objectValue) {
        if (type.equals("exit")) {
            new Thread(() -> {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {}
                System.exit(0);
            }).start();
            shouldExit = true;
            return "{\"response\":\"OK\"}";
        }

        Command command = switch (type) {
            case "get" -> new GetCommand(db, key);

            case "set" -> {

                String value;
              JsonObject jsonValue;
                JsonArray array;
                Integer number;

                JsonElement actualValue = new GsonBuilder().create().toJsonTree(objectValue);

                if (actualValue.isJsonObject()){
                    jsonValue = actualValue.getAsJsonObject();
                    yield new SetCommand(db, key, jsonValue);
                } else if (actualValue.isJsonPrimitive()){

                    JsonPrimitive primitive = actualValue.getAsJsonPrimitive();
                    if (primitive.isNumber()){
                        number = primitive.getAsInt();
                        yield new SetCommand(db, key, number);
                    }
                    if (primitive.isString()){
                        value = primitive.getAsString();
                        yield new SetCommand(db, key, value);
                    }
                } else if (actualValue.isJsonArray()){
                    array = actualValue.getAsJsonArray();
                    yield new SetCommand(db, key, array);
                } else yield new SetCommand(db, key, actualValue.getAsJsonObject());
                yield null;
            }

            case "delete" -> new DeleteCommand(db, key);
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };

        CommandInvoker executor = new CommandInvoker(command);
        return executor.run();
    }
}
