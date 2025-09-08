import { useState } from "react";
import { useAuth } from "../context/AuthContext";
import { useNavigate } from "react-router-dom";

export default function Login() {
  const [username, setUsername] = useState("");
  const { login } = useAuth();
  const nav = useNavigate();

  async function onSubmit(e) {
    e.preventDefault();
    try {
      await login(username || "alexandre"); // padrão p/ teste
      nav("/");
    } catch (err) {
      console.error(err);
      alert("Falha no login");
    }
  }

  return (
    <div style={{ maxWidth: 360, margin: "80px auto", color: "#eee" }}>
      <h2>Entrar</h2>
      <form onSubmit={onSubmit}>
        <label>Usuário&nbsp;</label>
        <input value={username} onChange={(e) => setUsername(e.target.value)} />
        &nbsp;
        <button type="submit">Gerar Token</button>
      </form>
    </div>
  );
}
