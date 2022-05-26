package com.github.klefstad_teaching.cs122b.movies.model.response;

import com.github.klefstad_teaching.cs122b.core.base.ResponseModel;
import com.github.klefstad_teaching.cs122b.movies.model.data.Person;

public class PersonDetailResponse extends ResponseModel<PersonDetailResponse> {
    private Person person;

    public Person getPerson() {
        return person;
    }

    public PersonDetailResponse setPerson(Person person) {
        this.person = person;
        return this;
    }
}
