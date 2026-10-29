import { useEffect, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import ProductCard from '../components/common/ProductCard';
import Spinner from '../components/common/Spinner';
import { catalogApi, getApiError, productApi } from '../services/api';
import "../styles/pages/products.css";

export default function Products() {
  const [params, setParams] = useSearchParams();
  const [data, setData] = useState({ content: [], totalPages: 0, number: 0, totalElements: 0 });
  const [cats, setCats] = useState([]);
  const [brands, setBrands] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const search = params.get('search') || '';
  const category = params.get('category') || '';
  const brand = params.get('brand') || '';
  const minPrice = params.get('minPrice') || '';
  const maxPrice = params.get('maxPrice') || '';
  const minRating = params.get('minRating') || '';
  const sort = params.get('sort') || '';
  const page = Number(params.get('page') || 0);

  useEffect(() => {
    Promise.all([catalogApi.categories(), catalogApi.brands()])
      .then(([c, b]) => { setCats(c.data || []); setBrands(b.data || []); })
      .catch(e => setError(getApiError(e)));
  }, []);

  useEffect(() => {
    let cancelled = false;
    productApi.list({
      page, size: 12,
      search: search || undefined,
      category: category ? Number(category) : undefined,
      brand: brand ? Number(brand) : undefined,
      minPrice: minPrice || undefined,
      maxPrice: maxPrice || undefined,
      minRating: minRating || undefined,
      sort: sort || undefined
    }).then(r => {
      if (!cancelled) {
        setData(r.data);
        setError('');
      }
    })
      .catch(e => { if (!cancelled) setError(getApiError(e)); })
      .finally(() => { if (!cancelled) setLoading(false); });
    return () => { cancelled = true; };
  }, [page, search, category, brand, minPrice, maxPrice, minRating, sort]);

  const update = (key, value) => {
    setLoading(true);
    const next = new URLSearchParams(params);
    if (value !== '') next.set(key, value); else next.delete(key);
    next.delete('page');
    setParams(next);
  };

  return (
    <main className="page section">
      <div className="listing-head"><div><div className="eyebrow">OUR CATALOG</div><h1>Find something you’ll love.</h1><p>Search, filter and discover products from the store.</p></div></div>
      <div className="filters">
        <input value={search} onChange={e => update('search', e.target.value)} placeholder="Search products..." aria-label="Search products" />
        <select value={category} onChange={e => update('category', e.target.value)}><option value="">All categories</option>{cats.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}</select>
        <select value={brand} onChange={e => update('brand', e.target.value)}><option value="">All brands</option>{brands.map(b => <option key={b.id} value={b.id}>{b.name}</option>)}</select>
        <input type="number" min="0" value={minPrice} onChange={e => update('minPrice', e.target.value)} placeholder="Min ₹" aria-label="Minimum price" />
        <input type="number" min="0" value={maxPrice} onChange={e => update('maxPrice', e.target.value)} placeholder="Max ₹" aria-label="Maximum price" />
        <select value={minRating} onChange={e => update('minRating', e.target.value)}><option value="">Any rating</option><option value="5">5+ rating</option><option value="4">4+ rating</option><option value="3">3+ rating</option><option value="2">2+ rating</option><option value="1">1+ rating</option></select>
        <select value={sort} onChange={e => update('sort', e.target.value)}><option value="">Newest</option><option value="price_asc">Price low to high</option><option value="price_desc">Price high to low</option><option value="name_asc">Name A-Z</option><option value="name_desc">Name Z-A</option><option value="oldest">Oldest</option></select>
      </div>
      {loading ? <Spinner /> : error ? <div className="notice notice-error">{error}</div> : <>
        <p className="result-count">{data.totalElements ?? 0} products found</p>
        {data.content?.length ? <div className="product-grid">{data.content.map(p => <ProductCard key={p.id} product={p} />)}</div> : <div className="empty-state"><h2>No products found</h2><p>Try changing your search or filters.</p></div>}
        {data.totalPages > 1 && <div className="pagination">{Array.from({ length: data.totalPages }, (_, i) => <button className={i === data.number ? 'active' : ''} key={i} onClick={() => { const next = new URLSearchParams(params); next.set('page', String(i)); setParams(next); }}>{i + 1}</button>)}</div>}
      </>}
    </main>
  );
}
