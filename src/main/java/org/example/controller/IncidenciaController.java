package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.model.Empleado;
import org.example.model.EstadoIncidencia;
import org.example.model.IncidenciaEntity;
import org.example.repository.RelationalRepository;
import org.example.repository.IncidenciaRepository;
import org.bson.Document;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class IncidenciaController {

    @FXML private TextField txtTitulo;
    @FXML private TextArea txtDescripcion;
    @FXML private ComboBox<Empleado> cmbEmpleado;
    @FXML private TextField txtNombreCliente;
    @FXML private TextField txtTelefonoCliente;
    @FXML private TextField txtEmailCliente;
    
    // Campos para búsqueda
    @FXML private TextField txtBuscarId;
    @FXML private VBox panelInfo;
    @FXML private Label lblId;
    @FXML private Label lblTitulo;
    @FXML private Label lblCliente;
    @FXML private Label lblTelefono;
    @FXML private Label lblEmail;
    @FXML private Label lblEmpleado;
    @FXML private Label lblFecha;
    @FXML private Label lblEstadoActual;
    @FXML private ComboBox<EstadoIncidencia> cmbEstado;
    
    // Campos para vista de logs MongoDB
    @FXML private TableView<Document> tblLogs;
    @FXML private TableColumn<Document, String> colId;
    @FXML private TableColumn<Document, String> colAccion;
    @FXML private TableColumn<Document, String> colUsuario;
    @FXML private TableColumn<Document, String> colDetalle;
    @FXML private TableColumn<Document, String> colFecha;
    @FXML private Label lblTotalLogs;
    
    @FXML private BorderPane mainPane;

    private final RelationalRepository relacionalRepo = new RelationalRepository();
    private final IncidenciaRepository mongoRepo = new IncidenciaRepository();
    
    private IncidenciaEntity incidenciaActual; // Para guardar la incidencia que se está editando

    // --- NAVEGACIÓN DINÁMICA ---
    private void cargarPanel(String rutaFxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFxml));
            loader.setController(this); // Usamos la misma instancia del controlador
            VBox panel = loader.load();
            mainPane.setCenter(panel);
        } catch (IOException e) {
            System.err.println("Error al cargar " + rutaFxml + ": " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML 
    public void mostrarFormularioCrear() { 
        cargarPanel("/view/formulario_incidencia.fxml");
        cargarEmpleadosEnComboBox();
    }
    
    @FXML public void mostrarBuscar() { cargarPanel("/view/busqueda_incidencia.fxml"); }
    
    @FXML 
    public void mostrarEstado() { 
        cargarPanel("/view/TableView.fxml");
        configurarTablaLogs();
        verTodosLosLogs();
    }
    
    private void cargarEmpleadosEnComboBox() {
        if (cmbEmpleado != null) {
            cmbEmpleado.getItems().clear();
            List<Empleado> empleados = relacionalRepo.obtenerTodosEmpleados();
            cmbEmpleado.getItems().addAll(empleados);
        }
    }

    @FXML
    public void volverInicio() {
        try {
            System.out.println("Volviendo al inicio...");
            
            // Cargar la vista principal
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/incidencias.fxml"));
            javafx.scene.Parent root = loader.load();
            
            // Obtener el Stage actual desde cualquier nodo
            javafx.stage.Stage stage = null;
            if (mainPane != null) {
                stage = (javafx.stage.Stage) mainPane.getScene().getWindow();
            } else if (txtTitulo != null) {
                stage = (javafx.stage.Stage) txtTitulo.getScene().getWindow();
            } else if (tblLogs != null) {
                stage = (javafx.stage.Stage) tblLogs.getScene().getWindow();
            }
            
            if (stage != null) {
                // Crear nueva escena con la vista principal
                javafx.scene.Scene scene = new javafx.scene.Scene(root, 800, 500);
                stage.setScene(scene);
                System.out.println("Vuelto al inicio correctamente");
            } else {
                System.err.println("No se pudo obtener el Stage");
            }
        } catch (Exception e) {
            System.err.println("Error al volver al inicio: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --- LÓGICA DE PERSISTENCIA HÍBRIDA ---
    @FXML
    public void onGuardar() {
        if (txtTitulo == null || txtDescripcion == null || txtNombreCliente == null) return;

        // Validar campos obligatorios
        String titulo = txtTitulo.getText();
        String nombreCliente = txtNombreCliente.getText();
        
        if (titulo == null || titulo.trim().isEmpty()) {
            mostrarAlerta("Error", "El título es obligatorio");
            return;
        }
        
        if (nombreCliente == null || nombreCliente.trim().isEmpty()) {
            mostrarAlerta("Error", "El nombre del cliente es obligatorio");
            return;
        }
        
        // Validar que se haya seleccionado un empleado
        Empleado empleadoSeleccionado = null;
        if (cmbEmpleado != null) {
            empleadoSeleccionado = cmbEmpleado.getValue();
        }
        
        if (empleadoSeleccionado == null) {
            mostrarAlerta("Error", "Debe asignar la incidencia a un empleado");
            return;
        }

        try {
            // 1. SQL (Hibernate) - Guardar incidencia con datos del cliente
            String telefono = txtTelefonoCliente != null ? txtTelefonoCliente.getText() : "";
            String email = txtEmailCliente != null ? txtEmailCliente.getText() : "";
            
            IncidenciaEntity nueva = new IncidenciaEntity(
                titulo.trim(), 
                nombreCliente.trim(), 
                telefono.trim(), 
                email.trim()
            );
            
            // Asignar el empleado seleccionado
            nueva.setEmpleado(empleadoSeleccionado);
            
            relacionalRepo.guardar(nueva);

            // 2. NoSQL (MongoDB) - EVENTO de auditoría (NO copia de datos)
            Document log = new Document("incidencia_id", nueva.getId())
                    .append("usuario", "Usuario_Sesion")
                    .append("accion", "CREAR_INCIDENCIA")
                    .append("descripcion", "Incidencia creada: " + titulo.trim())
                    .append("empleado_asignado", empleadoSeleccionado.getNombre())
                    .append("departamento", empleadoSeleccionado.getDepartamento().getNombre())
                    .append("fecha", new java.util.Date());
            mongoRepo.guardarDocumento(log);

            mostrarAlerta("Éxito",
                "Incidencia guardada correctamente:\n" +
                "- ID: " + nueva.getId() + "\n" +
                "- Cliente: " + nombreCliente + "\n" +
                "- Asignada a: " + empleadoSeleccionado.getNombre() + "\n" +
                "- Registrado en MySQL y MongoDB");
            
            // Limpiar formulario
            txtTitulo.clear(); 
            txtDescripcion.clear();
            txtNombreCliente.clear();
            if (txtTelefonoCliente != null) txtTelefonoCliente.clear();
            if (txtEmailCliente != null) txtEmailCliente.clear();
            if (cmbEmpleado != null) cmbEmpleado.setValue(null);
        } catch (Exception e) {
            mostrarAlerta("Error", e.getMessage());
        }
    }
    
    // ================== VISUALIZACIÓN DE LOGS MONGODB ==================
    
    private void configurarTablaLogs() {
        if (tblLogs == null || colId == null) return;
        
        // Configurar columnas para mostrar datos del Document
        colId.setCellValueFactory(data -> {
            Object id = data.getValue().get("incidencia_id");
            return new javafx.beans.property.SimpleStringProperty(id != null ? id.toString() : "-");
        });
        
        colAccion.setCellValueFactory(data -> {
            Object accion = data.getValue().get("accion");
            return new javafx.beans.property.SimpleStringProperty(accion != null ? accion.toString() : "-");
        });
        
        colUsuario.setCellValueFactory(data -> {
            Object usuario = data.getValue().get("usuario");
            return new javafx.beans.property.SimpleStringProperty(usuario != null ? usuario.toString() : "-");
        });
        
        colDetalle.setCellValueFactory(data -> {
            // Buscar primero "descripcion", luego "detalle" (retrocompatibilidad)
            Object detalle = data.getValue().get("descripcion");
            if (detalle == null) {
                detalle = data.getValue().get("detalle");
            }
            String texto = detalle != null ? detalle.toString() : "-";
            return new javafx.beans.property.SimpleStringProperty(texto.length() > 50 ? texto.substring(0, 47) + "..." : texto);
        });
        
        colFecha.setCellValueFactory(data -> {
            Object fecha = data.getValue().get("fecha");
            return new javafx.beans.property.SimpleStringProperty(fecha != null ? fecha.toString() : "-");
        });
    }
    
    @FXML
    public void verTodosLosLogs() {
        try {
            System.out.println("Consultando TODOS los logs de MongoDB...");
            
            // Obtener todos los documentos de la colección
            List<Document> logs = mongoRepo.obtenerTodosLosLogs();
            
            System.out.println("Logs obtenidos: " + logs.size());
            
            if (tblLogs != null) {
                ObservableList<Document> items = FXCollections.observableArrayList(logs);
                tblLogs.setItems(items);
            }
            
            if (lblTotalLogs != null) {
                lblTotalLogs.setText("Total de logs: " + logs.size());
            }
            
            if (logs.isEmpty()) {
                mostrarAlerta("Información", 
                    "No hay logs registrados aún.\n\n" +
                    "Los logs se crean cuando:\n" +
                    "- Creas una nueva incidencia\n" +
                    "- Cambias el estado de una incidencia");
            } else {
                System.out.println("Mostrando " + logs.size() + " logs en la tabla");
            }
            
        } catch (Exception e) {
            System.err.println("Error al cargar logs: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta("Error", "Error al cargar logs de MongoDB: " + e.getMessage());
        }
    }
    
    @FXML
    private void mostrarAlerta(String titulo, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setContentText(msg);
        alert.showAndWait();
    }
    
    // ================== BÚSQUEDA Y ACTUALIZACIÓN DE ESTADO ==================
    
    @FXML
    public void buscarIncidenciaPorId() {
        if (txtBuscarId == null) return;
        
        String idTexto = txtBuscarId.getText();
        if (idTexto == null || idTexto.trim().isEmpty()) {
            mostrarAlerta("Error", "Ingrese un ID de incidencia");
            return;
        }
        
        try {
            Long id = Long.parseLong(idTexto.trim());
            IncidenciaEntity incidencia = relacionalRepo.buscarPorId(id);
            
            if (incidencia == null) {
                mostrarAlerta("No encontrado", "No existe incidencia con ID: " + id);
                panelInfo.setVisible(false);
                panelInfo.setManaged(false);
                return;
            }
            
            // Guardar para actualizar después
            incidenciaActual = incidencia;
            

            Document logConsulta = new Document("incidencia_id", incidencia.getId())
                    .append("usuario", "Usuario_Sesion")
                    .append("accion", "CONSULTAR_INCIDENCIA")
                    .append("descripcion", "Consulta de incidencia: " + incidencia.getTitulo())
                    .append("fecha", new java.util.Date());
            mongoRepo.guardarDocumento(logConsulta);
            
            // Mostrar información
            lblId.setText(String.valueOf(incidencia.getId()));
            lblTitulo.setText(incidencia.getTitulo());
            lblCliente.setText(incidencia.getNombreCliente());
            lblTelefono.setText(incidencia.getTelefonoCliente() != null ? incidencia.getTelefonoCliente() : "-");
            lblEmail.setText(incidencia.getEmailCliente() != null ? incidencia.getEmailCliente() : "-");
            lblEmpleado.setText(incidencia.getEmpleado() != null ? incidencia.getEmpleado().getNombre() : "No asignado");
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            lblFecha.setText(incidencia.getFechaCreacion() != null ? incidencia.getFechaCreacion().format(formatter) : "-");
            
            lblEstadoActual.setText(incidencia.getEstado() != null ? incidencia.getEstado().toString() : "Sin estado");
            
            // Cargar estados en ComboBox
            if (cmbEstado != null) {
                cmbEstado.getItems().clear();
                cmbEstado.getItems().addAll(EstadoIncidencia.values());
                cmbEstado.setValue(incidencia.getEstado());
            }
            
            // Mostrar panel
            panelInfo.setVisible(true);
            panelInfo.setManaged(true);
            
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "El ID debe ser un número válido");
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al buscar incidencia: " + e.getMessage());
        }
    }
    
    @FXML
    public void actualizarEstado() {
        if (incidenciaActual == null) {
            mostrarAlerta("Error", "Primero debe buscar una incidencia");
            return;
        }
        
        if (cmbEstado == null || cmbEstado.getValue() == null) {
            mostrarAlerta("Error", "Seleccione un estado");
            return;
        }
        
        try {
            EstadoIncidencia nuevoEstado = cmbEstado.getValue();
            EstadoIncidencia estadoAnterior = incidenciaActual.getEstado();
            
            // Actualizar estado
            incidenciaActual.setEstado(nuevoEstado);
            relacionalRepo.actualizar(incidenciaActual);
            
            // REGISTRAR EVENTO EN MONGODB (solo el cambio, no los datos)
            Document log = new Document("incidencia_id", incidenciaActual.getId())
                    .append("usuario", "Usuario_Sesion")
                    .append("accion", "CAMBIO_ESTADO")
                    .append("descripcion", "Estado cambiado de '" + 
                            (estadoAnterior != null ? estadoAnterior.toString() : "Sin estado") + 
                            "' a '" + nuevoEstado.toString() + "'")
                    .append("estado_anterior", estadoAnterior != null ? estadoAnterior.name() : null)
                    .append("estado_nuevo", nuevoEstado.name())
                    .append("fecha", new java.util.Date());
            mongoRepo.guardarDocumento(log);
            
            // Actualizar vista
            lblEstadoActual.setText(nuevoEstado.toString());
            
            mostrarAlerta("Éxito", 
                "Estado actualizado correctamente:\n" +
                "- Incidencia ID: " + incidenciaActual.getId() + "\n" +
                "- Nuevo estado: " + nuevoEstado.toString());
                
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al actualizar estado: " + e.getMessage());
        }
    }
}