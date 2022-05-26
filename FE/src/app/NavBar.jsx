import React from "react";
import {NavLink} from "react-router-dom";
import styled from "styled-components";

const StyledNav = styled.nav`
  display: flex;
  justify-content: center;

  width: calc(100vw - 10px);
  height: 50px;
  padding: 5px;

  background-color: #fff;
`;

const StyledNavLink = styled(NavLink)`
  padding: 10px;
  font-size: 25px;
  color: #000;
  text-decoration: none;
`;


const NavBar = () => {
    return (
        <StyledNav>
            <StyledNavLink to="/">
                Home
            </StyledNavLink>

            <StyledNavLink to="/login">
                Login
            </StyledNavLink>

            <StyledNavLink to="/register">
                Register
            </StyledNavLink>

            <StyledNavLink to="/search">
                Search
            </StyledNavLink>

            <StyledNavLink to="/cart">
                Cart
            </StyledNavLink>

            <StyledNavLink to="/order">
                Order
            </StyledNavLink>
        </StyledNav>
    );
}

export default NavBar;
