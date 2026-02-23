package org.example.repository;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.example.connection.MongoDB;
import java.util.ArrayList;
import java.util.List;

public class IncidenciaRepository {
    private final MongoCollection<Document> logs;

    public IncidenciaRepository() {
        this.logs = MongoDB.getDatabase().getCollection("auditoria_incidencias");
    }

    /**
     * Guardar un documento en MongoDB y exportar automáticamente a JSON
     */
    public void guardarDocumento(Document doc) {
        try {
            System.out.println("Guardando en MongoDB...");
            System.out.println("Documento: " + doc.toJson());
            logs.insertOne(doc);
            System.out.println("Documento guardado exitosamente en MongoDB");
            
            // Exportar automáticamente a JSON después de guardar
            exportarAutomaticamente();
            
        } catch (Exception e) {
            System.err.println("Error al guardar en MongoDB: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Obtener todos los logs ordenados por fecha descendente
     */
    public List<Document> obtenerTodosLosLogs() {
        try {
            System.out.println("Obteniendo todos los logs de MongoDB...");
            List<Document> resultado = logs.find()
                    .sort(new Document("fecha", -1))
                    .into(new ArrayList<>());
            System.out.println("Logs obtenidos: " + resultado.size());
            return resultado;
        } catch (Exception e) {
            System.err.println("Error al obtener logs: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Exportar automáticamente todos los logs a JSON
     */
    private void exportarAutomaticamente() {
        try {
            List<Document> todosLosLogs = logs.find()
                    .sort(new Document("fecha", -1))
                    .into(new ArrayList<>());
            
            if (todosLosLogs.isEmpty()) {
                return; // No hay nada que exportar
            }
            
            java.io.File archivo = new java.io.File("logs_mongodb_export.json");
            java.io.FileWriter writer = new java.io.FileWriter(archivo);
            
            writer.write("[\n");
            for (int i = 0; i < todosLosLogs.size(); i++) {
                Document doc = todosLosLogs.get(i);
                writer.write("  " + doc.toJson());
                if (i < todosLosLogs.size() - 1) {
                    writer.write(",");
                }
                writer.write("\n");
            }
            writer.write("]\n");
            writer.close();
            
            System.out.println("JSON actualizado automáticamente: " + archivo.getAbsolutePath());
            System.out.println("   Total de eventos: " + todosLosLogs.size());
            
        } catch (Exception e) {
            System.err.println("Error al exportar automáticamente: " + e.getMessage());
            // No lanzamos la excepción para no afectar el guardado principal
        }
    }
}
