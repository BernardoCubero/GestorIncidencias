package org.example.connection;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.example.model.IncidenciaEntity;

public class HibernateUtil {
    private static SessionFactory sessionFactory;

    static {
        try {
            // Carga la configuración del hibernate.cfg.xml
            sessionFactory = new Configuration()
                    .configure("hibernate.cfg.xml")
                    .addAnnotatedClass(IncidenciaEntity.class)
                    .buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Error al crear la SessionFactory: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            sessionFactory = new Configuration().configure("hibernate.cfg.xml")
                    .addAnnotatedClass(IncidenciaEntity.class).buildSessionFactory();
        }
        return sessionFactory;
    }
}