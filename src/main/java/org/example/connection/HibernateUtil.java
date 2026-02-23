package org.example.connection;

import org.example.model.Departamento;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.example.model.Empleado;
import org.example.model.IncidenciaEntity;

public class HibernateUtil {
    private static SessionFactory sessionFactory;

    static {
        try {
            // Cargamos la configuración y registramos las entidades para las relaciones
            sessionFactory = new Configuration().configure("hibernate.cfg.xml")
                    .addAnnotatedClass(Departamento.class)
                    .addAnnotatedClass(Empleado.class)
                    .addAnnotatedClass(IncidenciaEntity.class)
                    .buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Fallo al crear la SessionFactory: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        getSessionFactory().close();
    }
}