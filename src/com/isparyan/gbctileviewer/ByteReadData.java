package com.isparyan.gbctileviewer;

/**
 * Created by YURIY on 3/13/2016.
 */
public class ByteReadData {

    final byte[] bytes;
    final int bytesRead;
    final long readLineStartPos;


    public ByteReadData() {
        this(new byte[0], 0, 0);
    }
    public ByteReadData(byte[] bytes, int bytesRead, long linePos) {
        this.bytes = bytes;
        this.bytesRead = bytesRead;
        this.readLineStartPos = linePos;
    }

}
