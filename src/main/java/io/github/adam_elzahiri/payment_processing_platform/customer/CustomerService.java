package io.github.adam_elzahiri.payment_processing_platform.customer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    Customer createCustomer(CreateCustomerRequest createCustomerRequest) {
        Customer newCustomer = new Customer(createCustomerRequest.firstName(), createCustomerRequest.lastName());

        return customerRepository.save(newCustomer);
    }

}
