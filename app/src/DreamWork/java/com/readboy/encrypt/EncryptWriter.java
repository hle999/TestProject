package com.readboy.encrypt;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class EncryptWriter {

    public EncryptWriter(String filepath) throws FileNotFoundException {
        fos = new FileOutputStream(filepath);

    }

    public void write(String data) {
        msb.append(data);
    }

    public void flush() throws IOException {
        if (fos != null && msb.length() > 0) {
            byte[] data = msb.toString().getBytes("gbk");
            data = Encrypt.nativeEndec(data, -1);
            if (data == null) {
                throw new IOException("internal encryption error");
            }
            fos.write(data);
            msb.setLength(0);
        }
    }

    public void close() throws IOException {
        if (fos != null)
            fos.close();
    }

    private StringBuffer msb = new StringBuffer();
    private FileOutputStream fos = null;
}
