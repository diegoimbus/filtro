package co.moviired.digitalcontent.incomm.repository.impl;


import org.jpos.iso.BaseChannel;

import java.io.IOException;
import java.io.Serializable;

public class ICMChannel extends BaseChannel implements Serializable {

    private static final long serialVersionUID = -4605176134187533947L;

    @Override
    public void sendMessageLength(int len) throws IOException {
        this.serverOut.write(len >> 8);
        this.serverOut.write(len);
    }


    @Override
    public int getMessageLength() throws IOException {
        int l = 0;
        byte[] b = new byte[2];

        while (l == 0) {
            this.serverIn.readFully(b, 0, 2);
            l = (b[0] & 255) << 8 | b[1] & 255;
            if (l == 0) {
                this.serverOut.write(b);
                this.serverOut.flush();
            }
        }

        return l;
    }
}

