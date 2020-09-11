package co.moviired.register.controllers;

import co.moviired.register.domain.dto.DepartmentResponse;
import co.moviired.register.service.DepartmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;

import static co.moviired.register.helper.ConstantsHelper.*;

@RestController
@Slf4j
@RequestMapping(PROJECT_PATH)
public final class DepartmentController {

    private final DepartmentService departmentServiceI;

    public DepartmentController(@NotNull DepartmentService departmentService) {
        super();
        this.departmentServiceI = departmentService;
    }

    @GetMapping(value = {DEPARTMENT_ALL_VALUES, DEPARTMENT_VALUES})
    public Mono<DepartmentResponse> getDepartment(@PathVariable(value = DEPARTMENT_NAME, required = false) String departmentName) {
        return this.departmentServiceI.getDepartment((departmentName != null) ? departmentName : "");
    }
}

