import { jsPDF } from "jspdf";
import { useLocation } from "react-router-dom";
import { FaDownload } from "react-icons/fa";

function PaymentSlip() {
  const { state } = useLocation();

  if (!state) return <section className="page"><p>No payment data.</p></section>;

  const downloadSlip = () => {
    const doc = new jsPDF();
    doc.text("MobileZBD Payment Slip", 14, 20);
    doc.text(`Customer: ${state.customerName}`, 14, 30);
    doc.text(`Address: ${state.address}`, 14, 38);
    doc.text(`Email: ${state.email}`, 14, 46);
    doc.text(`Phone: ${state.mobileNumber}`, 14, 54);

    let y = 68;
    state.items.forEach((item, index) => {
      doc.text(
        `${index + 1}. ${item.productName} | ${item.color || "N/A"} | Qty ${item.quantity} | Unit ${item.unitPrice} | Total ${item.lineTotal}`,
        14,
        y,
      );
      y += 8;
    });
    doc.text(`Grand Total: ${state.grandTotal}`, 14, y + 10);
    doc.text("Payment: Cash on Delivery", 14, y + 18);
    doc.save("mobilezbd-slip.pdf");
  };

  return (
    <section className="page payment-slip-page">
      <h2>Payment Slip</h2>
      <p>{state.customerName}</p>
      <p>{state.address}</p>
      <p>{state.email}</p>
      <p>{state.mobileNumber}</p>
      <table className="slip-table">
        <thead>
          <tr>
            <th>Product</th>
            <th>Color</th>
            <th>Qty</th>
            <th>Unit</th>
            <th>Total</th>
          </tr>
        </thead>
        <tbody>
          {state.items.map((item) => (
            <tr key={`${item.productId}-${item.productName}`}>
              <td>{item.productName}</td>
              <td>{item.color}</td>
              <td>{item.quantity}</td>
              <td>{item.unitPrice}</td>
              <td>{item.lineTotal}</td>
            </tr>
          ))}
        </tbody>
      </table>
      <h3 className="payment-total">Grand Total: ${Number(state.grandTotal || 0).toFixed(2)}</h3>
      <p>Payment Method: Cash on Delivery</p>
      <button type="button" className="btn-with-icon" onClick={downloadSlip}>
        <FaDownload aria-hidden="true" />
        <span>Download Slip</span>
      </button>
    </section>
  );
}

export default PaymentSlip;
