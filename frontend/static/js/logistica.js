async function apiLogistica(ruta, opciones = {}) {
    const sesion = getSession();
    const headers = { ...(opciones.headers || {}) };
    if (sesion?.token) headers.Authorization = `Bearer ${sesion.token}`;
    if (opciones.body && !headers["Content-Type"]) headers["Content-Type"] = "application/json";

    let respuesta;
    try {
        respuesta = await fetch(urlApi(ruta), { ...opciones, headers });
    } catch (error) {
        throw new Error("No se pudo conectar con la plataforma. Verifica que Spring Boot esté activo en el puerto 8081.");
    }
    const contenido = await respuesta.json().catch(() => ({}));
    if (!respuesta.ok) {
        const detalle = contenido.errores ? Object.values(contenido.errores).join(" ") : contenido.message;
        throw new Error(detalle || "No se pudo completar la operación");
    }
    return contenido;
}

function urlApi(ruta) {
    const desdeLiveServer = window.location.port === "5500"
        || window.location.port === "5501"
        || window.location.pathname.includes("/front/templates/");
    const base = desdeLiveServer
        ? `${window.location.protocol}//${window.location.hostname}:8081/api`
        : "/api";
    return `${base}${ruta}`;
}

function textoSeguro(valor) {
    const nodo = document.createElement("span");
    nodo.textContent = valor ?? "";
    return nodo.innerHTML;
}

function textoEstado(estado) {
    return String(estado || "").replaceAll("_", " ");
}

function claseEstado(estado) {
    const clases = {
        REGISTRADO: "status-registrado",
        ASIGNADO: "status-asignado",
        EN_RUTA: "status-en-ruta",
        ENTREGADO: "status-entregado",
        INCIDENCIA: "status-incidencia",
        DEVUELTO: "status-devuelto",
    };
    return clases[estado] || "status-registrado";
}

function badgeEstado(estado) {
    return `<span class="badge rounded-pill status-badge ${claseEstado(estado)}">${textoSeguro(textoEstado(estado))}</span>`;
}

function fechaCorta(valor) {
    if (!valor) return "Por confirmar";
    const fecha = new Date(`${valor}`.length === 10 ? `${valor}T00:00:00` : valor);
    return Number.isNaN(fecha.getTime()) ? textoSeguro(valor) : new Intl.DateTimeFormat("es-PE", { dateStyle: "medium" }).format(fecha);
}

function fechaHora(valor) {
    if (!valor) return "Sin fecha";
    const fecha = new Date(valor);
    return Number.isNaN(fecha.getTime()) ? textoSeguro(valor) : new Intl.DateTimeFormat("es-PE", { dateStyle: "medium", timeStyle: "short" }).format(fecha);
}
