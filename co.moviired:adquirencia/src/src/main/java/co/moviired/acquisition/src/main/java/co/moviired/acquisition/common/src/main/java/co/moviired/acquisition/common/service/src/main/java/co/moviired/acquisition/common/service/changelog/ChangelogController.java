package co.moviired.acquisition.common.service.changelog;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

import javax.validation.constraints.NotNull;

import static co.moviired.acquisition.common.util.ConstantsHelper.*;

@Slf4j
@Controller
public class ChangelogController implements WebFluxConfigurer {

    @Value(APPLICATION_NAME)
    private String name;

    public ChangelogController() {
        super();
    }

    @Override
    public final void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**")
                .addResourceLocations("/public", "classpath:/static/", "classpath:/");
    }

    @GetMapping(value = ROOT_PATH)
    public final String get(final Model model) {
        return index(model);
    }

    @PostMapping
    public final String post(final Model model) {
        return index(model);
    }

    @PutMapping
    public final String put(final Model model) {
        return index(model);
    }

    @DeleteMapping
    public final String delete(final Model model) {
        return index(model);
    }

    @PatchMapping
    public final String patch(final Model model) {
        return index(model);
    }

    private String index(@NotNull Model model) {
        model.addAttribute(NAME, name);
        return INDEX;
    }

}

