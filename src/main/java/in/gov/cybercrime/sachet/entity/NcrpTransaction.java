package in.gov.cybercrime.sachet.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "ncrp_transactions")
@Getter
@Setter
public class NcrpTransaction extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private CaseFile caseFile;

    @Column(name = "ack_no")
    private String ackNo;

    @Column(name = "bank_name", nullable = false)
    private String bankName;

    @Column(name = "account_no")
    private String accountNo;

    @Column(name = "transaction_date")
    private LocalDate transactionDate;

    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "layer")
    private String layer;
}
