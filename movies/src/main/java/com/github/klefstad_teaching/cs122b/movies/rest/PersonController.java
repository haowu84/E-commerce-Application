package com.github.klefstad_teaching.cs122b.movies.rest;

import com.github.klefstad_teaching.cs122b.core.result.MoviesResults;
import com.github.klefstad_teaching.cs122b.movies.model.data.Person;
import com.github.klefstad_teaching.cs122b.movies.model.request.PersonSearchRequest;
import com.github.klefstad_teaching.cs122b.movies.model.response.PersonDetailResponse;
import com.github.klefstad_teaching.cs122b.movies.model.response.PersonSearchResponse;
import com.github.klefstad_teaching.cs122b.movies.repo.MovieRepo;
import com.github.klefstad_teaching.cs122b.movies.util.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PersonController
{
    private final MovieRepo repo;
    private final Validate validate;

    @Autowired
    public PersonController(MovieRepo repo, Validate validate)
    {
        this.repo = repo;
        this.validate = validate;
    }

    @GetMapping("/person/search")
    public ResponseEntity<PersonSearchResponse> personSearch(PersonSearchRequest request)
    {
        validate.validatePersonOrderBy(request.getOrderBy());
        validate.validateDirection(request.getDirection());
        validate.validateLimit(request.getLimit());
        validate.validateOffset(request.getPage());

        List<Person> persons = repo.selectPersons(request);

        if (persons.isEmpty())
        {
            PersonSearchResponse response = new PersonSearchResponse()
                    .setResult(MoviesResults.NO_PERSONS_FOUND_WITHIN_SEARCH);

            return response.toResponse();
        }

        PersonSearchResponse response = new PersonSearchResponse()
                .setResult(MoviesResults.PERSONS_FOUND_WITHIN_SEARCH)
                .setPersons(persons);

        return response.toResponse();
    }

    @GetMapping("/person/{personId}")
    public ResponseEntity<PersonDetailResponse> personSearchById(@PathVariable("personId") Long personId)
    {
        try
        {
            Person person = repo.selectPersonsById(personId);

            PersonDetailResponse response = new PersonDetailResponse()
                    .setResult(MoviesResults.PERSON_WITH_ID_FOUND)
                    .setPerson(person);

            return response.toResponse();
        }
        catch (DataAccessException e)
        {
            PersonDetailResponse response = new PersonDetailResponse()
                    .setResult(MoviesResults.NO_PERSON_WITH_ID_FOUND);

            return response.toResponse();
        }
    }
}
