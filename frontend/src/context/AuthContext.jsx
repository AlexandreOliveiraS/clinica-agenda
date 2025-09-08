import { createContext, useContext, useState } from "react";
import api from "../api/client";

const AuthContext = createContext(null);
export const useAuth = () => useContext(AuthContext);

export default function AuthProvider({ children }) {
  const [token, setToken] = useState(localStorage.getItem("token"));

  async function login(username) {
    // seu backend aceita { "username": "alexandre" }
    const { data } = await api.post("/auth/token", { username });
    // se seu backend retorna { token: "..." }, ajuste:
    const jwt = data.token ?? data; 
    localStorage.setItem("token", jwt);
    setToken(jwt);
  }

  function logout() {
    localStorage.removeItem("token");
    setToken(null);
  }

  const value = { token, isAuth: !!token, login, logout };
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
