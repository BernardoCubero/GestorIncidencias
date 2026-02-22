package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.example.connection.HibernateUtil;
import org.example.model.Incidencia;
import org.example.model.IncidenciaEntity;
import org.example.repository.IncidenciaRepository;

public class IncidenciaController {

    @FXML private TextField txtTitulo;
    @FXML private TextArea txtDescripcion;

    private IncidenciaRepository repo = new IncidenciaRepository();

    @FXML
    public void onGuardar() {
        String titulo = txtTitulo.getText();
        String desc = txtDescripcion.getText();

        Incidencia incMongo = new Incidencia(titulo, desc);
        repo.insertar(incMongo);

        try (org.hibernate.Session session = HibernateUtil.getSessionFactory().openSession()) {
            org.hibernate.Transaction tx = session.beginTransaction();

            IncidenciaEntity incRelacional = new IncidenciaEntity(titulo);
            session.persist(incRelacional);

            tx.commit();
        } catch (Exception e) {
            System.out.println("Error en Hibernate: " + e.getMessage());
        }

        txtTitulo.clear();
        txtDescripcion.clear();
        System.out.println("Guardado completado con éxito");
    }
}