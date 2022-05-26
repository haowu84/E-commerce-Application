import React from "react";
import {Route, Routes} from "react-router-dom";

import Home from "pages/Home";
import Login from "pages/Login";
import Register from "pages/Register";
import Search from "pages/Search";
import MovieDetail from "pages/MovieDetail";
import ShoppingCart from "../pages/ShoppingCart";
import Checkout from "../pages/Checkout";
import OrderHistory from "../pages/OrderHistory";
import styled from "styled-components";

const StyledDiv = styled.div`
  display: flex;
  justify-content: center;

  width: 100vw;
  height: 100vh;
  padding: 25px;

  background: #ffffff;
  box-shadow: inset 0 3px 5px -3px #000000;
`


const Content = () => {
    return (
        <StyledDiv>
            <Routes>
                <Route path="/" element={<Home/>}/>
                <Route path="/login" element={<Login/>}/>
                <Route path="/register" element={<Register/>}/>
                <Route path="/search" element={<Search/>}/>
                <Route path="/movie/:id" element={<MovieDetail/>}/>
                <Route path="/cart" element={<ShoppingCart/>}/>
                <Route path="/checkout" element={<Checkout/>}/>
                <Route path="/order" element={<OrderHistory/>}/>
            </Routes>
        </StyledDiv>
    );
}

export default Content;
