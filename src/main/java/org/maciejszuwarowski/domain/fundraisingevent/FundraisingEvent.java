package org.maciejszuwarowski.domain.fundraisingevent;

import jakarta.persistence.*;
import lombok.Builder; // Możesz nadal używać @Builder
import lombok.Getter;
import lombok.NoArgsConstructor; // Dodaj konstruktor bezargumentowy
import lombok.AllArgsConstructor; // Dodaj konstruktor z wszystkimi argumentami
import lombok.Setter; // Jeśli potrzebujesz setterów
import org.maciejszuwarowski.domain.shared.Currency;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "fundraising_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FundraisingEvent {

        @Id
        private String id;

        @Column(nullable = false)
        private String nameOfFundraisingEvent;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private Currency currencyOfTheMoneyAccount;

        @Column(nullable = false, precision = 19, scale = 2)
        private BigDecimal amountOfMoney;

}