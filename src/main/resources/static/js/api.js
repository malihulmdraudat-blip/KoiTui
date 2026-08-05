// Central place for talking to the backend.
// Since the frontend now lives inside Spring Boot's own static/ folder,
// it's served from the same origin as the API, so no absolute URL is needed.
const BASE_URL = ""; // same-origin now: Spring Boot serves both API and these static files

function getToken() {
  return localStorage.getItem("freeslot_token");
}

function setSession(token, user) {
  localStorage.setItem("freeslot_token", token);
  localStorage.setItem("freeslot_user", JSON.stringify(user));
}

function clearSession() {
  localStorage.removeItem("freeslot_token");
  localStorage.removeItem("freeslot_user");
}

function getStoredUser() {
  const raw = localStorage.getItem("freeslot_user");
  return raw ? JSON.parse(raw) : null;
}

// Redirect to login if there's no token (call this at the top of protected pages)
function requireAuth() {
  if (!getToken()) {
    window.location.href = "index.html";
  }
}

// Generic request helper. Automatically attaches the JWT and parses JSON.
async function apiRequest(path, method = "GET", body = null) {
  const headers = { "Content-Type": "application/json" };
  const token = getToken();
  if (token) {
    headers["Authorization"] = "Bearer " + token;
  }

  const options = { method, headers };
  if (body !== null) {
    options.body = JSON.stringify(body);
  }

  const response = await fetch(BASE_URL + path, options);

  // Session expired or invalid token
  if (response.status === 401 || response.status === 403) {
    clearSession();
    window.location.href = "index.html";
    return null;
  }

  let data = null;
  const text = await response.text();
  if (text) {
    try {
      data = JSON.parse(text);
    } catch (e) {
      data = text;
    }
  }

  if (!response.ok) {
    const message = (data && data.message) ? data.message : "Something went wrong. Please try again.";
    throw new Error(message);
  }

  return data;
}
