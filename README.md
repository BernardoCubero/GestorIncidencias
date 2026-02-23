# Sistema de Gestión de Incidencias Híbrido

## Descripción
Aplicación JavaFX que implementa un sistema híbrido de gestión de incidencias utilizando:
- **MySQL** (Hibernate): Almacenamiento relacional de incidencias, empleados y departamentos
- **MongoDB**: Auditoría y logs de acciones en tiempo real con exportación automática a JSON

## Tabla de Contenidos
- [Casos de Uso](#casos-de-uso)
- [Casos de Frecuencia](#casos-de-frecuencia)
- [Requisitos Previos](#requisitos-previos)
- [Configuración de Base de Datos](#configuración-de-base-de-datos)
- [Modelo de Datos](#modelo-de-datos)
- [Funcionalidades Implementadas](#funcionalidades-implementadas)
- [Compilación y Ejecución](#compilación-y-ejecución)

## Casos de Uso

![Diagrama de Casos de Uso](img/casos_uso.png?v=2)

### Actores del Sistema
- **Usuario**: Persona que gestiona las incidencias en el sistema

### Casos de Uso Principales

#### 1. Crear Incidencia
- **Actor**: Usuario
- **Descripción**: Permite registrar una nueva incidencia en el sistema
- **Precondición**: El sistema debe tener empleados y departamentos registrados
- **Flujo Principal**:
  1. Usuario accede al formulario de creación
  2. Completa los campos: título, descripción, datos del cliente
  3. Selecciona empleado asignado del desplegable
  4. Sistema muestra el departamento del empleado seleccionado
  5. Usuario guarda la incidencia
  6. Sistema guarda en MySQL y registra evento en MongoDB
  7. Sistema exporta automáticamente el log a JSON
- **Postcondición**: Incidencia creada en BD relacional y evento registrado en MongoDB

#### 2. Buscar Incidencia
- **Actor**: Usuario
- **Descripción**: Permite buscar una incidencia existente por su ID
- **Flujo Principal**:
  1. Usuario ingresa ID de incidencia
  2. Usuario pulsa "Buscar"
  3. Sistema consulta la base de datos
  4. Sistema muestra información completa de la incidencia
  5. Sistema registra evento de consulta en MongoDB
  6. Sistema exporta automáticamente el log a JSON
- **Postcondición**: Información mostrada y consulta registrada

#### 3. Actualizar Estado de Incidencia
- **Actor**: Usuario
- **Descripción**: Permite cambiar el estado de una incidencia
- **Precondición**: Incidencia debe estar cargada en pantalla de búsqueda
- **Flujo Principal**:
  1. Usuario selecciona nuevo estado del desplegable (En Proceso/Reparado/Finalizado)
  2. Usuario pulsa "Actualizar Estado"
  3. Sistema actualiza el estado en MySQL
  4. Sistema registra evento de cambio en MongoDB
  5. Sistema exporta automáticamente el log a JSON
  6. Sistema muestra confirmación
- **Postcondición**: Estado actualizado y cambio registrado

#### 4. Ver Logs de Auditoría
- **Actor**: Usuario
- **Descripción**: Visualiza el historial completo de eventos registrados
- **Flujo Principal**:
  1. Usuario accede a "Ver Logs MongoDB"
  2. Sistema carga todos los eventos de la colección
  3. Sistema muestra tabla con: ID Incidencia, Acción, Usuario, Descripción, Fecha
  4. Usuario puede actualizar la vista
  5. Usuario puede abrir el archivo JSON exportado
- **Postcondición**: Historial de eventos visualizado

#### 5. Exportar/Visualizar JSON de Auditoría
- **Actor**: Usuario
- **Descripción**: Accede al archivo JSON con todos los eventos registrados
- **Nota**: El archivo se actualiza automáticamente con cada operación
- **Flujo Principal**:
  1. Usuario pulsa "Abrir JSON"
  2. Sistema verifica existencia del archivo
  3. Sistema abre el archivo con el editor predeterminado
- **Postcondición**: Usuario visualiza el JSON actualizado

## Casos de Frecuencia

![Diagrama de Casos de Frecuencia](img/casos_frecuencia.png?v=2)

### Análisis de Uso del Sistema

#### Operaciones de Alta Frecuencia
Estas operaciones se realizan constantemente:

1. **Crear Incidencia** (Muy Frecuente)
   - Múltiples incidencias diarias
   - Operación principal del sistema
   - Cada creación genera evento en MongoDB + exportación JSON

2. **Buscar Incidencia** (Muy Frecuente)
   - Consultas constantes para seguimiento
   - Necesario antes de actualizar estado
   - Cada búsqueda genera evento de auditoría

3. **Actualizar Estado** (Frecuente)
   - Varias veces al día según resolución
   - Transiciones: En Proceso → Reparado → Finalizado
   - Cada cambio genera evento de auditoría

#### Operaciones de Media Frecuencia

4. **Ver Logs MongoDB** (Moderada)
   - Consulta periódica para auditoría
   - Revisión de historial
   - Verificación de eventos registrados

5. **Exportar/Abrir JSON** ( Moderada)
   - El archivo se actualiza automáticamente
   - Apertura manual para revisión o presentación
   - Útil para auditorías externas

#### Operaciones de Baja Frecuencia

6. **Gestión de Empleados/Departamentos** (Baja)
   - Configuración inicial
   - Cambios organizacionales esporádicos
   - Mantenimiento de catálogos

### Eventos Automáticos Registrados

Cada operación del usuario genera eventos automáticos en MongoDB:

| Acción Usuario | Evento MongoDB | Frecuencia |
|----------------|----------------|----------|
| Crear Incidencia | `CREAR_INCIDENCIA` | Alta |
| Buscar Incidencia | `CONSULTAR_INCIDENCIA` | Alta |
| Cambiar Estado | `CAMBIO_ESTADO` |  Media |

**Nota**: Todos los eventos se exportan automáticamente a `logs_mongodb_export.json` en tiempo real.

### Patrones de Uso Típicos

#### Flujo Diario Común
```
1. Recepción de nueva solicitud
   └─→ Crear Incidencia ()
       └─→ Evento CREAR_INCIDENCIA + JSON actualizado

2. Seguimiento durante el día
   └─→ Buscar Incidencia ( N veces)
       └─→ Evento CONSULTAR_INCIDENCIA + JSON actualizado

3. Resolución de incidencia
   └─→ Actualizar Estado 
       └─→ Evento CAMBIO_ESTADO + JSON actualizado

4. Revisión de historial (fin de día/semana)
   └─→ Ver Logs MongoDB 
       └─→ Verificar eventos registrados
       └─→ Abrir JSON para auditoría 
```

## Requisitos Previos
- Java 21
- Maven
- MySQL Server (puerto 3306)
- MongoDB Server (puerto 27017)

## Configuración de Base de Datos

### MySQL
1. Crear la base de datos:
```sql
CREATE DATABASE gestion_incidencias;
```

2. Configuración en `src/main/resources/hibernate.cfg.xml`:
   - Usuario: `root`
   - Contraseña: `1234`
   - Puerto: `3306`

### MongoDB
- Base de datos: `gestionIncidencias`
- Colección: `auditoria_incidencias`
- Puerto por defecto: `27017`

## Modelo de Datos

### Entidades Relacionales (MySQL)
1. **Departamento** (tabla: `departamentos`)
   - id (PK, AUTO_INCREMENT)
   - nombre

2. **Empleado** (tabla: `empleados`)
   - id (PK, AUTO_INCREMENT)
   - nombre
   - departamento_id (FK → Departamento)

3. **IncidenciaEntity** (tabla: `incidencias_relacionales`)
   - id (PK, AUTO_INCREMENT)
   - titulo
   - fechaCreacion
   - nombreCliente
   - telefonoCliente
   - emailCliente
   - empleado_id (FK → Empleado)

### Relaciones Implementadas
- **Departamento ↔ Empleado**: One-to-Many (bidireccional)
- **Empleado ↔ IncidenciaEntity**: One-to-Many (bidireccional)

### Documentos NoSQL (MongoDB)
Colección `auditoria_incidencias` - **Eventos de Auditoría**:

#### Estructura de Eventos
MongoDB **NO** almacena copia de los datos, solo registra **eventos/acciones**:

```json
{
  "incidencia_id": Long,
  "usuario": String,
  "accion": String,          // "CREAR_INCIDENCIA" | "CONSULTAR_INCIDENCIA" | "CAMBIO_ESTADO"
  "descripcion": String,     // Descripción del evento
  "empleado_asignado": String,  // Solo en CREAR_INCIDENCIA
  "departamento": String,       // Solo en CREAR_INCIDENCIA
  "estado_anterior": String,    // Solo en CAMBIO_ESTADO
  "estado_nuevo": String,       // Solo en CAMBIO_ESTADO
  "fecha": Date
}
```

#### Tipos de Eventos Registrados

1. **CREAR_INCIDENCIA**: Cuando se crea una nueva incidencia
```json
{
  "incidencia_id": 1,
  "usuario": "Usuario_Sesion",
  "accion": "CREAR_INCIDENCIA",
  "descripcion": "Incidencia creada: Error en impresora HP",
  "empleado_asignado": "Carmen Ruiz",
  "departamento": "Soporte Técnico",
  "fecha": "2026-02-23T11:30:00.000Z"
}
```

2. **CONSULTAR_INCIDENCIA**: Cuando se busca/consulta una incidencia
```json
{
  "incidencia_id": 1,
  "usuario": "Usuario_Sesion",
  "accion": "CONSULTAR_INCIDENCIA",
  "descripcion": "Consulta de incidencia: Error en impresora HP",
  "fecha": "2026-02-23T12:10:00.000Z"
}
```

3. **CAMBIO_ESTADO**: Cuando se modifica el estado de una incidencia
```json
{
  "incidencia_id": 1,
  "usuario": "Usuario_Sesion",
  "accion": "CAMBIO_ESTADO",
  "descripcion": "Estado cambiado de 'En Proceso' a 'Reparado'",
  "estado_anterior": "EN_PROCESO",
  "estado_nuevo": "REPARADO",
  "fecha": "2026-02-23T12:15:00.000Z"
}
```

#### Exportación Automática a JSON
- **Archivo**: `logs_mongodb_export.json`
- **Actualización**: Automática con cada evento registrado
- **Ubicación**: Raíz del proyecto
- **Formato**: Array JSON con todos los eventos ordenados por fecha

**Ventaja**: Los datos están en MySQL, MongoDB solo registra el historial de eventos (auditoría real).

## Funcionalidades Implementadas

### 1. CRUD Completo (Hibernate)
- **Create**: `RelationalRepository.guardar()` + Evento MongoDB
- **Read**: `RelationalRepository.buscarPorId()` + Evento MongoDB
- **Update**: `RelationalRepository.actualizar()` + Evento MongoDB
- **Delete**: `RelationalRepository.eliminar()` (implementado pero no usado en UI)

### 2. Gestión de Incidencias
#### Crear Incidencia
- Formulario completo con validación
- Campos: título, descripción, nombre cliente, teléfono, email
- Selector de empleado (desplegable con nombre + departamento)
- **Estado inicial**: "En Proceso"
- **Persistencia dual**: 
  - MySQL: Datos completos de la incidencia
  - MongoDB: Evento `CREAR_INCIDENCIA`
  - JSON: Actualización automática del archivo

#### Buscar Incidencia
- Búsqueda por ID
- Visualización completa de datos
- Muestra: título, cliente, teléfono, email, empleado, departamento, estado
- **Auditoría**: Registra evento `CONSULTAR_INCIDENCIA` en MongoDB

#### Actualizar Estado
- Selector de estado: En Proceso / Reparado / Finalizado
- Actualización en MySQL
- **Auditoría**: Registra evento `CAMBIO_ESTADO` con estados anterior y nuevo

### 3. Sistema de Auditoría (MongoDB)
#### Registro Automático de Eventos
- Cada operación genera un evento de auditoría
- 3 tipos de eventos: CREAR, CONSULTAR, CAMBIO_ESTADO
- Almacenamiento en colección `auditoria_incidencias`

#### Exportación Automática a JSON
- **Archivo**: `logs_mongodb_export.json`
- **Actualización**: En tiempo real con cada evento
- **Ubicación**: Raíz del proyecto
- **Contenido**: Array JSON con todos los eventos
- **Ordenamiento**: Por fecha descendente (más recientes primero)

#### Visualización de Logs
- Vista tabla con todos los eventos
- Columnas: ID Incidencia, Acción, Usuario, Descripción, Fecha
- Botón "Actualizar" para recargar
- Botón "Abrir JSON" para visualizar archivo exportado
- Contador total de eventos

### 4. Relaciones y Catálogos
- **Departamentos**: Catálogo de áreas organizacionales
- **Empleados**: Vinculados a departamentos
- **Asignación**: Cada incidencia asignada a un empleado específico
- **Visualización**: Al seleccionar empleado, se muestra su departamento

### 5. Interfaz JavaFX
- **Panel Principal**: 3 botones de navegación
  - 🆕 Crear Incidencia
  - 🔍 Buscar Incidencia
  - 📊 Ver Logs MongoDB
- **Navegación**: Botones "Volver" en todas las vistas
- **Validación**: Campos obligatorios y formatos
- **Mensajes**: Alertas informativas de confirmación/error

### 6. Arquitectura Híbrida SQL + NoSQL
#### MySQL (Datos)
```
Almacena: Incidencias, Empleados, Departamentos
Uso: Datos estructurados y relacionales
Queries: CRUD completo, búsquedas, filtros
```

#### MongoDB (Eventos)
```
Almacena: Eventos de auditoría
Uso: Historial de acciones del sistema
Queries: Logs ordenados por fecha
```

#### Separación de Responsabilidades
- **MySQL**: "¿Qué datos tenemos?" (Estado actual)
- **MongoDB**: "¿Qué acciones se realizaron?" (Historial)
- **JSON**: Exportación de auditoría (Compartible)

## Compilación y Ejecución

### Compilar
```bash
mvn clean compile
```

### Ejecutar
```bash
mvn javafx:run
```

O ejecutar directamente:
```bash
java -jar target/GestionIncidencias-1.0-SNAPSHOT.jar
```

## Estructura del Proyecto
```
GestionIncidencias/
├── img/                           # Imágenes para documentación
│   ├── casos_uso.png              # Diagrama de casos de uso
│   └── casos_frecuencia.png       # Diagrama de casos de frecuencia
├── src/main/java/org/example/
│   ├── connection/
│   │   ├── HibernateUtil.java     # Configuración Hibernate
│   │   └── MongoDB.java           # Conexión MongoDB
│   ├── controller/
│   │   └── IncidenciaController.java  # Controlador JavaFX
│   ├── model/
│   │   ├── Departamento.java      # Entidad JPA
│   │   ├── Empleado.java          # Entidad JPA
│   │   ├── EstadoIncidencia.java  # Enum de estados
│   │   └── IncidenciaEntity.java  # Entidad JPA
│   ├── repository/
│   │   ├── IncidenciaRepository.java  # DAO MongoDB (con exportación automática)
│   │   └── RelationalRepository.java  # DAO Hibernate
│   ├── Launcher.java              # Punto de entrada alternativo
│   └── Main.java                  # Aplicación JavaFX
├── src/main/resources/
│   ├── view/
│   │   ├── incidencias.fxml           # Pantalla principal
│   │   ├── formulario_incidencia.fxml # Formulario crear
│   │   ├── busqueda_incidencia.fxml   # Panel búsqueda y actualización
│   │   └── TableView.fxml             # Vista logs MongoDB
│   └── hibernate.cfg.xml              # Configuración Hibernate
└── pom.xml                            # Configuración Maven
```

## Dependencias Principales
- JavaFX 21
- Hibernate Core 6.4.4
- MySQL Connector 8.3.0
- MongoDB Driver Sync 5.6.2
- Jakarta Persistence API 3.1.0
- SLF4J Simple 2.0.9


## Flujo de Datos Híbrido

### Crear Incidencia
```
Usuario → Formulario JavaFX
    ↓
Validación de datos
    ↓
┌─────────────────────────┬──────────────────────────┐
│  MySQL (Hibernate)      │  MongoDB (Driver)        │
│  ─────────────────      │  ────────────────        │
│  Guardar incidencia     │  Guardar evento          │
│  completa en BD         │  CREAR_INCIDENCIA        │
│  relacional             │  en colección            │
└─────────────────────────┴──────────────────────────┘
                   ↓
         Exportación automática
         logs_mongodb_export.json
                   ↓
         Confirmación al usuario
```

### Buscar y Actualizar Estado
```
Usuario → Búsqueda por ID
    ↓
MySQL: Consulta incidencia
    ↓
MongoDB: Evento CONSULTAR_INCIDENCIA
    ↓
Mostrar datos al usuario
    ↓
Usuario → Cambiar estado
    ↓
MySQL: Actualizar estado
    ↓
MongoDB: Evento CAMBIO_ESTADO
    ↓
Exportación automática JSON
    ↓
Confirmación al usuario
```

## Uso del Sistema

### 1. Iniciar Servicios
```bash
# MySQL
sudo systemctl start mysql

# MongoDB
sudo systemctl start mongod
# O en Windows:
net start MongoDB
```

### 2. Configurar Base de Datos
```bash
# Ejecutar scripts SQL
mysql -u root -p < init_database.sql
mysql -u root -p gestion_incidencias < insertar_departamentos_y_empleados.sql
```

### 3. Ejecutar Aplicación
```bash
mvn clean compile
mvn javafx:run
```

### 4. Operaciones Básicas

#### Crear Primera Incidencia
1. Clic en **CREAR INCIDENCIA**
2. Completar formulario:
   - Título: "Error en servidor principal"
   - Descripción: "El servidor no responde desde las 10:00"
   - Cliente: "María García"
   - Teléfono: "612345678"
   - Email: "maria@empresa.com"
3. Seleccionar empleado del desplegable
4. Verificar que aparece el departamento
5. Clic en **Guardar**
6. Ver consola:
   ```
   🔵 Guardando en MongoDB...
   ✅ Documento guardado exitosamente en MongoDB
   📄 JSON actualizado automáticamente: [...]\logs_mongodb_export.json
      Total de eventos: 1
   ```

#### Buscar y Actualizar
1. Clic en **BUSCAR INCIDENCIA**
2. Ingresar ID: 1
3. Clic en **Buscar**
4. Ver consola: evento CONSULTAR_INCIDENCIA registrado
5. Cambiar estado a "Reparado"
6. Clic en **Actualizar Estado**
7. Ver consola: evento CAMBIO_ESTADO registrado

#### Ver Auditoría
1. Clic en **VER LOGS MONGODB**
2. Ver tabla con todos los eventos
3. Clic en **Abrir JSON** para ver archivo exportado
4. El JSON se ha actualizado automáticamente con todos los eventos

## Troubleshooting

### Error de conexión MySQL
- Verificar que MySQL esté corriendo: `sudo systemctl status mysql`
- Verificar credenciales en `hibernate.cfg.xml`

### Error de conexión MongoDB
- Verificar que MongoDB esté corriendo: `sudo systemctl status mongod`
- Verificar URI en `MongoDB.java`

### JavaFX no encuentra recursos FXML
- Verificar que los archivos estén en `src/main/resources/view/`
- Las rutas en código deben empezar con `/view/`
