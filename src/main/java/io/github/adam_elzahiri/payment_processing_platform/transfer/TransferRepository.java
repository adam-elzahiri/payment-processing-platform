package io.github.adam_elzahiri.payment_processing_platform.transfer;

import io.github.adam_elzahiri.payment_processing_platform.customer.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TransferRepository extends JpaRepository<Customer, UUID> {
}
