package com.github.klefstad_teaching.cs122b.movies.repo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.klefstad_teaching.cs122b.movies.model.data.*;
import com.github.klefstad_teaching.cs122b.movies.model.request.MovieSearchRequest;
import com.github.klefstad_teaching.cs122b.movies.model.request.PersonSearchRequest;
import com.github.klefstad_teaching.cs122b.movies.model.response.MovieDetailResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MovieRepo
{
    private final NamedParameterJdbcTemplate template;
    private final ObjectMapper objectMapper;

    private final static String MOVIE_NO_GENRE =
            "SELECT m.id AS id, title, year, p.name AS director, rating, backdrop_path AS backdropPath, poster_path AS posterPath, hidden " +
                    "FROM movies.movie m " +
                    "JOIN movies.person p " +
                    "ON m.director_id = p.id " +
                    "WHERE TRUE ";

    private final static String MOVIE_WITH_GENRE =
            "SELECT m.id AS id, title, year, p.name AS director, rating, backdrop_path AS backdropPath, poster_path AS posterPath, hidden " +
                    "FROM movies.movie m " +
                    "JOIN movies.person p " +
                    "ON m.director_id = p.id " +
                    "JOIN movies.movie_genre mg " +
                    "ON m.id = mg.movie_id " +
                    "JOIN movies.genre g " +
                    "ON mg.genre_id = g.id " +
                    "WHERE TRUE ";

    private final static String MOVIE_WITH_PERSONID =
            "SELECT m.id AS id, title, year, p.name AS director, rating, backdrop_path AS backdropPath, poster_path AS posterPath, hidden " +
                    "FROM movies.movie m " +
                    "JOIN movies.person p " +
                    "ON m.director_id = p.id " +
                    "JOIN movies.movie_person mp " +
                    "ON m.id = mp.movie_id " +
                    "WHERE mp.person_id = :personId ";

    private final static String MOVIE_WITH_ID =
            "SELECT m.id AS id, title, year, p.name AS director, rating, num_votes AS numVotes, budget, revenue, overview, backdrop_path AS backdropPath, poster_path AS posterPath, hidden, " +
                    "(SELECT JSON_ARRAYAGG(JSON_OBJECT('id', g.id, 'name', g.name)) FROM " +
                    "(SELECT DISTINCT g.id, g.name " +
                    "FROM movies.movie m " +
                    "JOIN movies.movie_genre mg " +
                    "ON m.id = mg.movie_id " +
                    "JOIN movies.genre g " +
                    "ON mg.genre_id = g.id " +
                    "WHERE mg.movie_id = :movieId " +
                    "ORDER BY g.name, g.id) AS g) AS genres, " +
                    "(SELECT JSON_ARRAYAGG(JSON_OBJECT('id', p.id, 'name', p.name)) FROM " +
                    "(SELECT DISTINCT p.id, p.name, p.popularity " +
                    "FROM movies.movie m " +
                    "JOIN movies.movie_person mp " +
                    "ON m.id = mp.movie_id " +
                    "JOIN movies.person p " +
                    "ON mp.person_id = p.id " +
                    "WHERE m.id = :movieId " +
                    "ORDER BY p.popularity DESC, p.id) AS p) AS persons " +
                    "FROM movies.movie m " +
                    "JOIN movies.person p " +
                    "ON m.director_id = p.id " +
                    "WHERE m.id = :movieId ";

    private final static String PERSON_NO_TITLE =
            "SELECT p.id AS id, p.name AS name, birthday, biography, birthplace, popularity, profile_path AS profilePath " +
                    "FROM movies.person p " +
                    "WHERE TRUE ";

    private final static String PERSON_WITH_TITLE =
            "SELECT p.id AS id, p.name AS name, birthday, biography, birthplace, popularity, profile_path AS profilePath " +
                    "FROM movies.person p " +
                    "JOIN movies.movie_person mp " +
                    "ON p.id = mp.person_id " +
                    "JOIN movies.movie m " +
                    "ON mp.movie_id = m.id " +
                    "WHERE TRUE ";

    private final static String PERSON_WITH_ID =
            "SELECT p.id AS id, p.name AS name, birthday, biography, birthplace, popularity, profile_path AS profilePath " +
                    "FROM movies.person p " +
                    "WHERE p.id = :personId ";

    @Autowired
    public MovieRepo(ObjectMapper objectMapper, NamedParameterJdbcTemplate template)
    {
        this.template = template;
        this.objectMapper = objectMapper;
    }

    public List<Movie> selectMovies(MovieSearchRequest request, boolean hide)
    {
        StringBuilder sql;
        MapSqlParameterSource source = new MapSqlParameterSource();

        if (request.getGenre() != null)
        {
            sql = new StringBuilder(MOVIE_WITH_GENRE);
            sql.append(" AND ");
            sql.append(" g.name LIKE :genreName ");
            source.addValue("genreName", '%' + request.getGenre() + '%', Types.VARCHAR);
        }
        else
        {
            sql = new StringBuilder(MOVIE_NO_GENRE);
        }

        if (request.getTitle() != null)
        {
            sql.append(" AND ");
            sql.append(" m.title LIKE :title ");
            source.addValue("title", '%' + request.getTitle() + '%', Types.VARCHAR);
        }

        if (request.getYear() != null)
        {
            sql.append(" AND ");
            sql.append(" m.year = :year ");
            source.addValue("year", request.getYear(), Types.INTEGER);
        }

        if (request.getDirector() != null)
        {
            sql.append(" AND ");
            sql.append(" p.name LIKE :directorName ");
            source.addValue("directorName", '%' + request.getDirector() + '%', Types.VARCHAR);
        }

        if (hide)
        {
            sql.append(" AND ");
            sql.append(" m.hidden = 0 ");
        }

        sql.append(MovieOrder.fromString(request.getOrderBy(), request.getDirection()).toSql());
        sql.append(Pagination.toString(request.getLimit(), request.getPage()));

        return this.template.query(
                sql.toString(),
                source,
                (rs, rowNum) -> new Movie()
                        .setId(rs.getLong("id"))
                        .setTitle(rs.getString("title"))
                        .setYear(rs.getInt("year"))
                        .setDirector(rs.getString("director"))
                        .setRating(rs.getDouble("rating"))
                        .setBackdropPath(rs.getString("backdropPath"))
                        .setPosterPath(rs.getString("posterPath"))
                        .setHidden(rs.getBoolean("hidden"))
        );
    }

    public List<Movie> selectMoviesByPersonId(MovieSearchRequest request, boolean hide, Long personId)
    {
        StringBuilder sql = new StringBuilder(MOVIE_WITH_PERSONID);

        if (hide)
        {
            sql.append(" AND ");
            sql.append(" m.hidden = 0 ");
        }

        sql.append(MovieOrder.fromString(request.getOrderBy(), request.getDirection()).toSql());
        sql.append(Pagination.toString(request.getLimit(), request.getPage()));

        return this.template.query(
                sql.toString(),
                new MapSqlParameterSource().addValue("personId", personId, Types.INTEGER),
                (rs, rowNum) -> new Movie()
                        .setId(rs.getLong("id"))
                        .setTitle(rs.getString("title"))
                        .setYear(rs.getInt("year"))
                        .setDirector(rs.getString("director"))
                        .setRating(rs.getDouble("rating"))
                        .setBackdropPath(rs.getString("backdropPath"))
                        .setPosterPath(rs.getString("posterPath"))
                        .setHidden(rs.getBoolean("hidden"))
        );
    }

    public MovieDetailResponse selectMovieById(boolean hide, Long movieId)
    {
        StringBuilder sql = new StringBuilder(MOVIE_WITH_ID);

        if (hide)
        {
            sql.append(" AND ");
            sql.append(" m.hidden = 0 ");
        }

        try
        {
            return this.template.queryForObject(
                    sql.toString(),
                    new MapSqlParameterSource().addValue("movieId", movieId, Types.INTEGER),
                    this::mapping
            );
        }
        catch (DataAccessException e)
        {
            throw e;
        }
    }

    private MovieDetailResponse mapping(ResultSet rs, int rowNumber)
    {
        List<MovieGenre> genres = null;
        List<MoviePerson> persons = null;
        MovieDetail movie = null;

        try
        {
            MovieGenre[] movieGenreArray = objectMapper.readValue(rs.getString("genres"), MovieGenre[].class);
            genres = Arrays.stream(movieGenreArray).collect(Collectors.toList());

            MoviePerson[] moviePersonArray = objectMapper.readValue(rs.getString("persons"), MoviePerson[].class);
            persons = Arrays.stream(moviePersonArray).collect(Collectors.toList());

            movie = new MovieDetail()
                    .setId(rs.getLong("id"))
                    .setTitle(rs.getString("title"))
                    .setYear(rs.getInt("year"))
                    .setDirector(rs.getString("director"))
                    .setRating(rs.getDouble("rating"))
                    .setNumVotes(rs.getLong("numVotes"))
                    .setBudget(rs.getLong("budget"))
                    .setRevenue(rs.getLong("revenue"))
                    .setOverview(rs.getString("overview"))
                    .setBackdropPath(rs.getString("backdropPath"))
                    .setPosterPath(rs.getString("posterPath"))
                    .setHidden(rs.getBoolean("hidden"));

        }
        catch (SQLException | JsonProcessingException ignored)
        {
        }

        return new MovieDetailResponse()
                .setMovie(movie)
                .setGenres(genres)
                .setPersons(persons);
    }

    public List<Person> selectPersons(PersonSearchRequest request)
    {
        StringBuilder sql;
        MapSqlParameterSource source = new MapSqlParameterSource();

        if (request.getMovieTitle() != null)
        {
            sql = new StringBuilder(PERSON_WITH_TITLE);
            sql.append(" AND ");
            sql.append(" m.title LIKE :movieTitle ");
            source.addValue("movieTitle", '%' + request.getMovieTitle() + '%', Types.VARCHAR);
        }
        else
        {
            sql = new StringBuilder(PERSON_NO_TITLE);
        }

        if (request.getName() != null)
        {
            sql.append(" AND ");
            sql.append(" p.name LIKE :name ");
            source.addValue("name", '%' + request.getName() + '%', Types.VARCHAR);
        }

        if (request.getBirthday() != null)
        {
            sql.append(" AND ");
            sql.append(" p.birthday = :birthday ");
            source.addValue("birthday", request.getBirthday(), Types.VARCHAR);
        }

        sql.append(PersonOrder.fromString(request.getOrderBy(), request.getDirection()).toSql());
        sql.append(Pagination.toString(request.getLimit(), request.getPage()));

        return this.template.query(
                sql.toString(),
                source,
                (rs, rowNum) -> new Person()
                        .setId(rs.getLong("id"))
                        .setName(rs.getString("name"))
                        .setBirthday(rs.getString("birthday"))
                        .setBiography(rs.getString("biography"))
                        .setBirthplace(rs.getString("birthplace"))
                        .setPopularity(rs.getFloat("popularity"))
                        .setProfilePath(rs.getString("profilePath"))
        );
    }

    public Person selectPersonsById(Long personId)
    {
        StringBuilder sql = new StringBuilder(PERSON_WITH_ID);

        try
        {
            return this.template.queryForObject(
                    sql.toString(),
                    new MapSqlParameterSource().addValue("personId", personId, Types.INTEGER),
                    (rs, rowNum) -> new Person()
                            .setId(rs.getLong("id"))
                            .setName(rs.getString("name"))
                            .setBirthday(rs.getString("birthday"))
                            .setBiography(rs.getString("biography"))
                            .setBirthplace(rs.getString("birthplace"))
                            .setPopularity(rs.getFloat("popularity"))
                            .setProfilePath(rs.getString("profilePath"))
            );
        }
        catch (DataAccessException e)
        {
            throw e;
        }
    }
}
