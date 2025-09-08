import { useEffect, useState } from "react";
import api from "../api/client";

export default function DaySchedule() {
  const [fisios, setFisios]   = useState([]);
  const [fisioId, setFisioId] = useState("");
  const [dia, setDia]         = useState("");
  const [lista, setLista]     = useState([]);

  useEffect(() => { (async () => {
    setFisios((await api.get("/api/fisios")).data);
  })(); }, []);

  async function carregar(e) {
    e?.preventDefault();
    if (!fisioId || !dia) return;
    const { data } = await api.get("/api/consultas", { params: { fisioId, dia }});
    setLista(data);
  }

  return (
    <div>
      <h3>Agenda do dia</h3>
      <form onSubmit={carregar}>
        <select value={fisioId} onChange={e=>setFisioId(e.target.value)}>
          <option value="">Fisio</option>
          {fisios.map(f => <option key={f.id} value={f.id}>{f.nome}</option>)}
        </select>
        <input type="date" value={dia} onChange={e=>setDia(e.target.value)} />
        <button>Carregar</button>
      </form>

      <ul>
        {lista.map(c => (
          <li key={c.id}>
            {c.inicio} → {c.fim} | Paciente {c.pacienteId}
          </li>
        ))}
      </ul>
    </div>
  );
}
