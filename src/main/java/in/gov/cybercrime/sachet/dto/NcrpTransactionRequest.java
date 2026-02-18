package in.gov.cybercrime.sachet.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class NcrpTransactionRequest {
    private String ackNo;
    private String bankName;
    private String accountNo;
    private LocalDate transactionDate;
    private BigDecimal amount;
    private String layer;
}
