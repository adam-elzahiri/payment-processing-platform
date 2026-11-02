package io.github.adam_elzahiri.payment_processing_platform.ledger;

import io.github.adam_elzahiri.payment_processing_platform.customer.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LedgerRepository extends JpaRepository<Customer, UUID> {
}
