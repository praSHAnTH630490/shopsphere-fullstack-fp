import {useEffect,useState} from 'react';
import {addressApi,userApi,getApiError} from '../services/api';
import {useAuth} from '../context/AuthContext';
import "../styles/pages/profile.css";

const emptyAddress={fullName:'',phone:'',addressLine:'',city:'',state:'',postalCode:'',country:'India'};
export default function Profile(){
 const {user,setUser}=useAuth();
 const [form,setForm]=useState({name:user?.name||'',phone:user?.phone||''}),[password,setPassword]=useState({currentPassword:'',newPassword:''}),[addresses,setAddresses]=useState([]),[address,setAddress]=useState(emptyAddress),[editing,setEditing]=useState(null),[msg,setMsg]=useState(''),[error,setError]=useState('');
 useEffect(() => {
  let cancelled = false;
  const loadAddresses = async () => {
   try {
    const response = await addressApi.list();
    if (!cancelled) setAddresses(response.data || []);
   } catch (e) {
    if (!cancelled) setError(getApiError(e));
   }
  };
  loadAddresses();
  return () => { cancelled = true; };
 }, []);
 const save=async e=>{e.preventDefault();try{const r=await userApi.update(form);setUser(r.data);setMsg('Profile updated.')}catch(e){setError(getApiError(e))}};
 const change=async e=>{e.preventDefault();try{await userApi.password(password);setPassword({currentPassword:'',newPassword:''});setMsg('Password changed.')}catch(e){setError(getApiError(e))}};
 const saveAddress=async e=>{e.preventDefault();try{const r=editing?await addressApi.update(editing,address):await addressApi.create(address);setAddresses(x=>editing?x.map(a=>a.id===editing?r.data:a):[...x,r.data]);setAddress(emptyAddress);setEditing(null);setMsg(editing?'Address updated.':'Address added.')}catch(e){setError(getApiError(e))}};
 const remove=async id=>{try{await addressApi.remove(id);setAddresses(x=>x.filter(a=>a.id!==id));setMsg('Address deleted.')}catch(e){setError(getApiError(e))}};
 return <main className="page section"><div className="eyebrow">ACCOUNT</div><h1>My profile</h1>{msg&&<div className="notice notice-success">{msg}</div>}{error&&<div className="notice notice-error">{error}</div>}
 <div className="profile-grid"><section className="panel"><h2>Personal details</h2><form onSubmit={save}><label>Name<input required value={form.name} onChange={e=>setForm({...form,name:e.target.value})}/></label><label>Email<input value={user?.email||''} disabled/></label><label>Phone<input value={form.phone||''} onChange={e=>setForm({...form,phone:e.target.value})}/></label><button className="primary-button">Save changes</button></form></section><section className="panel"><h2>Change password</h2><form onSubmit={change}><label>Current password<input type="password" required value={password.currentPassword} onChange={e=>setPassword({...password,currentPassword:e.target.value})}/></label><label>New password<input type="password" minLength="8" required value={password.newPassword} onChange={e=>setPassword({...password,newPassword:e.target.value})}/></label><button className="secondary-button">Update password</button></form></section></div>
 <section className="panel"><div className="panel-head"><h2>{editing?'Edit address':'Add address'}</h2>{editing&&<button className="text-button" type="button" onClick={()=>{setEditing(null);setAddress(emptyAddress)}}>Cancel</button>}</div><form className="address-form" onSubmit={saveAddress}>{Object.keys(address).map(k=><input key={k} required placeholder={k.replace(/([A-Z])/g,' $1')} value={address[k]} onChange={e=>setAddress({...address,[k]:e.target.value})}/>) }<button className="primary-button">{editing?'Update address':'Save address'}</button></form></section>
 <section className="panel"><h2>Saved addresses</h2>{!addresses.length?<p className="muted">No addresses saved.</p>:addresses.map(a=><div className="saved-address" key={a.id}><div><b>{a.fullName}</b><p>{a.addressLine}, {a.city}, {a.state} - {a.postalCode}, {a.country}</p><span>{a.phone}</span></div><div><button className="text-button" onClick={()=>{setEditing(a.id);setAddress({fullName:a.fullName||'',phone:a.phone||'',addressLine:a.addressLine||'',city:a.city||'',state:a.state||'',postalCode:a.postalCode||'',country:a.country||'India'})}}>Edit</button><button className="text-button danger" onClick={()=>remove(a.id)}>Delete</button></div></div>)}</section></main>
}
