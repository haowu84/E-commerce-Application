package com.github.klefstad_teaching.cs122b.movies.model.response;

import com.github.klefstad_teaching.cs122b.core.base.ResponseModel;
import com.github.klefstad_teaching.cs122b.movies.model.data.MovieDetail;
import com.github.klefstad_teaching.cs122b.movies.model.data.MovieGenre;
import com.github.klefstad_teaching.cs122b.movies.model.data.MoviePerson;

import java.util.List;

public class MovieDetailResponse extends ResponseModel<MovieDetailResponse> {
    private MovieDetail movie;
    private List<MovieGenre> genres;
    private List<MoviePerson> persons;

    public MovieDetail getMovie() {
        return movie;
    }

    public MovieDetailResponse setMovie(MovieDetail movie) {
        this.movie = movie;
        return this;
    }

    public List<MovieGenre> getGenres() {
        return genres;
    }

    public MovieDetailResponse setGenres(List<MovieGenre> genres) {
        this.genres = genres;
        return this;
    }

    public List<MoviePerson> getPersons() {
        return persons;
    }

    public MovieDetailResponse setPersons(List<MoviePerson> persons) {
        this.persons = persons;
        return this;
    }
}
