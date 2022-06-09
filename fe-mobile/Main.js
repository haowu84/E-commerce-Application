import React from 'react';
import { Text, FlatList, StyleSheet, View, TextInput, Button, Image, TouchableHighlight } from 'react-native';
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

async function movie_search(queryParams, accessToken) {
    const options = {
        method: "GET",
        baseURL: "http://10.0.2.2:8083",
        url:  "/movie/search",
        params: queryParams,
        headers: {
            Authorization: "Bearer " + accessToken
        }
    }

    return Axios.request(options);
}


const movieDivider = () => {
    return (
        <View
        style={{
            height: 1,
            width: "100%",
            backgroundColor: "#0000FF",
        }}
        />
    );
}


const MainScreen = ({ route, navigation }) => {
    const { accessToken, refreshToken } = route.params;
    
    const [category, onChangeCategory] = React.useState(null);
    const [content, onChangeContent] = React.useState(null);

    const [movies, setMovies] = React.useState([]);
    const [page, setPage] = React.useState(1);

    const submitSearch = (category, content, page) => {
        const queryParams = {
            title: category === "title" ? content.trim() : null,
            year:  category === "year" ? content.trim() : null,
            director: category === "director" ? content.trim() : null,
            genre: category === "genre" ? content.trim() : null,
            page: page
        }

        movie_search(queryParams, accessToken)
            .then(response => {
                setMovies(response.data.movies);
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
    }

    const changePageAndSearch = (category, content, page) =>
    {
        if (page <= 0)
        {
            alert("No more previous pages");
            return;
        }

        setPage(page);
        submitSearch(category, content, page);
    }


    return (
        <View style={styles.container}>
            <TextInput
                style={styles.input}
                onChangeText={onChangeCategory}
                placeholder="title, year, director, or genre"
                value={category}
            />
            <TextInput
                style={styles.input}
                onChangeText={onChangeContent}
                value={content}
                placeholder="Search"
            />
            <View style={styles.buttonContainer}>
                <Button
                    onPress = {() => changePageAndSearch(category, content, page - 1)}
                    title="PREV"
                />
                <Button
                    onPress = {() => changePageAndSearch(category, content, 1)}
                    title="SEARCH"
                />
                <Button
                    onPress = {() => changePageAndSearch(category, content, page + 1)}
                    title="NEXT"
                />
            </View>

            <FlatList
                data={movies}
                keyExtractor={item => item.id}
                renderItem={({ item }) => (
                    <View style={styles.container}>
                        <TouchableHighlight 
                            onPress = {() => {navigation.navigate("Movie", {accessToken: accessToken, refreshToken: refreshToken, id: item.id});}} 
                            underlayColor="white">
                            <View style={styles.subContainer} flexDirection='row'>
                                <Image 
                                    style={styles.thumbnail}
                                    source={{uri: "https://image.tmdb.org/t/p/w300/"+item.backdropPath}}/>
                                <Text 
                                    style={{ fontSize: 15, fontWeight: "bold", margin: 10}}>
                                    {item.title} {"\n"}
                                    {item.year} {"\n"}
                                    {item.director}
                                </Text>
                            </View>  
                        </TouchableHighlight> 
                    </View>  
                )}
                ItemSeparatorComponent={movieDivider}
            />
        </View>
    );
};

const styles = StyleSheet.create({
    container: {
        flex: 1,
        justifyContent: 'center',
    },
    subContainer: {
        flex: 1,
    },
    buttonContainer: {
        margin: 10,
        flexDirection: 'row',
        justifyContent: 'space-evenly'
    },
    input: {
        padding: 10,
        margin: 10,
        height: 40,
        borderWidth: 1,
    },
    thumbnail: {
        width: 50,
        height: 50,
        margin: 10,
    },
});

export default MainScreen;