package co.moviired.support.endpoint.bancobogota.dto.consignment.out;


import lombok.Data;

@Data
public class ConsignmentOutDTO {
    private String transactionIdentificator;
    private String response;
    private String consignmentType;
    private int status;

    private String responseCode;
    private String responseMessage;
    private String phoneNumber;
    private String firtsName;
    private String lastName;
    private String email;
    private String dob;
    private String city;
    private String gender;
    private String statusId;

    public String toString() {
        return "ConsignmentOutDTO [transactionIdentificator=" + this.transactionIdentificator + ", response=" + this.response + ", consignmentType=" + this.consignmentType + ", status=" + this.status + "]";
    }
}

