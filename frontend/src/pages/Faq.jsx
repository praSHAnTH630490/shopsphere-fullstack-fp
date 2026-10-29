import "../styles/pages/faq.css";
const questions = [
  {
    question: "How do I place an order?",
    answer:
      "Add products to your cart, open checkout, select an address and choose a payment method.",
  },
  {
    question: "Can I cancel an order?",
    answer:
      "Orders that have not reached a final state can be cancelled from your Orders page.",
  },
  {
    question: "How does online payment work?",
    answer:
      "The backend provides a payment simulation endpoint for this project, so online payment can be demonstrated without a real gateway.",
  },
  {
    question: "Can I review products?",
    answer:
      "Yes. Signed-in customers can leave ratings and comments on product detail pages.",
  },
];

export default function Faq() {
  return (
    <main className="page section narrow">
      <div className="eyebrow">HELP CENTER</div>
      <h1>Frequently asked questions</h1>

      <div className="faq-list">
        {questions.map(({ question, answer }) => (
          <details key={question}>
            <summary>{question}</summary>
            <p>{answer}</p>
          </details>
        ))}
      </div>
    </main>
  );
}
