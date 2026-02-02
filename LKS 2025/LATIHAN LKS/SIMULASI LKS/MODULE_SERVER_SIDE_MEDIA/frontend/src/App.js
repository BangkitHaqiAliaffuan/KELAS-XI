import React from 'react'
import { Route, Routes } from 'react-router-dom'
import Login from './components/Login'
import Dashboard from './components/Dashboard'
import CreateValidation from './components/CreateValidation'
import Installment from './components/Installment'
import InstallmentShow from './components/InstallmentShow'

const App = () => {
  return (
    <Routes>
      <Route path='/login' element={<Login/>}/>
      <Route path='/dashboard' element={<Dashboard/>}/>
      <Route path='/request' element={<CreateValidation/>}/>
      <Route path='/installment' element={<Installment/>}/>
      <Route path='/installment/:id' element={<InstallmentShow/>}/>
    </Routes>
  )
}

export default App