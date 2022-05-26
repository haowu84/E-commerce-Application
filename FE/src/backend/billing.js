import Config from "backend/config.json";
import Axios from "axios";

async function cart_insert(insertRequest, accessToken) {
    const requestBody = {
        movieId: insertRequest.movieId,
        quantity: insertRequest.quantity
    };

    const options = {
        method: "POST",
        baseURL: Config.billingUrl,
        url: Config.billing.cart_insert,
        data: requestBody,
        headers: {
            Authorization: "Bearer " + accessToken
        }
    }

    return Axios.request(options);
}

async function cart_update(updateRequest, accessToken) {
    const requestBody = {
        movieId: updateRequest.movieId,
        quantity: updateRequest.quantity
    };

    const options = {
        method: "POST",
        baseURL: Config.billingUrl,
        url: Config.billing.cart_update,
        data: requestBody,
        headers: {
            Authorization: "Bearer " + accessToken
        }
    }

    return Axios.request(options);
}

async function cart_delete_id(id, accessToken) {
    const options = {
        method: "DELETE",
        baseURL: Config.billingUrl,
        url: Config.billing.cart_delete_id + id,
        headers: {
            Authorization: "Bearer " + accessToken
        }
    }

    return Axios.request(options);
}

async function cart_retrieve(accessToken) {
    const options = {
        method: "GET",
        baseURL: Config.billingUrl,
        url: Config.billing.cart_retrieve,
        headers: {
            Authorization: "Bearer " + accessToken
        }
    }

    return Axios.request(options);
}

async function cart_clear(accessToken) {
    const options = {
        method: "POST",
        baseURL: Config.billingUrl,
        url: Config.billing.cart_clear,
        headers: {
            Authorization: "Bearer " + accessToken
        }
    }

    return Axios.request(options);
}

async function order_list(accessToken) {
    const options = {
        method: "GET",
        baseURL: Config.billingUrl,
        url: Config.billing.order_list,
        headers: {
            Authorization: "Bearer " + accessToken
        }
    }

    return Axios.request(options);
}

async function order_payment(accessToken) {
    const options = {
        method: "GET",
        baseURL: Config.billingUrl,
        url: Config.billing.order_payment,
        headers: {
            Authorization: "Bearer " + accessToken
        }
    }

    return Axios.request(options);
}

async function order_complete(completeRequest, accessToken) {
    const requestBody = {
        paymentIntentId: completeRequest.paymentIntentId,
    };

    const options = {
        method: "POST",
        baseURL: Config.billingUrl,
        url: Config.billing.order_complete,
        data: requestBody,
        headers: {
            Authorization: "Bearer " + accessToken
        }
    }

    return Axios.request(options);
}

export default {
    cart_insert,
    cart_update,
    cart_delete_id,
    cart_retrieve,
    cart_clear,
    order_list,
    order_payment,
    order_complete
}