import React from 'react';
import { Text, StyleSheet, View, Image, ScrollView } from 'react-native';
import Axios from "axios";

async function refresh(refreshRequest) {
    const requestBody = {
        refreshToken: refreshRequest.refreshToken
    }

    const options = {
        method: "POST",
        baseURL: "http://10.0.2.2:8082",
        url: "/refresh",
        data: requestBody
    }

    return Axios.request(options);
}

async function movie_id(id, accessToken) {
    const options = {
        method: "GET",
        baseURL: "http://10.0.2.2:8083",
        url:  "/movie/" + id,
        headers: {
            Authorization: "Bearer " + accessToken
        }
    }

    return Axios.request(options);
}

const MovieScreen = ( {route, navigation} ) => {

    const { accessToken, refreshToken, id } = route.params;

    const [movie, setMovie] = React.useState();
    const [genres, setGenres] = React.useState([]);
    const [persons, setPersons] = React.useState([]);

    React.useEffect(() => {
        movie_id(id, accessToken)
            .then(response => {
                setMovie(response.data.movie);
                setGenres(response.data.genres);
                setPersons(response.data.persons);
            })
            .catch(error => {
                const payload = {
                    refreshToken: refreshToken
                }

                refresh(payload)
                    .then(response => {
                        navigation.setParams({
                            accessToken: response.data.accessToken,
                            refreshToken: response.data.refreshToken
                          });
                    })
            })
    }, [id, accessToken, refreshToken]);

    return (
        <View style={styles.container}>
            {movie && 
                <ScrollView contentContainerStyle={{ flexGrow: 1, alignItems: 'center' }}>
                        <Image 
                            style={styles.thumbnail}
                            source={{uri: "https://image.tmdb.org/t/p/w300/"+movie.posterPath}}/>
                    <Text 
                        style={{ textAlign: 'center', fontSize: 15, fontWeight: "bold", margin: 10 }}>
                        Title: {movie.title} {"\n"}
                        Year: {movie.year} {"\n"}
                        Director: {movie.director} {"\n"}
                        Rating: {movie.rating} Stars {"\n"}
                        Votes: {movie.numVotes} {"\n"}
                        Overview: {movie.overview} {"\n"}

                        Genres: {genres && genres.map((genre, i) =>
                            <Text key={i}>{genre.name}{i < genres.length - 1 ? ", " : ""}</Text>)}

                        {"\n"}

                        People: {persons && persons.map((person, i) =>
                            <Text key={i}>{person.name}{i < persons.length - 1 ? ", " : ""}</Text>)}
                    </Text>
                </ScrollView>
            }
        </View>
    )
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        justifyContent: 'center',
    },
    thumbnail: {
        width: 350,
        height: 350,
        margin: 10,
    },
});

export default MovieScreen;