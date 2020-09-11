package co.moviired.acquisition.repository;

import lombok.Data;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

@Data
@Component
public class RepositoryContainer {

    private final ProductRepository productRepository;
    private final ProductCodeRepository productCodeRepository;
    private final TransactionRepository transactionRepository;

    public RepositoryContainer(
            @NotNull ProductRepository productRepositoryI,
            @NotNull ProductCodeRepository productCodeRepositoryI,
            @NotNull TransactionRepository transactionRepositoryI) {
        this.productRepository = productRepositoryI;
        this.productCodeRepository = productCodeRepositoryI;
        this.transactionRepository = transactionRepositoryI;
    }
}

