/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package j2me.rms;

import java.io.*;
import javax.microedition.rms.*;
import j2me.io.*;

// A wrapper class for a record store that allows the
// records to be accessed as a set of fields. The field
// definitions are maintained separately using a FieldList
// object, which can be stored as part of the record store
// or separately.

public class FieldBasedStore {

    // Some useful constants

    public static Boolean TRUE = new Boolean( true );
    public static Boolean FALSE = new Boolean( false );

    // Markers for the types of string we support

    private static final byte NULL_STRING_MARKER = 0;
    private static final byte UTF_STRING_MARKER = 1;

    // Constructs a field store where the field list is
    // assumed to be stored in the first record.

    public FieldBasedStore( RecordStore rs )
                            throws IOException,
                                   RecordStoreException {
        this( rs, 1 );
    }

    // Constructs a field store where the field list is
    // stored in the given record.

    public FieldBasedStore( RecordStore rs, int fieldListID )
                            throws IOException,
                                   RecordStoreException {
        this( rs, loadFieldList( rs, fieldListID ) );
    }

    // Constructs a field store with the given field list.

    public FieldBasedStore( RecordStore rs, FieldList list ){
        _rs = rs;
        _fieldList = list;
    }

    // Adds a new record to the store. Returns the new
    // record ID.

    public synchronized int addRecord( Object[] fields )
                                 throws IOException,
                                        RecordStoreException {
        writeStream( fields );
        byte[] data = _bout.getByteArray();
        return _rs.addRecord( data, 0, data.length );
    }

    // Returns the current field list.

    public FieldList getFieldList(){
        return _fieldList;
    }

    // Returns the record store.

    public RecordStore getRecordStore(){
        return _rs;
    }

    // Loads the field list from the record store.

    private static FieldList loadFieldList( RecordStore rs,
                                            int fieldListID )
                                 throws IOException,
                                        RecordStoreException {
        FieldList list = new FieldList();
        list.fromRecordStore( rs, fieldListID );
        return list;
    }

    // Prepares the store for input by making sure that
    // the data buffer is big enough. The streams are
    // reused.

    private void prepareForInput( int size ){
        if( _buffer == null || _buffer.length < size ){
            _buffer = new byte[ size ];
        }

        if( _bin == null ){
            _bin = new DirectByteArrayInputStream( _buffer );
            _din = new DataInputStream( _bin );
        } else {
            _bin.setByteArray( _buffer );
        }
    }

    // Prepares the store for output. The streams are reused.

    private void prepareForOutput(){
        if( _bout == null ){
            _bout = new DirectByteArrayOutputStream();
            _dout = new DataOutputStream( _bout );
        } else {
            _bout.reset();
        }
    }

    // Reads a field from the buffer.

    private Object readField( int type ) throws IOException {
        switch( type ){
            case FieldList.TYPE_BOOLEAN:
                return _din.readBoolean() ? TRUE : FALSE;
            case FieldList.TYPE_BYTE:
                return new Byte( _din.readByte() );
            case FieldList.TYPE_CHAR:
                return new Character( _din.readChar() );
            case FieldList.TYPE_SHORT:
                return new Short( _din.readShort() );
            case FieldList.TYPE_INT:
                return new Integer( _din.readInt() );
            case FieldList.TYPE_LONG:
                return new Long( _din.readLong() );
            case FieldList.TYPE_STRING: {
                byte marker = _din.readByte();
                if( marker == UTF_STRING_MARKER ){
                    return _din.readUTF();
                }
            }
        }

        return null;
    }

    // Reads the record at the given ID and returns it as
    // a set of objects that match the types in the
    // field list.

    public synchronized Object[] readRecord( int recordID )
                                 throws IOException,
                                        RecordStoreException {
        prepareForInput( _rs.getRecordSize( recordID ) );
        _rs.getRecord( recordID, _buffer, 0 );

        int count = _fieldList.getFieldCount();
        Object[] fields = new Object[ count ];

        for( int i = 0; i < count; ++i ){
            fields[i] = readField(_fieldList.getFieldType(i));
        }

        return fields;
    }

    // Converts an object to a boolean value.

