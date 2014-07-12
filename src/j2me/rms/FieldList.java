/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package j2me.rms;

import java.io.*;
import javax.microedition.rms.*;

// Maintains information about the fields in a
// field-based record store. Currently just a list of
// field types and (optional) field names, but could
// easily be expanded to store other information.

public class FieldList {

    private static final int VERSION = 1;

    // The basic field types.

    public static final byte TYPE_BOOLEAN = 1;
    public static final byte TYPE_BYTE = 2;
    public static final byte TYPE_CHAR = 3;
    public static final byte TYPE_SHORT = 4;
    public static final byte TYPE_INT = 5;
    public static final byte TYPE_LONG = 6;
    public static final byte TYPE_STRING = 7;

    // Constructs an empty list.

    public FieldList(){
    }

    // Constructs a list of the given size.

    public FieldList( int numFields ){
        if( numFields < 0 || numFields > 255 ){
            throw new IllegalArgumentException(
                       "Bad number of fields" );
        }

        _types = new byte[ numFields ];
        _names = new String[ numFields ];
    }

    // Returns the number of fields.

    public int getFieldCount(){
        return _types != null ? _types.length : 0;
    }

    // Returns the name of a field.

    public String getFieldName( int index ){
        String name = _names[ index ];
        return name != null ? name : "";
    }

    // Returns the type of a field.

    public byte getFieldType( int index ){
        return _types[ index ];
    }

    // Reads the field list from a byte array.

    public void fromByteArray( byte[] data )
                               throws IOException {
        ByteArrayInputStream bin =
                  new ByteArrayInputStream( data );
        fromDataStream( new DataInputStream( bin ) );
        bin.close();
    }

    // Reads the fields list from a data stream.

    public void fromDataStream( DataInputStream din )
                                throws IOException {
        int version = din.readUnsignedByte();
        if( version != VERSION ){
            throw new IOException( "Incorrect version " +
                  version + " for FieldList, expected " +
                  VERSION );
        }

        int numFields = din.readUnsignedByte();

        _types = new byte[ numFields ];
        _names = new String[ numFields ];

        if( numFields > 0 ){
            din.readFully( _types );

            for( int i = 0; i < numFields; ++i ){
                _names[i] = din.readUTF();
            }
        }
    }

    // Reads a field list from a record store.

    public void fromRecordStore( RecordStore rs, int index )
                                 throws IOException,
                                        RecordStoreException {
        fromByteArray( rs.getRecord( index ) );
    }

    // Sets the name of a field.

    public void setFieldName( int index, String name ){
        _names[ index ] = name;
    }

    // Sets the type of a field.

    public void setFieldType( int index, byte type ){
        _types[ index ] = type;
    }

    // Stores the fields list to a byte array

    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream bout =
                     new ByteArrayOutputStream();
        toDataStream( new DataOutputStream( bout ) );
        byte[] data = bout.toByteArray();
        bout.close();
        return data;
    }

    // Stores the fields list to a data stream

    public void toDataStream( DataOutputStream out )
                              throws IOException {
        out.writeByte( VERSION );

        int count = getFieldCount();

        out.writeByte( count );

        if( count > 0 ){
            out.write( _types, 0, count );

            for( int i = 0; i < count; ++i ){
                out.writeUTF( getFieldName( i ) );
            }
        }
    }

    // Writes a field list to a record store.

    public int toRecordStore( RecordStore rs, int index )
                               throws IOException,
                                      RecordStoreException {
        byte[]  data = toByteArray();
        boolean add = true;

        if( index > 0 ){
            try {
                rs.setRecord( index, data, 0, data.length );
                add = false;
            }
            catch( InvalidRecordIDException e ){
            }
        }

        // If the record doesn't actually exist yet,
        // go ahead and create it by inserting dummy
        // records ahead of it

        if( add ){
            synchronized( rs ){
                int nextID = rs.getNextRecordID();
                if( index <= 0 ) index = nextID;

                while( nextID < index ){
                    rs.addRecord( null, 0, 0 );
                }

                if( nextID == index ){
                    rs.addRecord( data, 0, data.length );
                }
            }
        }

        return index;
    }

    private String[] _names;
    private byte[]   _types;
}
