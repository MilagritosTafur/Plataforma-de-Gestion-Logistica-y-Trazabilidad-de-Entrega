const SESSION_KEY = "cl_session";

const ROLE_DASHBOARD = {
    ADMINISTRADOR: "../../templates/dashboard/admin.html",
    OPERADOR: "../../templates/dashboard/operador.html",
    REPARTIDOR: "../../templates/dashboard/repartidor.html",
    USUARIO: "../../templates/dashboard/cliente.html",
};

const LOGIN_URL = "../../templates/public/login.html";
const INDEX_URL = "../../templates/public/index.html";

function urlApi(ruta) {
    const desdeLiveServer = window.location.port === "5500"
        || window.location.port === "5501"
        || window.location.pathname.includes("/front/templates/");
    const base = desdeLiveServer
        ? `${window.location.protocol}//${window.location.hostname}:8081/api`
        : "/api";
    return `${base}${ruta}`;
}

async function solicitarApi(ruta, opciones) {
    try {
        return await fetch(urlApi(ruta), opciones);
    } catch (error) {
        throw new Error("No se pudo conectar con la plataforma. Verifica que Spring Boot esté activo en el puerto 8081.");
    }
}

async function apiLogin(email, password) {
    const res = await solicitarApi("/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email: String(email || "").trim().toLowerCase(), password }),
    });
    const data = await res.json().catch(() => ({}));
    if (!res.ok) {
        throw new Error(data.message || "No se pudo iniciar sesion");
    }
    return data;
}

async function apiRegistro(payload) {
    const datos = { ...payload, email: String(payload.email || "").trim().toLowerCase() };
    const res = await solicitarApi("/auth/registro", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(datos),
    });
    const data = await res.json().catch(() => ({}));
    if (!res.ok) {
        const detalle = data.errores
            ? Object.values(data.errores).join(" ")
            : data.message;
        throw new Error(detalle || "No se pudo completar el registro");
    }
    return data;
}

async function apiCrearCuenta(payload, token) {
    const datos = { ...payload, email: String(payload.email || "").trim().toLowerCase() };
    const res = await solicitarApi("/usuarios", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            Authorization: "Bearer " + token,
        },
        body: JSON.stringify(datos),
    });
    const data = await res.json().catch(() => ({}));
    if (!res.ok) {
        const detalle = data.errores
            ? Object.values(data.errores).join(" ")
            : data.message;
        throw new Error(detalle || "No se pudo crear la cuenta");
    }
    return data;
}

function saveSession(loginResponse) {
    localStorage.setItem(
        SESSION_KEY,
        JSON.stringify({
            token: loginResponse.token,
            tokenType: loginResponse.tokenType,
            usuario: loginResponse.usuario,
        })
    );
}

function getSession() {
    try {
        return JSON.parse(localStorage.getItem(SESSION_KEY));
    } catch {
        return null;
    }
}

function clearSession() {
    localStorage.removeItem(SESSION_KEY);
}

function redirectPorRol(rol) {
    window.location.href = ROLE_DASHBOARD[rol] || INDEX_URL;
}

function requireRole(rolEsperado) {
    const session = getSession();
    if (!session || !session.usuario || session.usuario.rol !== rolEsperado) {
        clearSession();
        window.location.href = LOGIN_URL;
        return null;
    }
    return session;
}

function logout() {
    clearSession();
    window.location.href = INDEX_URL;
}
