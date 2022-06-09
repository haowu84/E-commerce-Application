import Config from "backend/config.json";
import Axios from "axios";

async function login(loginRequest) {
    const requestBody = {
        email: loginRequest.email,
        password: loginRequest.password
    };

    const options = {
        method: "POST", // Method type ("POST", "GET", "DELETE", ect)
        baseURL: Config.idmUrl, // Base URL (localhost:8081 for example)
        url: Config.idm.login, // Path of URL ("/login")
        data: requestBody // Data to send in Body (The RequestBody to send)
    }

    return Axios.request(options);
}

async function register(registerRequest) {
    const requestBody = {
        email: registerRequest.email,
        password: registerRequest.password
    };

    const options = {
        method: "POST",
        baseURL: Config.idmUrl,
        url: Config.idm.register,
        data: requestBody
    }

    return Axios.request(options);
}

async function refresh(refreshRequest) {
    const requestBody = {
        refreshToken: refreshRequest.refreshToken
    }

    const options = {
        method: "POST",
        baseURL: Config.idmUrl,
        url: Config.idm.refresh,
        data: requestBody
    }

    return Axios.request(options);
}

export default {
    login,
    register,
    refresh
}
