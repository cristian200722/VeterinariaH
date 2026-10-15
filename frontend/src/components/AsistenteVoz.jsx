import { useEffect, useRef, useState } from 'react';
import './AsistenteVoz.css';

const API_URL = import.meta.env.VITE_API_URL?.replace(/\/+$/, '');

function obtenerReconocedor() {
  return window.SpeechRecognition || window.webkitSpeechRecognition || null;
}

export default function AsistenteVoz() {
  const [abierto, setAbierto] = useState(false);
  const [consentimiento, setConsentimiento] = useState(false);
  const [estado, setEstado] = useState('Listo para escuchar');
  const [ocupado, setOcupado] = useState(false);
  const [error, setError] = useState('');
  const reconocedorRef = useRef(null);
  const solicitudActivaRef = useRef(false);

  useEffect(() => () => {
    reconocedorRef.current?.abort();
    window.speechSynthesis?.cancel();
  }, []);

  function hablar(texto) {
    if (!('speechSynthesis' in window)) {
      setError('Este navegador no permite responder con voz.');
      setEstado('Respuesta lista');
      setOcupado(false);
      return;
    }

    window.speechSynthesis.cancel();
    const voz = new SpeechSynthesisUtterance(texto);
    voz.lang = 'es-CO';
    voz.rate = 0.96;
    voz.onend = () => {
      setEstado('Listo para otra pregunta');
      setOcupado(false);
    };
    voz.onerror = () => {
      setError('No se pudo reproducir la respuesta en voz.');
      setOcupado(false);
    };
    window.speechSynthesis.speak(voz);
  }

  async function consultar(pregunta) {
    setOcupado(true);
    setError('');
    setEstado('Consultando los datos...');

    const token = localStorage.getItem('token');
    if (!token || !API_URL) {
      setError(!token ? 'Tu sesión terminó. Inicia sesión otra vez.' : 'Falta configurar el servidor.');
      setOcupado(false);
      return;
    }

    const controlador = new AbortController();
    const temporizador = window.setTimeout(() => controlador.abort(), 90000);

    try {
      const response = await fetch(API_URL + '/api/v1/asistente/consulta-voz', {
        method: 'POST',
        headers: {
          Authorization: 'Bearer ' + token,
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ pregunta }),
        signal: controlador.signal,
      });

      const data = await response.json().catch(() => ({}));
      if (!response.ok) {
        throw new Error(data.message || data.error || 'No pude completar la consulta.');
      }
      if (typeof data.respuesta !== 'string' || !data.respuesta.trim()) {
        throw new Error('El asistente no recibió una respuesta válida.');
      }

      setEstado('Respondiendo por voz...');
      hablar(data.respuesta);
    } catch (requestError) {
      setOcupado(false);
      setEstado('No se pudo completar');
      setError(
        requestError.name === 'AbortError'
          ? 'El servidor tardó demasiado. Inténtalo otra vez.'
          : requestError.message || 'No se pudo conectar con el servidor.'
      );
    } finally {
      window.clearTimeout(temporizador);
      solicitudActivaRef.current = false;
    }
  }

  function iniciarEscucha() {
    if (!consentimiento || ocupado) return;

    const Reconocedor = obtenerReconocedor();
    if (!Reconocedor) {
      setError('El reconocimiento de voz no está disponible en este navegador. Prueba Chrome.');
      return;
    }
    if (!('speechSynthesis' in window)) {
      setError('Este navegador no permite responder con voz.');
      return;
    }

    setError('');
    setEstado('Escuchando...');
    setOcupado(true);
    solicitudActivaRef.current = false;

    const reconocedor = new Reconocedor();
    reconocedor.lang = 'es-CO';
    reconocedor.continuous = false;
    reconocedor.interimResults = false;
    reconocedor.maxAlternatives = 1;
    reconocedorRef.current = reconocedor;

    reconocedor.onresult = (event) => {
      const pregunta = event.results?.[0]?.[0]?.transcript?.trim();
      if (!pregunta || solicitudActivaRef.current) return;
      solicitudActivaRef.current = true;
      reconocedor.stop();
      consultar(pregunta);
    };

    reconocedor.onerror = (event) => {
      if (solicitudActivaRef.current) return;
      setOcupado(false);
      setEstado('Listo para escuchar');
      const mensajes = {
        'not-allowed': 'Permite el uso del micrófono en el navegador para continuar.',
        'no-speech': 'No alcancé a escuchar. Inténtalo de nuevo.',
        network: 'El servicio de reconocimiento de voz no está disponible.',
      };
      setError(mensajes[event.error] || 'No se pudo reconocer la voz.');
    };

    reconocedor.onend = () => {
      reconocedorRef.current = null;
      if (!solicitudActivaRef.current) {
        setOcupado(false);
        setEstado('Listo para escuchar');
      }
    };

    try {
      reconocedor.start();
    } catch {
      setOcupado(false);
      setEstado('Listo para escuchar');
      setError('No se pudo iniciar el micrófono. Espera un momento e inténtalo de nuevo.');
    }
  }

  function cerrar() {
    reconocedorRef.current?.abort();
    reconocedorRef.current = null;
    window.speechSynthesis?.cancel();
    setOcupado(false);
    setEstado('Listo para escuchar');
    setError('');
    setAbierto(false);
  }

  return (
    <div className="asistente-voz">
      {abierto ? (
        <section className="asistente-voz-panel" role="dialog" aria-label="Asistente de voz">
          <header className="asistente-voz-encabezado">
            <div>
              <strong>Asistente VeterinariaH</strong>
              <span>Pregunta por las mascotas de un cliente</span>
            </div>
            <button type="button" onClick={cerrar} aria-label="Cerrar asistente">×</button>
          </header>

          <p className="asistente-voz-estado" role="status">{estado}</p>

          <label className="asistente-voz-consentimiento">
            <input
              type="checkbox"
              checked={consentimiento}
              onChange={(event) => setConsentimiento(event.target.checked)}
              disabled={ocupado}
            />
            <span>
              El navegador puede procesar el audio en línea. Google Gemini recibe la transcripción
              depurada para clasificar la pregunta; se quita el nombre detectado, correos y números.
              No digas información sensible. VeterinariaH no guarda grabaciones.
            </span>
          </label>

          {error && <p className="asistente-voz-error" role="alert">{error}</p>}

          <button
            type="button"
            className="asistente-voz-microfono"
            onClick={iniciarEscucha}
            disabled={!consentimiento || ocupado}
          >
            <span aria-hidden="true">{ocupado ? '…' : '🎙️'}</span>
            {ocupado ? 'Procesando' : 'Hacer pregunta'}
          </button>
          <p className="asistente-voz-ayuda">
            Ejemplo: “¿Qué mascotas tiene nombre y apellido del cliente?”
          </p>
        </section>
      ) : null}

      <button
        type="button"
        className="asistente-voz-abrir"
        onClick={() => setAbierto(true)}
        aria-label="Abrir asistente de voz"
        title="Asistente de voz"
      >
        🎙️
      </button>
    </div>
  );
}
