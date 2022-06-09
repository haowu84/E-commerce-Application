import React from "react";
import styled from "styled-components";
import {Link} from "react-router-dom";
import {useUser} from "../hook/User";
import Idm from "../backend/idm";
import Billing from "backend/billing";
import {Button, Form, Table} from "react-bootstrap";


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


const ShoppingCart = () => {
    const {accessToken, setAccessToken, refreshToken, setRefreshToken} = useUser();

    const [items, setItems] = React.useState([]);
    const [total, setTotal] = React.useState(0);
    const [counter, setCounter] = React.useState(0);

    React.useEffect(() => {
        Billing.cart_retrieve(accessToken)
            .then(response => {
                setItems(response.data.items);
                setTotal(response.data.total);
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
    }, [counter, accessToken, refreshToken]);

    const submitCartClear = () => {
        Billing.cart_clear(accessToken)
            .then(response => setCounter(counter + 1))
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

    const submitCartUpdate = (id, event) => {
        const payLoad = {
            movieId: id,
            quantity: event.target.value
        }

        Billing.cart_update(payLoad, accessToken)
            .then(response => setCounter(counter + 1))
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

    const submitCartDelete = (id) => {
        Billing.cart_delete_id(id, accessToken)
            .then(response => setCounter(counter + 1))
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
            <StyledH1>Shopping Cart</StyledH1>

            {total > 0 && <p className="text-center">Total: ${total}</p>}

            <Table striped bordered hover>
                <thead>
                <tr>
                    <th class="text-center">Image</th>
                    <th class="text-center">Title</th>
                    <th class="text-center">Quantity</th>
                    <th class="text-center">Price</th>
                    <th><Button variant="primary" onClick={submitCartClear}>Clear</Button></th>
                </tr>
                </thead>
                <tbody>
                {items && items.map((item, i) =>
                    <tr key={i}>
                        <td>
                            <img src={"https://image.tmdb.org/t/p/w300/"+item.backdropPath}/>
                        </td>

                        <td class="text-center align-middle">{item.movieTitle}</td>

                        <td class="align-middle">
                            <Form.Select value={item.quantity}
                                          onChange={(event) => submitCartUpdate(item.movieId, event)}>
                                <option value={1}>1</option>
                                <option value={2}>2</option>
                                <option value={3}>3</option>
                                <option value={4}>4</option>
                                <option value={5}>5</option>
                                <option value={6}>6</option>
                                <option value={7}>7</option>
                                <option value={8}>8</option>
                                <option value={9}>9</option>
                                <option value={10}>10</option>
                            </Form.Select>
                        </td>

                        <td class="text-center align-middle">${Math.round(item.quantity * item.unitPrice * 100) / 100}</td>

                        <td class="align-middle"><Button variant="primary" onClick={() => submitCartDelete(item.movieId)}>Delete</Button></td>
                    </tr>
                )}
                </tbody>
            </Table>

            <StyledLink to={"/checkout"}>Checkout Your Order</StyledLink>

        </StyledDiv>
    )
}

export default ShoppingCart;