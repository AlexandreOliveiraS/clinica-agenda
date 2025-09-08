import { useEffect, useMemo, useState } from "react";
import { Calendar, Views } from "react-big-calendar";
import "react-big-calendar/lib/css/react-big-calendar.css";
import { localizer } from "../lib/calendar";
import api from "../api/client";

function toDate(s) {
  // aceita "2025-09-10T14:00:00" ou "2025-09-10 14:00:00"
  return new Date(s.replace(" ", "T"));
}

export default function CalendarView() {
  const [fisios, setFisios]     = useState([]);
  const [fisioId, setFisioId]   = useState("");
  const [events, setEvents]     = useState([]);
  const [loading, setLoading]   = useState(false);
  const [viewDate, setViewDate] = useState(new Date());

  useEffect(() => {
    (async () => {
      const { data } = await api.get("/api/fisios");
      setFisios(data);
      if (data?.length) setFisioId(data[0].id);
    })();
  }, []);

  const diaParam = useMemo(() => {
    // monta YYYY-MM-DD baseado no centro da semana/mes
    const y = viewDate.getFullYear();
    const m = String(viewDate.getMonth() + 1).padStart(2, "0");
    const d = String(viewDate.getDate()).padStart(2, "0");
    return `${y}-${m}-${d}`;
  }, [viewDate]);

  async function loadDay() {
    if (!fisioId) return;
    setLoading(true);
    try {
      const { data } = await api.get("/api/consultas", {
        params: { fisioId, dia: diaParam },
      });
      const mapped = (data || []).map((c) => ({
        id: c.id,
        title: `Paciente ${c.pacienteId.slice(0, 6)}…`,
        start: toDate(c.inicio),
        end: toDate(c.fim),
        allDay: false,
      }));
      setEvents(mapped);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => { loadDay(); /* auto-carrega ao trocar data/fisio */ }, [fisioId, diaParam]);

  return (
    <div>
      <h3>Agenda (calendário)</h3>
      <div style={{ marginBottom: 8, display: "flex", gap: 8 }}>
        <select value={fisioId} onChange={e=>setFisioId(e.target.value)}>
          {fisios.map(f => <option key={f.id} value={f.id}>{f.nome}</option>)}
        </select>
        <button onClick={loadDay} disabled={loading}>
          {loading ? "Carregando..." : "Atualizar dia"}
        </button>
      </div>

      <Calendar
        culture="pt-BR"
        localizer={localizer}
        events={events}
        startAccessor="start"
        endAccessor="end"
        style={{ height: 550, background: "#fff" }}
        views={[Views.DAY, Views.WEEK, Views.MONTH]}
        defaultView={Views.DAY}
        date={viewDate}
        onNavigate={setViewDate}
        popup
        tooltipAccessor="title"
        messages={{
          today: "Hoje",
          previous: "Anterior",
          next: "Próximo",
          month: "Mês",
          week: "Semana",
          day: "Dia",
          agenda: "Agenda",
          showMore: (total) => `+${total} mais`,
        }}
      />
    </div>
  );
}
