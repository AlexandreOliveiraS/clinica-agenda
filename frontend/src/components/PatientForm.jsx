import { useEffect, useState } from "react";
import api from "../api/client";

export default function PatientForm({ onChange }) {
  const [list, setList] = useState([]);
  const [nome, setNome] = useState("");
  const [telefone, setTelefone] = useState("");

  async function load() {
    const { data } = await api.get("/api/pacientes");
    setList(data);
    onChange && onChange(data);
  }

  useEffect(() => { load(); }, []);

  async function addPatient(e) {
    e.preventDefault();
    await api.post("/api/pacientes", { nome, telefone });
    setNome(""); setTelefone("");
    await load();
  }

  return (
    <div>
      <h3>Pacientes</h3>
      <form onSubmit={addPatient}>
        <input placeholder="Nome" value={nome} onChange={(e)=>setNome(e.target.value)} />
        <input placeholder="Telefone" value={telefone} onChange={(e)=>setTelefone(e.target.value)} />
        <button>Adicionar</button>
      </form>
      <ul>{list.map(p => <li key={p.id}>{p.ownerName || p.nome} ({p.telefone})</li>)}</ul>
    </div>
  );
}
