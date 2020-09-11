package co.moviired.support.endpoint.bancobogota.enums;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum BankEnum {
    BANCO_BOGOTA("001", "co.moviired.support.endpoint.bancobogota.factory.impl.NotifyBillPaymentBogota", "co.moviired.support.endpoint.bancobogota.factory.impl.RevertBankBogota");

    private static final String CLASS_NAME = BankEnum.class.getSimpleName();
    private final String compensationCode;
    private final String notifyClazz;
    private final String revertClazz;

    BankEnum(String compensationCode, String notifyClazz, String revertClazz) {
        this.compensationCode = compensationCode;
        this.notifyClazz = notifyClazz;
        this.revertClazz = revertClazz;
    }

    public static String getRevert(String compensationCode) {
        BankEnum[] arr = values();
        int len = arr.length;

        for(int i = 0; i < len; ++i) {
            BankEnum bank = arr[i];
            if (bank.getCompensationCode().equals(compensationCode)) {
                return bank.getRevertClazz();
            }
        }

        return null;
    }

    public static String getNotify(String compensationCode) {
        log.debug(CLASS_NAME, "getNotify", compensationCode);
        BankEnum[] arr = values();
        int len = arr.length;

        for(int i = 0; i < len; ++i) {
            BankEnum bank = arr[i];
            if (bank.getCompensationCode().equals(compensationCode)) {
                return bank.getNotifyClazz();
            }
        }

        return null;
    }

    public String getCompensationCode() {
        return this.compensationCode;
    }

    public String getNotifyClazz() {
        return this.notifyClazz;
    }

    public String getRevertClazz() {
        return this.revertClazz;
    }
}

