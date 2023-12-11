package com.mikrosoft.consumer.dao;

import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class DB {

    private String connectionString = "mongodb+srv://yuc:rhVqQiBrA0cDkvuL@cluster0.xoz7ds9.mongodb.net/?retryWrites=true&w=majority";
    //private MongoDatabase database;
    //private MongoCollection<Document> collection;

    public void DB(){
      /**  ServerApi serverApi = ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .serverApi(serverApi)
                .build();
        // Create a new client and connect to the server
        try (MongoClient mongoClient = MongoClients.create(settings)) {
            try {
                // Send a ping to confirm a successful connection
                MongoDatabase database = mongoClient.getDatabase("DS23");
                database.runCommand(new Document("ping", 1));
                System.out.println("Pinged your deployment. You successfully connected to MongoDB!");
            } catch (MongoException e) {
                e.printStackTrace();
            }
        }*/
    }

    public boolean queryUser(String name, String psw){
        try (MongoClient mongoClient = MongoClients.create(connectionString)) {
            MongoDatabase database = mongoClient.getDatabase("DS23");
            System.out.println("1");
            MongoCollection<Document> collection = database.getCollection("auth");
            Document query = new Document("$and",
                    List.of(
                            new Document("name", name),
                            new Document("password", psw)
                    )
            );
            Document result = collection.find(query).first();
           // Document doc = collection.find(eq("name", "user1")).first();
            if (result != null) return true;
            else return false;
        }

    }
}
