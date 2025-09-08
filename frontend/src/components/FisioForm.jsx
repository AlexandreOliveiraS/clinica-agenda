import { useEffect, useState } from "react";
import api from "../api/client";

export default function FisioForm({ onChange }) {
  const [list, setList] = useState([]);
  const [nome, setNome] = useState("");
  const [registro, setRegistro] = useState("");

  async function load() {
    const { data } = await api.get("/api/fisios");
    setList(data);
    onChange && onChange(data);
  }

  useEffect(() => { load(); }, []);

  async function addFisio(e) {
    e.preventDefault();
    await api.post("/api/fisios", { nome, registro });
    setNome(""); setRegistro("");
    await load();
  }

  return (
    <div>
      <h3>Fisioterapeutas</h3>
      <form onSubmit={addFisio}>
        <input placeholder="Nome" value={nome} onChange={(e)=>setNome(e.target.value)} />
        <input placeholder="Registro" value={registro} onChange={(e)=>setRegistro(e.target.value)} />
        <button>Adicionar</button>
      </form>
      <ul>{list.map(f => <li key={f.id}>{f.nome} ({f.registro})</li>)}</ul>
    </div>
  );
}
