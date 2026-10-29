import "../styles/pages/about.css";
export default function About() {
  return (
    <main className="page section narrow">
      <div className="eyebrow">ABOUT SHOPSPHERE</div>
      <h1>Shopping should feel simple.</h1>
      <p>
        ShopSphere is a full-stack e-commerce experience built around a real
        Spring Boot backend. The goal is straightforward: make discovering
        products, buying them and managing orders easy for everyone.
      </p>

      <div className="info-cards">
        <div className="panel">
          <h2>Quality first</h2>
          <p>
            Products, stock and orders are powered by the store backend rather
            than fake browser data.
          </p>
        </div>

        <div className="panel">
          <h2>Built for people</h2>
          <p>
            A clean responsive interface keeps the important things easy to
            find on desktop and mobile.
          </p>
        </div>

        <div className="panel">
          <h2>Secure accounts</h2>
          <p>
            Authentication uses the backend&apos;s JWT security and role-based access.
          </p>
        </div>
      </div>
    </main>
  );
}
