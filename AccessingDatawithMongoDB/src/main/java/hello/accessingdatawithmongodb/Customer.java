package hello.accessingdatawithmongodb;

import org.springframework.data.annotation.Id;

public class Customer {
    @Id
    private String id;

    private String firstName;
    private String lastName;

    public Customer() {}

    public Customer(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return String.format(
                "Customer[id=%s, firstName='%s', lastName='%s']",
                id, firstName, lastName);
    }

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    // 테스트를 위해 함수를 만들었다. 굳이 equals Override 까지 필요할까
    public boolean sameCustomer(Customer customer) {
        return id.equals(customer.getId()) && firstName.equals(customer.getFirstName()) && lastName.equals(customer.getLastName());
    }
}