    public static boolean toBoolean( Object value ){
        if( value instanceof Boolean ){
            return ((Boolean) value).booleanValue();
        } else if( value != null ){
            String str = value.toString().trim();

            if( str.equals( "true" ) ) return true;
            if( str.equals( "false" ) ) return false;

            return( toInt( value ) != 0 );
        }

        return false;
    }

    // Converts an object to a char.

    public static char toChar( Object value ){
        if( value instanceof Character ){
            return ((Character) value).charValue();
        } else if( value != null ){
            String s = value.toString();
            if( s.length() > 0 ){
                return s.charAt( 0 );
            }
        }

        return 0;
    }

    // Converts an object to an int. This code
    // would be much simpler if the CLDC supported
    // the java.lang.Number class.

    public static int toInt( Object value ){
        if( value instanceof Integer ){
            return ((Integer) value).intValue();
        } else if( value instanceof Boolean ){
            return ((Boolean) value).booleanValue() ? 1 : 0;
        } else if( value instanceof Byte ){
            return ((Byte) value).byteValue();
        } else if( value instanceof Character ){
            return ((Character) value).charValue();
        } else if( value instanceof Short ){
            return ((Short) value).shortValue();
        } else if( value instanceof Long ){
            return (int) ((Long) value).longValue();
        } else if( value != null ){
            try {
                return Integer.parseInt( value.toString() );
            }
            catch( NumberFormatException e ){
            }
        }

        return 0;
    }

    // Converts an object to a long. This code
    // would be much simpler if the CLDC supported
    // the java.lang.Number class.

    public static long toLong( Object value ){
        if( value instanceof Integer ){
            return ((Integer) value).longValue();
        } else if( value instanceof Boolean ){
            return ((Boolean) value).booleanValue() ? 1 : 0;
        } else if( value instanceof Byte ){
            return ((Byte) value).byteValue();
        } else if( value instanceof Character ){
            return ((Character) value).charValue();
        } else if( value instanceof Short ){
            return ((Short) value).shortValue();
        } else if( value instanceof Long ){
            return ((Long) value).longValue();
        } else if( value != null ){
            try {
                return Long.parseLong( value.toString() );
            }
            catch( NumberFormatException e ){
            }
        }

        return 0;
    }

    // Writes a field to the output buffer.

    private void writeField( int type, Object value )
                                 throws IOException {
        switch( type ){
            case FieldList.TYPE_BOOLEAN:
                _dout.writeBoolean( toBoolean( value ) );
                break;
            case FieldList.TYPE_BYTE:
                _dout.write( (byte) toInt( value ) );
                break;
            case FieldList.TYPE_CHAR:
                _dout.writeChar( toChar( value ) );
                break;
            case FieldList.TYPE_SHORT:
                _dout.writeShort( (short) toInt( value ) );
                break;
            case FieldList.TYPE_INT:
                _dout.writeInt( toInt( value ) );
                break;
            case FieldList.TYPE_LONG:
                _dout.writeLong( toLong( value ) );
                break;
            case FieldList.TYPE_STRING:
                if( value != null ){
                    String str = value.toString();
                    _dout.writeByte( UTF_STRING_MARKER );
                    _dout.writeUTF( str );
                } else {
                    _dout.writeByte( NULL_STRING_MARKER );
                }
                break;
        }
    }

    // Writes a set of fields to the given record. The
    // fields must be compatible with the types in
    // the field list.

    public synchronized void writeRecord( int recordID,
                                          Object[] fields )
                                 throws IOException,
                                        RecordStoreException {
        writeStream( fields );
        byte[] data = _bout.getByteArray();
        _rs.setRecord( recordID, data, 0, data.length );
    }

    // Writes a set of fields to the output stream.

    private void writeStream( Object[] fields )
                                       throws IOException {
        int count = _fieldList.getFieldCount();
        int len = ( fields != null ? fields.length : 0 );

        prepareForOutput();

        for( int i = 0; i < count; ++i ){
            writeField( _fieldList.getFieldType( i ),
                        ( i < len ? fields[i] : null ) );
        }
    }

    private DirectByteArrayInputStream  _bin;
    private DirectByteArrayOutputStream _bout;
    private byte[]                      _buffer;
    private DataInputStream             _din;
    private DataOutputStream            _dout;
    private FieldList                   _fieldList;
    private RecordStore                 _rs;
}
