package co.moviired.register.service;

import co.moviired.register.config.StatusCodeConfig;
import co.moviired.register.domain.dto.*;
import co.moviired.register.helper.SignatureHelper;
import co.moviired.register.properties.GlobalProperties;
import co.moviired.register.repository.IDepartmentRepository;
import co.moviired.register.repository.IPendingUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public final class DepartmentService extends BaseService {

    private final IDepartmentRepository departmentRepository;

    private final List<DepartmentDTO> departments;

    public DepartmentService(@NotNull IDepartmentRepository pDepartmentRepository,
                             @NotNull SignatureHelper signatureHelper,
                             @NotNull IPendingUserRepository pendingUserRepository,
                             @NotNull GlobalProperties globalProperties,
                             @NotNull StatusCodeConfig statusCodeConfig) {
        super(globalProperties, statusCodeConfig, signatureHelper, pendingUserRepository);
        this.departmentRepository = pDepartmentRepository;
        this.departments = this.getDepartments();
    }

    public Mono<DepartmentResponse> getDepartment(String departmentName) {
        return Mono.just(departmentName)
                .flatMap(depName -> {
                    StopWatch stopWatch = new StopWatch();
                    stopWatch.start();

                    // Response in ok format
                    DepartmentResponse departmentResponse = new DepartmentResponse();
                    departmentResponse.setCode("00");
                    departmentResponse.setMessage("Departments encontrados");
                    departmentResponse.setDepartments(this.departments);

                    if ((depName != null) && (!depName.trim().isEmpty())) {
                        // Buscar el departamento
                        DepartmentDTO dto = DepartmentDTO.builder().name(depName).build();
                        int found = this.departments.indexOf(dto);
                        if (found == -1) {
                            departmentResponse.setCode("-001");
                            departmentResponse.setMessage("Departmento no encontrado");
                            departmentResponse.setDepartments(null);
                            stopWatch.stop();
                            log.info("Listado de departamentos. Tiempo de ejecución: {} milisegundos", stopWatch.getTotalTimeMillis());

                            return Mono.just(departmentResponse);
                        }

                        // Devolver la nueva lista
                        List<DepartmentDTO> deptos = new ArrayList<>();
                        deptos.add(this.departments.get(found));
                        departmentResponse.setDepartments(deptos);
                    }
                    stopWatch.stop();
                    log.info("Listado de departamentos. Tiempo de ejecución: {} milisegundos", stopWatch.getTotalTimeMillis());

                    return Mono.just(departmentResponse);
                })
                .onErrorResume(Mono::error);
    }

    private List<DepartmentDTO> getDepartments() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        List<QueryDepartmentDTO> mList = this.departmentRepository.findDepartments();
        Map<DepartmentDTO, Set<MunicipalityDTO>> deps = mList.stream()
                .map(QueryDTO::new)
                .collect(
                        Collectors.groupingBy(
                                QueryDTO::getDepartment,
                                Collectors.mapping(QueryDTO::getMunicipality, Collectors.toSet())
                        )
                );
        stopWatch.stop();
        log.info("Listado de departamentos y municipios realizada satisfactoriamente. Tiempo de ejecución: {} segundos", stopWatch.getTotalTimeSeconds());
        return QueryDTO.transform(deps);
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}


