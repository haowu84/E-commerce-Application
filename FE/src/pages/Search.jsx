import React from "react";
import styled from "styled-components";
import {useForm} from "react-hook-form";
import {Link} from "react-router-dom";
import {useUser} from "../hook/User";
import Idm from "backend/idm";
import Movies from "backend/movies";
import {Button, Form, Row, Col, Table, Stack} from "react-bootstrap";

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


const Search = () => {
    const {accessToken, setAccessToken, refreshToken, setRefreshToken} = useUser();

    const {register, getValues, handleSubmit} = useForm();

    const [movies, setMovies] = React.useState([]);
    const [page, setPage] = React.useState(1);

    const submitSearch = (page) => {
        const title = getValues("title").trim();
        const year = getValues("year").trim();
        const director = getValues("director").trim();
        const genre = getValues("genre").trim();

        const queryParams = {
            title: title !== "" ? title : null,
            year:  year !== "" ? year : null,
            director: director !== "" ? director : null,
            genre: genre !== "" ? genre : null,
            orderBy: getValues("orderBy"),
            direction: getValues("direction"),
            limit: parseInt(getValues("limit")),
            page: page
        }

        Movies.movie_search(queryParams, accessToken)
            .then(response => setMovies(response.data.movies))
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

    const changePageAndSearch = (page) =>
    {
        setPage(page);
        submitSearch(page);
    }

    return (
        <StyledDiv>
            <StyledH1>Movie Search</StyledH1>

            <Form>
                <Row className="mb-3">
                    <Form.Group as={Col}>
                        <Form.Label>Title</Form.Label>
                        <Form.Control type="search" placeholder="Title" {...register("title")}/>
                    </Form.Group>

                    <Form.Group as={Col}>
                        <Form.Label>Year</Form.Label>
                        <Form.Control type="search" placeholder="Year" {...register("year")}/>
                    </Form.Group>
                </Row>

                <Row className="mb-3">
                    <Form.Group as={Col}>
                        <Form.Label>Director</Form.Label>
                        <Form.Control type="search" placeholder="Director" {...register("director")}/>
                    </Form.Group>

                    <Form.Group as={Col}>
                        <Form.Label>Genre</Form.Label>
                        <Form.Control type="search" placeholder="Genre" {...register("genre")}/>
                    </Form.Group>
                </Row>

                <Row className="mb-3">
                    <Form.Group as={Col}>
                        <Form.Label>Sort By</Form.Label>
                        <Form.Select {...register("orderBy")}>
                            <option value={"title"}>title</option>
                            <option value={"rating"}>rating</option>
                            <option value={"year"}>year</option>
                    </Form.Select>
                    </Form.Group>

                    <Form.Group as={Col}>
                        <Form.Label>Order By</Form.Label>
                        <Form.Select {...register("direction")}>
                            <option value={"asc"}>asc</option>
                            <option value={"desc"}>desc</option>
                    </Form.Select>
                    </Form.Group>

                    <Form.Group as={Col}>
                        <Form.Label>Limit By</Form.Label>
                        <Form.Select {...register("limit")}>
                            <option value={10}>10</option>
                            <option value={25}>25</option>
                            <option value={50}>50</option>
                            <option value={100}>100</option>
                        </Form.Select>
                    </Form.Group>
                </Row>

                <Stack direction="horizontal" gap={2}>
                    <Button variant="primary" onClick={handleSubmit(() => changePageAndSearch(page - 1))}>
                        Prev
                    </Button>
                    <Button variant="primary" onClick={handleSubmit(() => changePageAndSearch(1))}>
                        Search
                    </Button>
                    <Button variant="primary" onClick={handleSubmit(() => changePageAndSearch(page + 1))}>
                        Next
                    </Button>
                </Stack>
            </Form>

            <p className="text-center">Page {page}</p>

            <Table striped bordered hover>
                <thead>
                <tr>
                    <th class="text-center">Image</th>
                    <th class="text-center">Title</th>
                    <th class="text-center">Year</th>
                    <th class="text-center">Director</th>
                </tr>
                </thead>
                <tbody>
                {movies && movies.map((movie, i) =>
                    <tr key={i}>
                        <td>
                            <StyledLink to={"/movie/"+movie.id}>
                                <img src={"https://image.tmdb.org/t/p/w300/"+movie.backdropPath}/>
                            </StyledLink>
                        </td>
                        <td class="text-center align-middle">{movie.title}</td>
                        <td class="text-center align-middle">{movie.year}</td>
                        <td class="text-center align-middle">{movie.director}</td>
                    </tr>
                )}
                </tbody>
            </Table>
        </StyledDiv>
    );
}

export default Search;