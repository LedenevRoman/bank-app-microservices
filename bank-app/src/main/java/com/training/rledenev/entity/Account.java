package com.training.rledenev.entity;

import com.training.rledenev.entity.enums.CurrencyCode;
import com.training.rledenev.entity.enums.Status;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@NamedEntityGraph(name = "user-account-agreement-product-graph",
        attributeNodes = {@NamedAttributeNode(value = "client"),
                @NamedAttributeNode(value = "agreement", subgraph = "agreement")},
        subgraphs = @NamedSubgraph(name = "agreement", attributeNodes = @NamedAttributeNode(value = "product")))
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", referencedColumnName = "id")
    private User client;

    @OneToOne(
            mappedBy = "account",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    private Agreement agreement;

    @OneToMany(
            mappedBy = "debitAccount",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    private Set<Transaction> debitTransactions = new HashSet<>();

    @OneToMany(
            mappedBy = "creditAccount",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    private Set<Transaction> creditTransactions = new HashSet<>();

    @Column(name = "number")
    private String number;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    @Column(name = "balance")
    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency_code")
    private CurrencyCode currencyCode;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(id, account.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                '}';
    }
}
