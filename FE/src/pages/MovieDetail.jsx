import React from "react";
import styled from "styled-components";
import {useForm} from "react-hook-form";
import {useParams} from "react-router-dom";
import {useUser} from "../hook/User";
import Idm from "backend/idm";
import Movies from "backend/movies";
import Billing from "backend/billing";
import {Button, Form, Card, ListGroup, ListGroupItem, Row, Col, Stack} from "react-bootstrap";

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


const MovieDetail = () => {
    const {accessToken, setAccessToken, refreshToken, setRefreshToken} = useUser();

    const {id} = useParams();

    const {register, getValues, handleSubmit} = useForm();

    const [movie, setMovie] = React.useState();
    const [genres, setGenres] = React.useState([]);
    const [persons, setPersons] = React.useState([]);

    React.useEffect(() => {
        Movies.movie_id(id, accessToken)
            .then(response => {
                setMovie(response.data.movie);
                setGenres(response.data.genres);
                setPersons(response.data.persons);
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
    }, [id, accessToken, refreshToken]);

    const submitCartInsert = () => {
        const payLoad = {
            movieId: id,
            quantity: parseInt(getValues("quantity"))
        }

        Billing.cart_insert(payLoad, accessToken)
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
            <StyledH1>Movie Detail</StyledH1>

            {movie &&
                <Stack direction="horizontal" gap={2}>
                    <Stack gap={2}>
                        <img src={"https://image.tmdb.org/t/p/w500/"+movie.posterPath}/>
                        <Form>
                            <Form.Group>
                                <Form.Label>Quantity</Form.Label>
                                <Form.Select {...register("quantity")} >
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
                            </Form.Group>

                            <Button variant="primary" onClick={handleSubmit(submitCartInsert)}>Add To Cart</Button>
                        </Form>
                    </Stack>

                    <Card className="text-center">
                        <Card.Body>
                            <Card.Title>Title: {movie.title}</Card.Title>
                            <ListGroup className="list-group-flush">
                                <ListGroupItem>Year: {movie.year}</ListGroupItem>
                                <ListGroupItem>Director: {movie.director}</ListGroupItem>
                                <ListGroupItem>Rating: {movie.rating} Stars</ListGroupItem>
                                <ListGroupItem>Votes: {movie.numVotes.toLocaleString()}</ListGroupItem>
                                <ListGroupItem>Budget: ${movie.budget.toLocaleString()}</ListGroupItem>
                                <ListGroupItem>Revenue: ${movie.revenue.toLocaleString()}</ListGroupItem>
                                <ListGroupItem>Overview: {movie.overview}</ListGroupItem>
                                <ListGroupItem>
                                    Genres: {genres && genres.map((genre, i) =>
                                    <Form.Text key={i}>{genre.name}{i < genres.length - 1 ? ", " : ""}</Form.Text>)}
                                </ListGroupItem>
                                <ListGroupItem>
                                    Actors: {persons && persons.map((person, i) =>
                                    <Form.Text key={i}>{person.name}{i < persons.length - 1 ? ", " : ""}</Form.Text>)}
                                </ListGroupItem>
                            </ListGroup>
                        </Card.Body>
                    </Card>
                </Stack>
            }
        </StyledDiv>
    )
}

export default MovieDetail;