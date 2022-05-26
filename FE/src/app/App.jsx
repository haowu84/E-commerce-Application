import React from "react";
import Content from 'app/Content';
import NavBar from 'app/NavBar';
import {UserProvider} from "hook/User";
import styled from "styled-components";


const StyledDiv = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: center;
  width: 100%;
  height: 100%;
`


const App = () => {
    return (
        <UserProvider>
            <StyledDiv>
                <NavBar/>
                <Content/>
            </StyledDiv>
        </UserProvider>
    );
};

export default App;
