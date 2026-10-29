import {useEffect,useState} from 'react';
import {Link,useParams} from 'react-router-dom';
import {productApi,reviewApi,getApiError} from '../services/api';
import {useDispatch,useSelector} from 'react-redux';
import {addToCart} from '../redux/cartSlice';
import {toggleWishlist} from '../redux/wishlistSlice';
import {useAuth} from '../context/AuthContext';
import Spinner from '../components/common/Spinner';
import "../styles/pages/product-details.css";

export default function ProductDetails(){
 const {id}=useParams();const [p,setP]=useState(null),[reviews,setReviews]=useState([]),[rating,setRating]=useState(5),[comment,setComment]=useState(''),[error,setError]=useState(''),[editingReview,setEditingReview]=useState(null);
 const {isAuthenticated,user}=useAuth();const dispatch=useDispatch();const wished=useSelector(s=>(s.wishlist.data.items||[]).some(x=>x.productId===Number(id)));
 useEffect(() => {
  let cancelled = false;
  const load = async () => {
   try {
    const [productResponse, reviewResponse] = await Promise.all([productApi.get(id), reviewApi.list(id)]);
    if (!cancelled) {
     setP(productResponse.data);
     setReviews(reviewResponse.data || []);
    }
   } catch (e) {
    if (!cancelled) setError(getApiError(e));
   }
  };
  load();
  return () => { cancelled = true; };
 }, [id]);
 if(!p)return <main className="page">{error?<div className="notice notice-error">{error}</div>:<Spinner/>}</main>;
 const submit=async e=>{e.preventDefault();try{const payload={rating:Number(rating),comment};if(editingReview){const r=await reviewApi.update(editingReview,payload);setReviews(x=>x.map(v=>v.id===editingReview?r.data:v));setEditingReview(null)}else{const r=await reviewApi.create(id,payload);setReviews(x=>[r.data,...x])}setRating(5);setComment('')}catch(e){setError(getApiError(e))}};
 const editReview=r=>{setEditingReview(r.id);setRating(r.rating);setComment(r.comment||'')};
 const deleteReview=async reviewId=>{try{await reviewApi.remove(reviewId);setReviews(x=>x.filter(r=>r.id!==reviewId))}catch(e){setError(getApiError(e))}};
 return <main className="page section"><Link className="back-link" to="/products">← Back to products</Link><div className="detail-grid"><div className="detail-image"><img src={p.imageUrl||'https://placehold.co/800x700?text=Product'} alt={p.name}/></div><div className="detail-info"><div className="eyebrow">{p.categoryName} · {p.brandName}</div><h1>{p.name}</h1><div className="detail-price">₹{Number(p.price||0).toLocaleString('en-IN')}</div><p>{p.description||'A quality product selected for everyday shopping.'}</p><div className="detail-meta"><span>SKU: {p.sku}</span><span>{p.stockQuantity} in stock</span></div><div className="detail-actions"><button className="primary-button large" disabled={!p.stockQuantity||!isAuthenticated} onClick={()=>dispatch(addToCart({productId:p.id,quantity:1}))}>{isAuthenticated?'Add to cart':'Login to buy'}</button>{isAuthenticated&&<button className="secondary-button large" onClick={()=>dispatch(toggleWishlist({productId:p.id,exists:wished}))}>{wished?'♥ Saved':'♡ Wishlist'}</button>}</div></div></div>
 <section className="reviews"><div className="section-head"><div><div className="eyebrow">CUSTOMER VOICES</div><h2>Reviews</h2></div></div>{reviews.length?reviews.map(r=><div className="review" key={r.id}><div><b>{r.userName}</b><span className="stars">{'★'.repeat(r.rating)}{'☆'.repeat(5-r.rating)}</span></div><p>{r.comment||'No comment.'}</p>{user?.id===r.userId&&<div><button className="text-button" onClick={()=>editReview(r)}>Edit</button><button className="text-button danger" onClick={()=>deleteReview(r.id)}>Delete</button></div>}</div>):<p className="muted">No reviews yet.</p>}{isAuthenticated&&<form className="review-form" onSubmit={submit}><h3>{editingReview?'Edit your review':'Share your experience'}</h3><select value={rating} onChange={e=>setRating(e.target.value)}><option value="5">5 stars</option><option value="4">4 stars</option><option value="3">3 stars</option><option value="2">2 stars</option><option value="1">1 star</option></select><textarea required value={comment} onChange={e=>setComment(e.target.value)} placeholder="What did you think?"/><div className="form-actions"><button className="primary-button">{editingReview?'Update review':'Submit review'}</button>{editingReview&&<button type="button" className="secondary-button" onClick={()=>{setEditingReview(null);setRating(5);setComment('')}}>Cancel</button>}</div></form>}</section></main>
}
