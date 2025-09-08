// src/api/client.js
import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8081", // seu backend
});

// Anexa o token antes de cada request
api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

// Opcional: se 401, limpa token e manda pro /login
api.interceptors.response.use(
  (res) => res,
  (err) => {
    if (err.response?.status === 401) {
      localStorage.removeItem("token");
      // use seu router para redirecionar; fallback no location:
      window.location.href = "/login";
    }
    return Promise.reject(err);
  }
);

export default api;
