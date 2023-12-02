package hello.accessingdatawithmongodb;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@ExtendWith(SpringExtension.class)
public class MongoDbTest {

    @Autowired
    CustomerRepository repository;

    @BeforeEach
    void clear() {
        repository.deleteAll();
    }

    @Test
    void simpleExampleTest() {
        // save a couple of customers
        repository.save(new Customer("Alice", "Smith"));
        repository.save(new Customer("Bob", "Smith"));

        // fetch all customers
        System.out.println("Customers found with findAll():");
        System.out.println("-------------------------------");
        for (Customer customer : repository.findAll()) {
            System.out.println(customer);
        }
        System.out.println();

        // fetch an individual customer
        System.out.println("Customer found with findByFirstName('Alice'):");
        System.out.println("--------------------------------");
        System.out.println(repository.findByFirstName("Alice"));

        System.out.println("Customers found with findByLastName('Smith'):");
        System.out.println("--------------------------------");
        for (Customer customer : repository.findByLastName("Smith")) {
            System.out.println(customer);
        }
    }

    @Test
    @DisplayName("insert 테스트")
    void insertTest() {
        Customer Alice = new Customer("Alice", "Smith");
        repository.save(Alice);
        Customer Bob = new Customer("Bob", "Smith");
        repository.save(Bob);

        List<Customer> findAll = repository.findAll();
        assertThat(findAll.size()).isEqualTo(2);
//        assertThat(findAll).contains(Alice, Bob); // Jpa 와는 달리 객체의 동일성이 보장되지 않는다.
        long aliceCount = findAll.stream()
                .filter(Alice::sameCustomer)
                .count();
        assertThat(aliceCount).isEqualTo(1);

        long bobCount = findAll.stream()
                .filter(Bob::sameCustomer)
                .count();
        assertThat(bobCount).isEqualTo(1);
    }

    @Test
    @DisplayName("FirstName의 중복이 없는 경우 성공한다.")
    void findOnlyOneOfFirstName() {
        Customer Alice = new Customer("Alice", "Smith");
        repository.save(Alice);
        Customer Bob = new Customer("Bob", "Smith");
        repository.save(Bob);

        Customer firstNameAlice = repository.findByFirstName(Alice.getFirstName());

        assertThat(firstNameAlice).isNotNull();
        assertThat(firstNameAlice.sameCustomer(Alice)).isTrue();
    }
}
