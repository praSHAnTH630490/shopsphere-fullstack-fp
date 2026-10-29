import { useState } from "react";

import { contactApi, getApiError } from "../services/api";
import "../styles/pages/contact.css";

const initialForm = {
  name: "",
  email: "",
  subject: "",
  message: "",
};

export default function Contact() {
  const [form, setForm] = useState(initialForm);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  const handleChange = (event) => {
    const { name, value } = event.target;
    setForm((current) => ({ ...current, [name]: value }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setMessage("");
    setError("");

    try {
      await contactApi.send(form);
      setMessage("Thanks! Your message has been sent.");
      setForm(initialForm);
    } catch (requestError) {
      setError(getApiError(requestError));
    }
  };

  return (
    <main className="page section narrow">
      <div className="eyebrow">WE&apos;RE HERE</div>
      <h1>Contact us</h1>
      <p>
        Have a question? Send us a message and our team will get back to you.
      </p>

      {message && <div className="notice notice-success">{message}</div>}
      {error && <div className="notice notice-error">{error}</div>}

      <form className="panel contact-form" onSubmit={handleSubmit}>
        <label>
          Name
          <input name="name" required value={form.name} onChange={handleChange} />
        </label>

        <label>
          Email
          <input
            name="email"
            type="email"
            required
            value={form.email}
            onChange={handleChange}
          />
        </label>

        <label>
          Subject
          <input
            name="subject"
            required
            value={form.subject}
            onChange={handleChange}
          />
        </label>

        <label>
          Message
          <textarea
            name="message"
            required
            value={form.message}
            onChange={handleChange}
          />
        </label>

        <button className="primary-button">Send message</button>
      </form>
    </main>
  );
}
