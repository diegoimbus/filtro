package co.movii.auth.server.domain.repository;

import co.movii.auth.server.domain.entity.AuthRegisterPassword;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;

public interface AuthRegisterPasswordRepository extends CrudRepository<AuthRegisterPassword, Long>, Serializable {
}

