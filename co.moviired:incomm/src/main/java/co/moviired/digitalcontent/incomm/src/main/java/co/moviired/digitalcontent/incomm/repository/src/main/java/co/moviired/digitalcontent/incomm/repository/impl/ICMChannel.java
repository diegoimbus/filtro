package co.moviired.digitalcontent.incomm.repository.impl;


import co.moviired.digitalcontent.incomm.jpos.SimpleLogListener;
import org.jpos.iso.BaseChannel;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;

public final class ICMChannel extends BaseChannel implements Serializable {

    private static final long serialVersionUID = -4605176134187533947L;

    private static final int BITS_8 = 8;
    private static final int BYTES = 255;

    private static boolean loggerIsStarted = false;
    private static String[] hideFields = new String[0];

    public static void setHideFields(String[] hideFieldsI) {
        hideFields = Arrays.copyOf(hideFieldsI, hideFieldsI.length);
    }

    public static void resetLogger(ICMChannel instance) {
        if (instance != null && instance.getLogger() != null && !loggerIsStarted) {
            instance.getLogger().removeAllListeners();
            instance.getLogger().addListener(new SimpleLogListener(hideFields));
            loggerIsStarted = true;
        }
    }

    @Override
    public void sendMessageLength(int len) throws IOException {
        resetLogger(this);
        this.serverOut.write(len >> BITS_8);
        this.serverOut.write(len);
    }


    @Override
    public int getMessageLength() throws IOException {
        resetLogger(this);
        int l = 0;
        byte[] b = new byte[2];

        while (l == 0) {
            this.serverIn.readFully(b, 0, 2);
            l = (b[0] & BYTES) << BITS_8 | b[1] & BYTES;
            if (l == 0) {
                this.serverOut.write(b);
                this.serverOut.flush();
            }
        }

        return l;
    }
}

