import React from 'react'
import {BrowserRouter, Route, Routes} from 'react-router-dom'
import Login from './components/Login'
import 'bootstrap/dist/css/bootstrap.min.css'
import Dashboard from './components/Dashboard'
import JobVacan from './components/JobVacan'
import Request from './components/Request'
import JobDetail from './components/JobDetail'
const App = () => {
  return (
    <BrowserRouter>
    <Routes>
      <Route path='/' element={<Login/>}/>
      <Route path='/dashboard' element={<Dashboard/>}/>
      <Route path='/request' element={<Request/>}/>
      <Route path='/jobvacan/:id' element={<JobVacan/>}/>
      <Route path='/jobdetail/:id' element={<JobDetail/>}/>
      {/* <Route path='/' element={<Login/>}/> */}
    </Routes>
    </BrowserRouter>
  )
}

export default App