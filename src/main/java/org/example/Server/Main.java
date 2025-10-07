package org.example.Server;

import com.google.gson.JsonObject;
import org.example.Server.data.Database;
import org.example.Server.network.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;


public class Main {
    static int PORT = 23456;
    public static JsonObject db = Database.getInstance();
    public static boolean shouldExit = false;

    public static void main(String[] args){
        System.out.println("Server started!");

        try(ServerSocket server = new ServerSocket(PORT)){
            while(true){
                Socket socket = server.accept();
                System.out.println("A client connected!");

                CompletableFuture<Void> future = CompletableFuture.runAsync(
                        () ->
                        ClientHandler.runner(socket));
            }
        } catch (IOException e) {
            System.out.println("Exception occurred in main");
        } finally {
            System.out.println("Server closed!");
        }
    }
}