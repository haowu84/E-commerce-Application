import React from "react";
import styled from "styled-components";
import {useUser} from "../hook/User";
import Idm from "../backend/idm";
import Billing from "backend/billing";
import {Table} from "react-bootstrap";

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


const OrderHistory = () => {
    const {accessToken, setAccessToken, refreshToken, setRefreshToken} = useUser();

    const [sales, setSales] = React.useState([]);

    React.useEffect(() => {
        Billing.order_list(accessToken)
            .then(response => setSales(response.data.sales))
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

    return (
        <StyledDiv>
            <StyledH1>Order History</StyledH1>

            <Table striped bordered hover>
                <thead>
                <tr>
                    <th class="text-center">Date</th>
                    <th class="text-center">Total</th>
                </tr>
                </thead>
                <tbody>
                {sales && sales.map((sale, i) =>
                    <tr key={i}>
                        <td class="text-center">{new Date(sale.orderDate).toLocaleDateString()}</td>
                        <td class="text-center">${sale.total}</td>
                    </tr>
                )}
                </tbody>
            </Table>
        </StyledDiv>
    )
}

export default OrderHistory;