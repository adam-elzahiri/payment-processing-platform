package io.github.adam_elzahiri.payment_processing_platform.account;

import io.github.adam_elzahiri.payment_processing_platform.customer.Customer;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "account")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Generated(event = EventType.INSERT)
    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    @NotNull
    private Customer customer;

    @Column(name = "balance", nullable = false, precision = 20, scale = 4)
    @NotNull
    @PositiveOrZero // possibly remove later if we start supporting negative balances
    private BigDecimal balance;

    @Column(name = "currency_code", nullable = false, length = 3)
    @NotNull
    private String currencyCode;

    public Account(Customer customer, String currencyCode) {
        this.customer = customer;
        this.currencyCode = currencyCode;
        this.balance = BigDecimal.ZERO;
    }

}
