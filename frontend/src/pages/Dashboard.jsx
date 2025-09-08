import { useEffect, useState } from "react";
import api from "../api/client";
import { formatISO } from "date-fns";

export default function Dashboard() {
  const [fisios, setFisios] = useState([]);
  const [pacientes, setPacientes] = useState([]);
  const [fisioId, setFisioId] = useState("");
  const [pacienteId, setPacienteId] = useState("");
  const [inicio, setInicio] = useState("");
  const [duracaoMin, setDuracaoMin] = useState(30);
  const [consultas, setConsultas] = useState([]);

  useEffect(() => {
    (async () => {
      const { data: fs } = await api.get("/api/fisios");
      const { data: ps } = await api.get("/api/pacientes");
      setFisios(fs);
      setPacientes(ps);
    })();
  }, []);

  async function carregarConsultasDia() {
    if (!fisioId || !inicio) return;
    const dia = inicio.slice(0, 10); // yyyy-MM-dd
    const { data } = await api.get(`/api/consultas?fisioId=${fisioId}&dia=${dia}`);
    setConsultas(data);
  }

  async function agendar() {
    if (!fisioId || !pacienteId || !inicio) return;
    const idemKey = crypto.randomUUID();
    await api.post(
      "/api/consultas",
      { pacienteId, fisioId, inicio, duracaoMin: Number(duracaoMin) },
      { headers: { "Idempotency-Key": idemKey } }
    );
    await carregarConsultasDia();
    alert("Consulta criada!");
  }

  return (
    <div style={{ maxWidth: 800, margin: "40px auto", color: "#eee" }}>
      <h2>Agenda</h2>

      <div style={{ display: "grid", gap: 8, gridTemplateColumns: "1fr 1fr 1fr 1fr auto" }}>
        <select value={pacienteId} onChange={(e) => setPacienteId(e.target.value)}>
          <option value="">Selecione paciente</option>
          {pacientes.map(p => <option key={p.id} value={p.id}>{p.nome}</option>)}
        </select>

        <select value={fisioId} onChange={(e) => setFisioId(e.target.value)}>
          <option value="">Selecione fisio</option>
          {fisios.map(f => <option key={f.id} value={f.id}>{f.nome}</option>)}
        </select>

        <input
          type="datetime-local"
          value={inicio}
          onChange={(e) => setInicio(e.target.value)}
        />

        <input
          type="number"
          min="10"
          step="5"
          value={duracaoMin}
          onChange={(e) => setDuracaoMin(e.target.value)}
        />

        <button onClick={agendar}>Agendar</button>
      </div>

      <div style={{ marginTop: 16 }}>
        <button onClick={carregarConsultasDia}>Carregar consultas do dia</button>
      </div>

      <ul style={{ marginTop: 16 }}>
        {consultas.map(c => (
          <li key={c.id}>
            {c.pacienteId} — {c.fisioId} — {c.inicio} → {c.fim}
          </li>
        ))}
      </ul>
    </div>
  );
}
