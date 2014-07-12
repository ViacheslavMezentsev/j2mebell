/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package j2me.io;

import java.io.*;

// A version of ByteArrayInputStream that lets you
// replace the underlying byte array.

public class DirectByteArrayInputStream
                  extends ByteArrayInputStream {

    // Constructs an output stream from the given array

    public DirectByteArrayInputStream( byte buf[] ){
        super( buf );
    }

    // Constructs an output stream from the given subarray

    public DirectByteArrayInputStream( byte buf[],
                                       int offset,
                                       int length ){
        super( buf, offset, length );
    }

    // Resets the array the stream reads from

    public synchronized void setByteArray( byte[] buf ){
        this.buf = buf;
        this.pos = 0;
        this.count = buf.length;
        this.mark = 0;
    }

    // Resets the array the stream reads from

    public synchronized void setByteArray( byte[] buf,
                                           int offset,
                                           int length ){
        this.buf = buf;
        this.pos = offset;
        this.count = Math.min( offset + length, buf.length );
        this.mark = offset;
    }
}
