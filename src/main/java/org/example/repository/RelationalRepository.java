package org.example.repository;

import org.example.connection.HibernateUtil;
import org.example.model.Empleado;
import org.example.model.IncidenciaEntity;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class RelationalRepository {


    /**
     * CREATE: Inserta una nueva incidencia en la base de datos.
     */
    public void guardar(IncidenciaEntity incidencia) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(incidencia);
            tx.commit();
        } catch (Exception e) {
            System.err.println("Error al guardar incidencia: " + e.getMessage());
        }
    }

    /**
     * READ: Busca una incidencia por su identificador único.
     */
    public IncidenciaEntity buscarPorId(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(IncidenciaEntity.class, id);
        }
    }

    /**
     * UPDATE: Actualiza los datos de una incidencia existente.
     */
    public void actualizar(IncidenciaEntity incidencia) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(incidencia);
            tx.commit();
        } catch (Exception e) {
            System.err.println("Error al actualizar incidencia: " + e.getMessage());
        }
    }

    /**
     * DELETE: Elimina una incidencia de la base de datos mediante su ID.
     */
    public void eliminar(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            IncidenciaEntity incidencia = session.get(IncidenciaEntity.class, id);
            if (incidencia != null) {
                session.remove(incidencia);
            }
            tx.commit();
        } catch (Exception e) {
            System.err.println("Error al eliminar incidencia: " + e.getMessage());
        }
    }

    public List<IncidenciaEntity> listarConFiltroYPaginacion(String texto, int pagina, int tamano) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String jpql = "SELECT i FROM IncidenciaEntity i WHERE i.titulo LIKE :busqueda ORDER BY i.id DESC";
            Query<IncidenciaEntity> query = session.createQuery(jpql, IncidenciaEntity.class);

            query.setParameter("busqueda", "%" + texto + "%");

            // Lógica de paginación
            query.setFirstResult(pagina * tamano);
            query.setMaxResults(tamano);

            return query.getResultList();
        }
    }


    public List<IncidenciaEntity> buscarPorRangoFechas(LocalDateTime inicio, LocalDateTime fin) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String jpql = "SELECT i FROM IncidenciaEntity i WHERE i.fechaCreacion BETWEEN :inicio AND :fin ORDER BY i.fechaCreacion ASC";
            return session.createQuery(jpql, IncidenciaEntity.class)
                    .setParameter("inicio", inicio)
                    .setParameter("fin", fin)
                    .getResultList();
        }
    }
    
    /**
     * Obtener todos los empleados para el ComboBox con su departamento
     */
    public List<Empleado> obtenerTodosEmpleados() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String jpql = "SELECT e FROM Empleado e LEFT JOIN FETCH e.departamento ORDER BY e.nombre ASC";
            return session.createQuery(jpql, Empleado.class).getResultList();
        } catch (Exception e) {
            System.err.println("Error al obtener empleados: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}