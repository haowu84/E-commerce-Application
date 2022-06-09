import React from "react";
import styled from "styled-components";
import { loadStripe } from "@stripe/stripe-js";
import { Elements } from "@stripe/react-stripe-js";
import {useUser} from "../hook/User";
import Idm from "../backend/idm";
import Billing from "backend/billing";
import CheckoutForm from "./CheckoutForm";
import "../Checkout.css";

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

const stripePromise = loadStripe("pk_test_51KwFiNL8Pc3XnjMsexbODobAyfTzkwPUzHGG33g610VZUQ5mRY5oQo0OfCuUUitWYL8rMNDEaxo0weJPAPCZXCcy00y1Ma3HKr");

const Checkout = () => {
    const {accessToken, setAccessToken, refreshToken, setRefreshToken} = useUser();

    const [clientSecret, setClientSecret] = React.useState("");

    React.useEffect( () => {
        Billing.order_payment(accessToken)
            .then(response => {
                setClientSecret(response.data.clientSecret);
            })
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
    }, [accessToken, refreshToken]);

    const appearance = {
        theme: 'stripe',
    };

    const options = {
        clientSecret,
        appearance,
    };

    return (
        <StyledDiv>
            <StyledH1>Checkout</StyledH1>

            {clientSecret && (
                <Elements options={options} stripe={stripePromise}>
                    <CheckoutForm/>
                </Elements>
            )}
        </StyledDiv>
    );
}

export default Checkout;