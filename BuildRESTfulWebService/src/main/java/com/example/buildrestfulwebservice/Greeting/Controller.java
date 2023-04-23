package com.example.buildrestfulwebservice.Greeting;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;

@RestController
public class Controller {

    private static final String templete = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @GetMapping("/greeting")
    public GreetingRecord greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        return new GreetingRecord(counter.incrementAndGet(), templete.formatted(name));
    }
}
