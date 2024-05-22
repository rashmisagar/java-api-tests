package utils;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class MongoDBManager {

    private MongoClient mongoClient;
    private MongoDatabase database;

    public MongoDBManager(String connectionString, String databaseName) {
        mongoClient = MongoClients.create(connectionString);
        database = mongoClient.getDatabase(databaseName);
    }

    public MongoClient getMongoClient() {
        return mongoClient;
    }

    public MongoDatabase getDatabase() {
        return database;
    }

    public void cleanupCollections(String... collectionNames) {
        for (String collectionName : collectionNames) {
            MongoCollection<Document> collection = database.getCollection(collectionName);
            collection.deleteMany(new Document());
        }
    }

    public void close() {
        mongoClient.close();
    }
}
