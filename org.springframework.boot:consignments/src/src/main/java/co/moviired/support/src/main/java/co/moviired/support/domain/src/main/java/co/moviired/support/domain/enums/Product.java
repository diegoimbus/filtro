package co.moviired.support.domain.enums;

import java.util.Arrays;

public enum Product {

    PHONE_RECHARGE("RECARGAS CELULAR", "operator_count", "operator_value", "phoneRecharge", true),
    DIRECTV("RECARGAS DIRECTTV", "directv_count", "directv_value", "direcTvRecharge", true),
    SITP("RECARGAS SITP", "sitp_count", "sitp_value", "sitpRecharge", true),
    ALLIES_MOVIIRED("RECARGAS ALIADOS MOVIIRED", "allies_count", "allies_value", "alliesRecharge", true),
    BILL_PAYMENT("PAGO DE FACTURAS", "bill_count", "bill_value", "billPayment", true),
    DIGITAL_CONTENT("CONTENIDO DIGITAL", "digital_count", "digital_value", "digitalContent", true),

    CASH_OUT("RETIRO DE EFECTIVO", null, "cash_out_value", "cashOut", false),
    CASH_IN("DEPÓSITOS", null, "cash_in_value", "cashIn", false),
    COMMISSIONS("COMISIONES", null, "total_commissions", "totalCommissions", false),

    // Not is provided for data team
    TOTAL_SALES("TOTAL_SALES", null, "total_sales", "totalSales", false),

    UNKNOWN("UNKNOWN", null, null, null, false);

    private final String productName;
    private final String countPlacerHolder;
    private final String valuePlaceHolder;
    private final boolean useForCalculateTotalSales;
    private final String displayPlaceHolder;

    Product(String productName, String countPlacerHolder, String valuePlaceHolder, String displayPlaceHolder, boolean useForCalculateTotalSales) {
        this.productName = productName;
        this.countPlacerHolder = countPlacerHolder;
        this.valuePlaceHolder = valuePlaceHolder;
        this.useForCalculateTotalSales = useForCalculateTotalSales;
        this.displayPlaceHolder = displayPlaceHolder;
    }

    public String getProductName() {
        return productName;
    }

    public String getCountPlacerHolder() {
        return countPlacerHolder;
    }

    public String getValuePlaceHolder() {
        return valuePlaceHolder;
    }

    public boolean isUseForCalculateTotalSales() {
        return useForCalculateTotalSales;
    }

    public String getDisplayPlaceHolder() {
        return displayPlaceHolder;
    }

    public static Product getByProductName(String productName) {
        return Arrays.stream(values()).filter(value -> value.getProductName().equalsIgnoreCase(productName)).findFirst().orElse(UNKNOWN);
    }
}

