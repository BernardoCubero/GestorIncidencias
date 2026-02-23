package org.example.connection;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MongoDB {
    private static final String URI = "mongodb://localhost:27017";
    private static MongoClient mongoClient;
    private static MongoDatabase database;

    public static MongoDatabase getDatabase() {
        if (mongoClient == null) {
            try {
                System.out.println("Conectando a MongoDB en: " + URI);
                mongoClient = MongoClients.create(URI);
                database = mongoClient.getDatabase("gestionIncidencias");
                System.out.println("Conectado a MongoDB - Base de datos: gestionIncidencias");
            } catch (Exception e) {
                System.err.println("ERROR: No se pudo conectar a MongoDB");
                System.err.println("   Asegúrate de que MongoDB esté corriendo en localhost:27017");
                System.err.println("   Error: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return database;
    }
    
    public static void close() {
        if (mongoClient != null) {
            mongoClient.close();
            System.out.println("Conexión MongoDB cerrada");
        }
    }
}