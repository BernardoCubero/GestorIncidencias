package org.example.repository;

import com.mongodb.client.MongoCollection;
import org.example.connection.MongoDB;
import org.example.model.Incidencia;
import org.bson.Document;
import java.util.ArrayList;
import java.util.List;

public class IncidenciaRepository {

    private final MongoCollection<Document> collection;

    public IncidenciaRepository() {
        this.collection = MongoDB.getDatabase().getCollection("incidencia");
    }

    public void insertar(Incidencia incidencia) {
        Document doc = new Document("titulo", incidencia.getTitulo())
                .append("descripcion", incidencia.getDescripcion());
        collection.insertOne(doc); // Guarda el documento en la nube/local de Mongo
    }

    public List<Document> obtenerListado() {
        return collection.find().into(new ArrayList<>());
    }

}
