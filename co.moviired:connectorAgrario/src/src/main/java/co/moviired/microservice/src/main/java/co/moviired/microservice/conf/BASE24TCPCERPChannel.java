package co.moviired.microservice.conf;

import org.jpos.iso.BaseChannel;
import org.jpos.iso.ISOPackager;
import org.jpos.util.LogEvent;
import org.jpos.util.Logger;

import java.io.IOException;

public class BASE24TCPCERPChannel extends BaseChannel {

    public BASE24TCPCERPChannel(String host, int port, ISOPackager p) {
        super(host, port, p);
    }

    @Override
    protected void sendMessageLength(int len) throws IOException {
        this.serverOut.write(len >> 8);
        this.serverOut.write(len);
    }

    @Override
    protected int getMessageLength() throws IOException {
        int l = 0;
        byte[] b = new byte[2];
        Logger.log(new LogEvent(this, "get-message-length"));

        while (l == 0) {
            this.serverIn.readFully(b, 0, 2);
            l = (b[0] & 255) << 8 | b[1] & 255;
            if (l == 0) {
                this.serverOut.write(b);
                this.serverOut.flush();
            }
        }
        Logger.log(new LogEvent(this, "got-message-length", Integer.toString(l)));
        return l;
    }

}

