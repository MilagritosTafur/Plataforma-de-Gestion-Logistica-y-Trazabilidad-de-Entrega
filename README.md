# Plataforma-de-Gestion-Logistica-y-Tranzabilidad-de-Entrega.

*Descripcion*
Plataforma integral de gestion logistica diseñada para empresas de reparto que permite registrar, monitorear y rastrear servicios de entrega de forma centralizada. Integra clientes, ordenes, vehiculos, rutas y eventos de tranzabilidad en una solucion unificada.
-

*Problematica*
actualmente, las empresas de reparto registarn servicios y entregas en aplicaciones separadas, lo cual genera: 
-Duplicidad de datos
-Falta de visibilidad integrada
-Ineficiendia operacional
-Dificultad en el seguimiento de la entrega.
-

*Caracteristicas principales*
Modulos Funcinales
-Clientes y Direcciones: Registro y gestion de informacion de clientes.
-Administracion: Configuracion y seguridad del sistema.
-Operador Logistico: Registro de servicios, rutas y eventos.
-Dispensador: Visualizacionn y actualizacion de eventos de entrega.
-Cliente: Consulta publicamente el estado de entrega (codigo unico publico)
-

*Reglas del negocio*
-Un evento sin estado activo no puede registrar asignaciones incompatibles.
-Una ruta solo puede modificarse si esta en estado "abierto".
-Solo el dispensador asignado puede registrar ciertos eventos de rutas.
-El codigo de seguimiento debe ser unico y generado automaticamente.
-El cliente no puede acceder a datos operativos de otros clientes. 
-

*Modulos de datos*
-Cleintes y Direcciones
-Direcciones Destino 
-Servicios y Rutas
-Vehiculos Repartidores
-Eventos y Asignaciones
-Seguimientos
-Estados, Incidencias, Diagnostico y Derivos. 
-
