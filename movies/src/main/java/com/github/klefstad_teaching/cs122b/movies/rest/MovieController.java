package com.github.klefstad_teaching.cs122b.movies.rest;

import com.github.klefstad_teaching.cs122b.core.result.MoviesResults;
import com.github.klefstad_teaching.cs122b.core.security.JWTManager;
import com.github.klefstad_teaching.cs122b.movies.model.data.Movie;
import com.github.klefstad_teaching.cs122b.movies.model.request.MovieSearchRequest;
import com.github.klefstad_teaching.cs122b.movies.model.response.MovieDetailResponse;
import com.github.klefstad_teaching.cs122b.movies.model.response.MovieSearchResponse;
import com.github.klefstad_teaching.cs122b.movies.repo.MovieRepo;
import com.github.klefstad_teaching.cs122b.movies.util.Validate;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.List;

@RestController
public class MovieController
{
    private final MovieRepo repo;
    private final Validate validate;

    @Autowired
    public MovieController(MovieRepo repo, Validate validate)
    {
        this.repo = repo;
        this.validate = validate;
    }

    @GetMapping("/movie/search")
    public ResponseEntity<MovieSearchResponse> movieSearch(@AuthenticationPrincipal SignedJWT user, MovieSearchRequest request)
    {
        validate.validateMovieOrderBy(request.getOrderBy());
        validate.validateDirection(request.getDirection());
        validate.validateLimit(request.getLimit());
        validate.validateOffset(request.getPage());

        List<Movie> movies = repo.selectMovies(request, hideMovies(user));

        if (movies.isEmpty())
        {
            MovieSearchResponse response = new MovieSearchResponse()
                    .setResult(MoviesResults.NO_MOVIES_FOUND_WITHIN_SEARCH);

            return response.toResponse();
        }

        MovieSearchResponse response = new MovieSearchResponse()
                .setResult(MoviesResults.MOVIES_FOUND_WITHIN_SEARCH)
                .setMovies(movies);

        return response.toResponse();
    }

    @GetMapping("/movie/search/person/{personId}")
    public ResponseEntity<MovieSearchResponse> movieSearchByPersonId(@AuthenticationPrincipal SignedJWT user, @PathVariable("personId") Long personId, MovieSearchRequest request)
    {
        validate.validateMovieOrderBy(request.getOrderBy());
        validate.validateDirection(request.getDirection());
        validate.validateLimit(request.getLimit());
        validate.validateOffset(request.getPage());

        List<Movie> movies = repo.selectMoviesByPersonId(request, hideMovies(user), personId);

        if (movies.isEmpty())
        {
            MovieSearchResponse response = new MovieSearchResponse()
                    .setResult(MoviesResults.NO_MOVIES_WITH_PERSON_ID_FOUND);

            return response.toResponse();
        }

        MovieSearchResponse response = new MovieSearchResponse()
                .setResult(MoviesResults.MOVIES_WITH_PERSON_ID_FOUND)
                .setMovies(movies);

        return response.toResponse();
    }

    @GetMapping("/movie/{movieId}")
    public ResponseEntity<MovieDetailResponse> movieSearchById(@AuthenticationPrincipal SignedJWT user, @PathVariable("movieId") Long movieId)
    {
        try
        {
            MovieDetailResponse response = repo.selectMovieById(hideMovies(user), movieId)
                    .setResult(MoviesResults.MOVIE_WITH_ID_FOUND);

            return response.toResponse();
        }
        catch (DataAccessException e)
        {
            MovieDetailResponse response = new MovieDetailResponse()
                    .setResult(MoviesResults.NO_MOVIE_WITH_ID_FOUND);

            return response.toResponse();
        }
    }

    private boolean hideMovies(@AuthenticationPrincipal SignedJWT user) {
        try
        {
            for (String role: user.getJWTClaimsSet().getStringListClaim(JWTManager.CLAIM_ROLES))
            {
                if (role.equalsIgnoreCase("Admin") || role.equalsIgnoreCase("Employee"))
                {
                    return false;
                }
            }
        }
        catch (ParseException ignored)
        {
        }
        return true;
    }
}
