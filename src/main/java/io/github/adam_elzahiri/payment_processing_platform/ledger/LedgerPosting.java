package io.github.adam_elzahiri.payment_processing_platform.ledger;

import io.github.adam_elzahiri.payment_processing_platform.account.Account;
import io.github.adam_elzahiri.payment_processing_platform.transfer.Transfer;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "ledger_posting")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LedgerPosting {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Generated(event = EventType.INSERT)
    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "transfer_id", nullable = false)
    @NotNull
    private Transfer transfer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    @NotNull
    private Account account;

    @Column(name = "amount", nullable = false, precision = 20, scale = 4)
    @NotNull
    private BigDecimal amount;

    public LedgerPosting(Transfer transfer, Account account, BigDecimal amount) {
        this.transfer = transfer;
        this.account = account;
        this.amount = amount;
    }

}
