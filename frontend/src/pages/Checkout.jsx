import { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { fetchCart, resetCart } from '../redux/cartSlice';
import { addressApi, getApiError, orderApi, paymentApi } from '../services/api';
import "../styles/pages/checkout.css";

const blankAddress = { fullName: '', phone: '', addressLine: '', city: '', state: '', postalCode: '', country: 'India' };
const methods = [
  ['CASH_ON_DELIVERY', 'Cash on Delivery', 'Pay when your order arrives.'],
  ['CARD_SIMULATION', 'Card payment', 'Safe simulated card payment for the project demo.'],
  ['UPI_SIMULATION', 'UPI payment', 'Safe simulated UPI payment for the project demo.']
];

export default function Checkout() {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const cart = useSelector(s => s.cart.data);
  const [addresses, setAddresses] = useState([]);
  const [addressId, setAddressId] = useState('');
  const [paymentMethod, setPaymentMethod] = useState('CASH_ON_DELIVERY');
  const [newAddr, setNewAddr] = useState(blankAddress);
  const [adding, setAdding] = useState(false);
  const [error, setError] = useState('');
  const [busy, setBusy] = useState(false);

  useEffect(() => {
    dispatch(fetchCart());
    addressApi.list().then(r => { const list = r.data || []; setAddresses(list); if (list.length) setAddressId(list[0].id); }).catch(e => setError(getApiError(e)));
  }, [dispatch]);

  const addAddress = async e => {
    e.preventDefault();
    setError('');
    try {
      const r = await addressApi.create(newAddr);
      setAddresses(x => [...x, r.data]);
      setAddressId(r.data.id);
      setNewAddr(blankAddress);
      setAdding(false);
    } catch (e) { setError(getApiError(e)); }
  };

  const place = async () => {
    if (!addressId) return setError('Please select or add a delivery address.');
    if (!cart.items?.length) return setError('Your cart is empty.');
    setBusy(true); setError('');
    try {
      const order = await orderApi.create({ addressId: Number(addressId), paymentMethod });
      if (paymentMethod !== 'CASH_ON_DELIVERY') await paymentApi.simulate(order.data.id, 'SUCCESS');
      dispatch(resetCart());
      navigate(`/orders/${order.data.id}`);
    } catch (e) { setError(getApiError(e)); } finally { setBusy(false); }
  };

  return <main className="page section"><div className="eyebrow">FINAL STEP</div><h1>Checkout</h1>{error && <div className="notice notice-error">{error}</div>}
    <div className="checkout-grid"><section className="checkout-main"><div className="panel"><div className="panel-head"><h2>Delivery address</h2><button type="button" className="text-button" onClick={() => setAdding(v => !v)}>{adding ? 'Close' : '+ Add address'}</button></div>
      {addresses.length ? addresses.map(a => <label className={`address-option ${String(addressId) === String(a.id) ? 'selected' : ''}`} key={a.id}><input type="radio" name="address" checked={String(addressId) === String(a.id)} onChange={() => setAddressId(a.id)} /><div><b>{a.fullName}</b><p>{a.addressLine}, {a.city}, {a.state} - {a.postalCode}</p><span>{a.phone}, {a.country}</span></div></label>) : <p className="muted">No saved addresses yet. Add one below.</p>}
      {adding && <form className="address-form" onSubmit={addAddress}>{Object.keys(newAddr).map(k => <input key={k} required={k !== 'country'} placeholder={k.replace(/([A-Z])/g, ' $1')} value={newAddr[k]} onChange={e => setNewAddr({ ...newAddr, [k]: e.target.value })} />)}<button className="primary-button">Save address</button></form>}</div>
      <div className="panel"><h2>Payment method</h2>{methods.map(([value, label, help]) => <label className="payment-option" key={value}><input type="radio" name="payment" checked={paymentMethod === value} onChange={() => setPaymentMethod(value)} /><span><b>{label}</b><small>{help}</small></span></label>)}</div></section>
      <aside className="summary"><h2>Summary</h2><div><span>Items</span><strong>{cart.items?.reduce((n, i) => n + Number(i.quantity || 0), 0)}</strong></div><div><span>Total</span><strong>₹{Number(cart.totalAmount || 0).toLocaleString('en-IN')}</strong></div><button className="primary-button full" disabled={busy || !cart.items?.length} onClick={place}>{busy ? 'Placing order...' : 'Place order'}</button><Link className="back-link" to="/cart">← Back to cart</Link></aside></div></main>;
}
