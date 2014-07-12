/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package j2me.io;

import java.io.*;

// A version of ByteArrayOutputStream that gives you
// direct access to the underlying byte array if
// you need it.

public class DirectByteArrayOutputStream
                       extends ByteArrayOutputStream {

    // Constructs a byte array output stream of default size

    public DirectByteArrayOutputStream(){
        super();
    }

    // Constructs a byte array output stream of given size

    public DirectByteArrayOutputStream( int size ){
        super( size );
    }

    // Returns a reference to the underlying byte array.
    // The actual amount of data in the byte array is
    // obtained via the size method.

    public synchronized byte[] getByteArray(){
        return buf;
    }

    // Swaps in a new byte array for the old one, resetting
    // the count as well.

    public synchronized byte[] swap( byte[] newBuf ){
        byte[] oldBuf = buf;
        buf = newBuf;
        reset();
        return oldBuf;
    }
}
