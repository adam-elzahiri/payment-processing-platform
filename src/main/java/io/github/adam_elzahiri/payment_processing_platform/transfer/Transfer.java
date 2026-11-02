package io.github.adam_elzahiri.payment_processing_platform.transfer;

import io.github.adam_elzahiri.payment_processing_platform.account.Account;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "transfer")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Transfer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Generated(event = EventType.INSERT)
    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "source_account_id", nullable = false)
    @NotNull
    private Account sourceAccount;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "destination_account_id", nullable = false)
    @NotNull
    private Account destinationAccount;

    @Column(name = "amount", nullable = false, precision = 20, scale = 4)
    @NotNull
    @Positive
    private BigDecimal amount;

    @Column(name = "currency_code", nullable = false, length = 3)
    @NotNull
    private String currencyCode;

    @Column(name = "status", nullable = false, length = 16)
    @NotNull
    @Enumerated(EnumType.STRING)
    private TransferStatus status;

    public Transfer(Account sourceAccount, Account destinationAccount, BigDecimal amount, String currencyCode, TransferStatus status) {
        this.sourceAccount = sourceAccount;
        this.destinationAccount = destinationAccount;
        this.amount = amount;
        this.currencyCode = currencyCode;
        this.status = status;
    }

}
