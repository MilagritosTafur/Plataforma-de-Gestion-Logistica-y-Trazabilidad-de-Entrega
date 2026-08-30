# Plataforma de Gestión Logística y Trazabilidad de Entrega

## 📝 Descripción
Plataforma integral de gestión logística diseñada para empresas de reparto que permite registrar, monitorear y rastrear servicios de entrega de forma centralizada. Integra clientes, órdenes, vehículos, rutas y eventos de trazabilidad en una solución unificada.

---

## ⚠️ Problemática
Actualmente, las empresas de reparto registran servicios y entregas en aplicaciones separadas, lo cual genera:
- Duplicidad de datos.
- Falta de visibilidad integrada.
- Ineficiencia operacional.
- Dificultad en el seguimiento de la entrega.

---

## 🚀 Características Principales

### Módulos Funcionales
- **Clientes y Direcciones:** Registro y gestión de información de clientes.
- **Administración:** Configuración y seguridad del sistema.
- **Operador Logístico:** Registro de servicios, rutas y eventos.
- **Dispensador:** Visualización y actualización de eventos de entrega.
- **Cliente:** Consulta públicamente el estado de entrega (código único público).

---

## 💼 Reglas de Negocio
- Un evento sin estado activo no puede registrar asignaciones incompatibles.
- Una ruta solo puede modificarse si está en estado "abierto".
- Solo el dispensador asignado puede registrar ciertos eventos de rutas.
- El código de seguimiento debe ser único y generado automáticamente.
- El cliente no puede acceder a datos operativos de otros clientes.

---

## 📊 Módulos de Datos
- Clientes y Direcciones
- Direcciones Destino
- Servicios y Rutas
- Vehículos Repartidores
- Eventos y Asignaciones
- Seguimientos
- Estados, Incidencias, Diagnóstico y Derivos.

