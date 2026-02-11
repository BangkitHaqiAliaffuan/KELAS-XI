import React from 'react'
import { Route, Routes } from 'react-router-dom'
import Signin from './components/Signin'
import './App.css'
import 'bootstrap/dist/css/bootstrap.min.css';
import Home from './components/User/Home';
import Signup from './components/Signup';
import ListAdmin from './components/Admin/ListAdmin';
import HomeAdmin from './components/Admin/HomeAdmin';
import ListUser from './components/Admin/ListUser';
import AddUser from './components/Admin/AddUser';
import UpdateUser from './components/Admin/UpdateUser';
// import Admins from './components/Admin/admins';

const App = () => {
  return (
    <Routes>
      <Route path='/login' element={<Signin/>}/>
      <Route path='/' element={<Home/>}/>
      <Route path='/signup' element={<Signup/>}/>
      <Route path='/list-admin' element={<ListAdmin/>}/>
      <Route path='/list-user' element={<ListUser/>}/>
      <Route path='/update-user/:id' element={<ListUser/>}/>
      <Route path='/add-user' element={<AddUser/>}/>
      <Route path='/update-users/:id' element={<UpdateUser/>}/>
      <Route path='/admin' element={<HomeAdmin/>}/>
    </Routes>
  )
}

export default App