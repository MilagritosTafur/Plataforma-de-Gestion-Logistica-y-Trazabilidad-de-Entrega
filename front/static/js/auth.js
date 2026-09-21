const SESSION_KEY = "cl_session";

const ROLE_DASHBOARD = {
    ADMINISTRADOR: "../../templates/dashboard/admin.html",
    OPERADOR: "../../templates/dashboard/operador.html",
    REPARTIDOR: "../../templates/dashboard/repartidor.html",
    USUARIO: "../../templates/dashboard/cliente.html",
};

const LOGIN_URL = "../../templates/public/login.html";
const INDEX_URL = "../../templates/public/index.html";

async function apiLogin(email, password) {
    const res = await fetch("/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password }),
    });
    const data = await res.json();
    if (!res.ok) {
        throw new Error(data.message || "No se pudo iniciar sesion");
    }
    return data;
}

async function apiRegistro(payload) {
    const res = await fetch("/auth/registro", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
    });
    const data = await res.json();
    if (!res.ok) {
        const detalle = data.errores
            ? Object.values(data.errores).join(" ")
            : data.message;
        throw new Error(detalle || "No se pudo completar el registro");
    }
    return data;
}

async function apiCrearCuenta(payload, token) {
    const res = await fetch("/usuarios", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            Authorization: "Bearer " + token,
        },
        body: JSON.stringify(payload),
    });
    const data = await res.json();
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
