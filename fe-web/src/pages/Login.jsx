import React from "react";
import styled from "styled-components";
import {useForm} from "react-hook-form";
import {Link, useNavigate} from "react-router-dom";
import {useUser} from "hook/User";
import Idm from "backend/idm";
import {Button, Form} from "react-bootstrap";


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

const StyledLink = styled(Link)`
  padding: 1em;
  text-align: center;
  color: #0275d8;
`


const Login = () => {
    const {setAccessToken, setRefreshToken} = useUser();

    const navigate = useNavigate();

    const {register, getValues, formState: { errors }, handleSubmit} = useForm();

    const submitLogin = () => {
        const email = getValues("email");
        const password = getValues("password");

        const payLoad = {
            email: email,
            password: password.split('')
        }

        Idm.login(payLoad)
            .then(response => {
                setAccessToken(response.data.accessToken);
                setRefreshToken(response.data.refreshToken);
                navigate("/");
            })
            .catch(error => console.log(error.response.data))
    }

    return (
        <StyledDiv>
            <StyledH1>Login</StyledH1>

            <Form>
                <Form.Group className="mb-3">
                    <Form.Label>Email</Form.Label>
                    <Form.Control type="email" placeholder="Email" {...register("email", { required: true })}/>
                    {errors.email && "Email is Required" }
                </Form.Group>

                <Form.Group className="mb-3">
                    <Form.Label>Password</Form.Label>
                    <Form.Control type="password" placeholder="Password" {...register("password", { required: true })}/>
                    {errors.poassword && "Password is Required" }
                </Form.Group>
                <Button variant="primary" onClick={handleSubmit(submitLogin)}>
                    Login
                </Button>
            </Form>

            <StyledLink to={"/register"}>Sign Up</StyledLink>
        </StyledDiv>
    );
}

export default Login;
