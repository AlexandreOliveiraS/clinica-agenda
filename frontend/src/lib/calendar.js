import { dateFnsLocalizer } from "react-big-calendar";
import {
  parse,
  format,
  startOfWeek,
  getDay,
} from "date-fns";
import { ptBR } from "date-fns/locale";

const locales = { "pt-BR": ptBR };

export const localizer = dateFnsLocalizer({
  format: (date, fmt) => format(date, fmt, { locale: ptBR }),
  parse: (value, fmt) => parse(value, fmt, new Date(), { locale: ptBR }),
  startOfWeek: () => startOfWeek(new Date(), { locale: ptBR }),
  getDay,
  locales,
});
