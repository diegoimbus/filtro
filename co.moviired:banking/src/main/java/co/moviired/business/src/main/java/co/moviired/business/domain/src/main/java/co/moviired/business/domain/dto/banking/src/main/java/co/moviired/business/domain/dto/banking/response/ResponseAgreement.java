package co.moviired.business.domain.dto.banking.response;

import co.moviired.business.domain.jpa.movii.entity.Biller;
import co.moviired.business.domain.jpa.movii.entity.BillerCategory;
import co.moviired.business.provider.mahindra.response.LoginResponseQuery;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseAgreement extends Response {

    private List<Biller> listBiller;
    private List<BillerCategory> listCategories;
    private LoginResponseQuery user;

    public ResponseAgreement(String error, String codigo, String mensaje) {
        setErrorType(error);
        setErrorCode(codigo);
        setErrorMessage(mensaje);
    }

    public ResponseAgreement(String error, String codigo, String mensaje, LoginResponseQuery login, List<Biller> listBiller, List<BillerCategory> listCategories) {
        setErrorType(error);
        setErrorCode(codigo);
        setErrorMessage(mensaje);
        this.user = login;
        this.listBiller = listBiller;
        this.listCategories = listCategories;
    }

}

