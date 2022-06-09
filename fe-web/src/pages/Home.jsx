import React from "react";
import styled from "styled-components";
import {Navigate, useSearchParams} from "react-router-dom";
import {useUser} from "../hook/User";
import Idm from "../backend/idm";
import Billing from "../backend/billing";

const StyledDiv = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
`

const StyledH1 = styled.h1`
  padding: 1em;
  text-align: center;
  color: #0275d8;
`


const Home = () => {
    const {accessToken, setAccessToken, refreshToken, setRefreshToken} = useUser();

    const [ searchParams ] = useSearchParams();

    if (!accessToken)
    {
        return (<Navigate to="/login"/>);
    }

    if (searchParams.get("payment_intent"))
    {
        const payLoad = {
            paymentIntentId: searchParams.get("payment_intent")
        }

        Billing.order_complete(payLoad, accessToken)
            .then(response => {})
            .catch(error => {
                console.log(error.response.data);
                const payload = {
                    refreshToken: refreshToken
                }

                Idm.refresh(payload)
                    .then(response => {
                        setAccessToken(response.data.accessToken);
                        setRefreshToken(response.data.refreshToken);
                    })
                    .catch(error => console.log(error.response.data))
            })
    }

    return (
        <StyledDiv>
            <StyledH1>Welcome</StyledH1>
        </StyledDiv>
    );
}

export default Home;