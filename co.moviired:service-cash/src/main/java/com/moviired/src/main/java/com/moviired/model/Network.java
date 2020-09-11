package com.moviired.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "network")
public class Network implements Serializable {

    private String defaultOption;
    private Map<String, NetworkDetail> options;

    /**
     * metodo getOption
     *
     * @param agentCode
     * @return NetworkDetail
     */
    public NetworkDetail getOption(String agentCode) {
        Network.NetworkDetail detail = this.options.get(agentCode);

        if (null == detail) {
            detail = this.options.get(this.defaultOption);
        }

        return detail;
    }

    public enum GeneratorOTP {
        INTERNAL("Internal"),
        AVAL("Aval");

        private String value;

        GeneratorOTP(String pValue) {
            this.value = pValue;
        }

        public String value() {
            return this.value;
        }
    }

    public enum Type {
        MOVII_POINTS("movii_points"),
        ALLIES("allies");

        private String value;

        Type(String pValue) {
            this.value = pValue;
        }

        public String value() {
            return this.value;
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class NetworkDetail implements Serializable {

        @Value("${:#{false}}")
        private boolean enable;
        @Value("${:#{''}}")
        private String msisdn;
        @Value("${:#{''}}")
        private String pin;
        private String name;

        private Network.Type type;

        private Network.GeneratorOTP generatorOtp;
        private Integer lengthOTP;
        private Integer timeOtpCashout;
        private Integer timeOtpComplete;
        private boolean pendingByMerchant;
        private boolean processCreateToken;

        private Integer costTransaction;
        private String logo;
        private String description;
        private String carouselName;
        private Integer protocol;
        private Collection<String> documentType;

        /**
         * metodo isAvailableDocumentType
         *
         * @param pType
         * @return boolean
         */
        public boolean isAvailableDocumentType(String pType) {
            if (isEnable()) {
                for (String docType : documentType) {
                    if (docType.equals(pType)) {
                        return Boolean.TRUE;
                    }
                }
            }
            return Boolean.FALSE;
        }

        /**
         * metodo getExpirationDate
         *
         * @param time
         * @return Date
         */
        public Date getExpirationDate(Integer time) {
            return generateExpirationDateFrom(new Date(), time);
        }

        /**
         * metodo generateExpirationDateFrom
         *
         * @param date,time
         * @return Date
         */
        public Date generateExpirationDateFrom(Date dateParam, Integer time) {
            Date date = dateParam;
            if (null == dateParam) {
                date = new Date();
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            calendar.add(Calendar.MINUTE, time);
            return calendar.getTime();
        }
    }

}

