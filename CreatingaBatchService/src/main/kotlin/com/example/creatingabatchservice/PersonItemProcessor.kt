package com.example.creatingabatchservice

import org.springframework.batch.item.ItemProcessor

class PersonItemProcessor: ItemProcessor<Person, Person> {

    val log = logger()

    override fun process(person: Person): Person {
        val transformedPerson = Person(person.firstName.uppercase(), person.lastName.uppercase())

        log.info("Converting $person into $transformedPerson")

        return transformedPerson
    }
}
