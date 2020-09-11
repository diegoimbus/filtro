package co.moviired.digitalcontent.business.conf;

import co.moviired.digitalcontent.business.domain.repository.*;
import lombok.Data;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotNull;

@Data
@Configuration
public class RepositoriesConfig {

    private final IIncommConfigRepository incommRepository;
    private final ICategoryRepository categoryRepository;
    private final IConciliacionRepository conciliacionRepository;
    private final IProductRepository productRepository;
    private final IHomologationIncommRepository homologationIncommRepository;
    private final ITypeOperatorRepository typeOperatorRepository;
    private final ISubtypeOperatorRepository subtypeOperatorRepository;

    public RepositoriesConfig(@NotNull IIncommConfigRepository iIncommConfigRepository,
                              @NotNull ICategoryRepository iCategoryRepository,
                              @NotNull IConciliacionRepository iConciliacionRepository,
                              @NotNull IProductRepository iProductRepository,
                              @NotNull IHomologationIncommRepository iHomologationIncommRepository,
                              @NotNull ITypeOperatorRepository iTypeOperatorRepository,
                              @NotNull ISubtypeOperatorRepository iSubtypeOperatorRepository) {
        super();
        this.incommRepository = iIncommConfigRepository;
        this.categoryRepository = iCategoryRepository;
        this.conciliacionRepository = iConciliacionRepository;
        this.productRepository = iProductRepository;
        this.homologationIncommRepository = iHomologationIncommRepository;
        this.typeOperatorRepository = iTypeOperatorRepository;
        this.subtypeOperatorRepository = iSubtypeOperatorRepository;
    }
}

