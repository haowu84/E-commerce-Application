import Config from "backend/config.json";
import Axios from "axios";

async function movie_search(queryParams, accessToken) {
    const options = {
        method: "GET",
        baseURL: Config.moviesUrl,
        url: Config.movies.movie_search,
        params: queryParams,
        headers: {
            Authorization: "Bearer " + accessToken
        }
    }

    return Axios.request(options);
}

async function movie_id(id, accessToken) {
    const options = {
        method: "GET",
        baseURL: Config.moviesUrl,
        url: Config.movies.movie_id + id,
        headers: {
            Authorization: "Bearer " + accessToken
        }
    }

    return Axios.request(options);
}

export default {
    movie_search,
    movie_id
}