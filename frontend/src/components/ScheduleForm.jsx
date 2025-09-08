import { useEffect, useState } from "react";
import api from "../api/client";
import { format } from "date-fns";

export default function ScheduleForm() {
  const [pacientes, setPacientes] = useState([]);
  const [fisios, setFisios] = useState([]);
  const [pacienteId, setPacienteId] = useState("");
  const [fisioId, setFisioId] = useState("");
  const [inicio, setInicio] = useState("");
  const [duracaoMin, setDuracaoMin] = useState(30);

  useEffect(() => {
    (async () => {
      setPacientes((await api.get("/api/pacientes")).data);
      setFisios((await api.get("/api/fisios")).data);
    })();
  }, []);

  async function agendar(e) {
    e.preventDefault();
    const body = { pacienteId, fisioId, inicio, duracaoMin: Number(duracaoMin) };
    try {
      const { data, headers } = await api.post("/api/consultas", body);
      alert("Criado! id: " + data.id + "\nLocation: " + headers.location);
    } catch (err) {
      const msg = err.response?.data?.error || "Erro ao agendar";
      alert(msg);
    }
  }

  return (
    <div>
      <h3>Agendar consulta</h3>
      <form onSubmit={agendar}>
        <select value={pacienteId} onChange={e=>setPacienteId(e.target.value)}>
          <option value="">Selecione o paciente</option>
          {pacientes.map(p => <option key={p.id} value={p.id}>{p.nome || p.ownerName}</option>)}
        </select>
        <select value={fisioId} onChange={e=>setFisioId(e.target.value)}>
          <option value="">Selecione o fisio</option>
          {fisios.map(f => <option key={f.id} value={f.id}>{f.nome}</option>)}
        </select>

        <input type="datetime-local"
               value={inicio}
               onChange={(e)=>setInicio(e.target.value)}
               title="Início (yyyy-MM-ddTHH:mm:ss)" />
        <input type="number" min="10" step="5"
               value={duracaoMin}
               onChange={(e)=>setDuracaoMin(e.target.value)} />
        <button>Agendar</button>
      </form>
    </div>
  );
}
