package hello.accessingmongodbdatawithrest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class MongoRestTest {

    MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    PersonRepository repository;

    @BeforeEach
    void beforeEach() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        repository.deleteAll();
    }

    @Test
    void indexPathTest() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.people").exists())
                .andDo(print());
    }

    @Test
    void findByLastName() throws Exception {
        Person Alice = new Person("Alice", "Smith");
        repository.save(Alice);
        Person Bob = new Person("Bob", "Smith");
        repository.save(Bob);

        mockMvc.perform(get("/people")
                        .param("name", "Smith"))
                .andExpect(jsonPath("$._embedded").exists())
                .andExpect(jsonPath("$._embedded.people").exists())
                .andExpect(jsonPath("$._embedded.people[*].firstName", containsInAnyOrder("Alice", "Bob")))
                .andExpect(jsonPath("$._embedded.people[*].lastName", containsInAnyOrder("Smith", "Smith")))
                .andExpect(status().isOk())
                .andDo(print());
    }
}
