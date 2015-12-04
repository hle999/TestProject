package com.readboy.encrypt;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class EncryptReader extends InputStream {

    @Override
    public int read() throws IOException {
        if (current >= length || buff == null)
            return -1;

        return (int) (buff[current++] & 0xFF);
    }

    public EncryptReader(String file) throws IOException {
        current = 0;
        fis = new FileInputStream(file);
        length = fis.available();
        if (length > 0) {
            buff = new byte[length];
            fis.read(buff);
            buff = Encrypt.nativeEndec(buff, -1);
            if (buff == null) {
                throw new IOException("internal encryption error");
            }
        }
    }

    @Override
    public int available() {
        return length;
    }

    @Override
    public void close() {
        try {
            fis.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private int length;
    private FileInputStream fis;
    private int current;
    private byte[] buff;
}
