/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package j2me.io;

import java.io.*;

public class ExtendedDataOutputStream extends DataOutputStream {
    public ExtendedDataOutputStream( OutputStream out ){
        super( out );
    }

    public final void writeIntArray( int[] arr )
                                     throws IOException {
        int size = arr.length;
        writeInt( size );
        for( int i = 0; i < size; ++i ){
            writeInt( arr[i] );
        }
    }
}
