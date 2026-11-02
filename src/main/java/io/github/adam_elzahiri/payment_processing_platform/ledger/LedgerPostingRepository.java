package io.github.adam_elzahiri.payment_processing_platform.ledger;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LedgerPostingRepository extends JpaRepository<LedgerPosting, UUID> {
}
