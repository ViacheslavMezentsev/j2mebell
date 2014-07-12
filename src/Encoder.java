/*
 * Minimal PNG Encoder to create png streams (and MIDP images) from RGBA arrays.
 *
 * Copyright 2006-2009 Christian Fr�schlin
 *
 * www.chrfr.de
 *
 *
 * Changelog:
 *
 * 09/22/08: Fixed Adler checksum calculation and byte order
 *           for storing length of zlib deflate block. Thanks
 *           to Miloslav Ruzicka for noting this.
 *
 * 05/12/09: Split Encoder and ZLIB functionality into separate classes.
 *           Added support for images > 64K by splitting the data into
 *           multiple uncompressed deflate blocks.
 *
 * Terms of Use:
 *
 * You may use the Encoder encoder free of charge for any purpose you desire, as long
 * as you do not claim credit for the original sources and agree not to hold me
 * responsible for any damage arising out of its use.
 *
 * If you have a suitable location in GUI or documentation for giving credit,
 * I'd appreciate a mention of
 *
 *  Encoder (C) 2006-2009 by Christian Fr�schlin, www.chrfr.de
 *
 * but that's not mandatory.
 *
 */

// <editor-fold defaultstate="collapsed" desc=" Подключаемые модули ">

import java.io.*;
import java.util.zip.*;

import javax.microedition.lcdui.Image;

// </editor-fold>


public class Encoder {

    /**
     * Returns an Image object from the supplied values.
     *
     * @param width   the width of the image
     * @param height  the height of the image
     * @param alpha   the byte array of the alpha channel
     * @param red     the byte array of the red channel
     * @param green   the byte array of the green channel
     * @param blue    the byte array of the blue channel
     * @return        an Image object containing Encoder data
     *
     */
    public static Image toImage( int width, int height, byte[] alpha, byte[] red, byte[] green, byte[] blue ) {

        try {

            byte[] png = toPNG( width, height, alpha, red, green, blue );

            return Image.createImage(png, 0, png.length);

        } catch ( IOException ex ) {

            main.Логгер.error( "[Encoder.java]: " + ex.toString() );
            return null;

        } catch ( Exception ex ) {

            main.Логгер.error( "[Encoder.java]: " + ex.toString() );
            return null;
        }

    }


    /**
     * Returns an Image object from the suplied values.
     *
     * @param png     a byte array containing Encoder data
     * @return        an Image object containing Encoder data
     *
     */
    public static Image toImage( byte[] png ) {

        try {

            return Image.createImage( png, 0, png.length );

        } catch ( Exception exception ) {

            main.Логгер.error( "[Encoder.java]: " + exception.toString() );
            return null;
        }

    }


    /**
     * Returns a Encoder stored in a byte array from the supplied values.
     *
     * @param width   the width of the image
     * @param height  the height of the image
     * @param alpha   the byte array of the alpha channel
     * @param red     the byte array of the red channel
     * @param green   the byte array of the green channel
     * @param blue    the byte array of the blue channel
     * @return        a byte array containing Encoder data
     *
     */
    public static byte[] toPNG(int width, int height, byte[] alpha, byte[] red, byte[] green, byte[] blue) throws IOException {

        byte[] signature = new byte[] {(byte) 137, (byte) 80, (byte) 78, (byte) 71, (byte) 13, (byte) 10, (byte) 26, (byte) 10};

        byte[] header = createHeaderChunk(width, height);
        byte[] data = createDataChunk(width, height, alpha, red, green, blue);
        byte[] trailer = createTrailerChunk();

        ByteArrayOutputStream png = new ByteArrayOutputStream(signature.length + header.length + data.length + trailer.length);

        png.write(signature);
        png.write(header);
        png.write(data);
        png.write(trailer);

        return png.toByteArray();
    }


    /**
     * Returns a Encoder stored in a byte array from the supplied Image.
     *
     * @param image   an Image object
     * @return        a byte array containing Encoder data
     *
     */
    public static byte[] toPNG( Image image ) {

        try {

            //Логгер.info( "[Encoder.java]: toPNG( Image image )" );

            int imageSize = image.getWidth() * image.getHeight();

            //Логгер.info( "[Encoder.java]: imageSize = " + imageSize );

            int[] rgbs = new int[ imageSize ];
            byte[] a, r, g, b;
            int colorToDecode;

            image.getRGB(rgbs, 0, image.getWidth() , 0, 0, image.getWidth(), image.getHeight());

            a = new byte[ imageSize ];
            r = new byte[ imageSize ];
            g = new byte[ imageSize ];
            b = new byte[ imageSize ];

            for (int i = 0; i < imageSize; i++) {
                colorToDecode = rgbs[i];

                a[i] = (byte) ((colorToDecode & 0xFF000000) >>> 24);
                r[i] = (byte) ((colorToDecode & 0x00FF0000) >>> 16);
                g[i] = (byte) ((colorToDecode & 0x0000FF00) >>> 8);
                b[i] = (byte) ((colorToDecode & 0x000000FF));
            }

            return toPNG(image.getWidth(), image.getHeight(), a, r, g, b);

        } catch ( IOException ex ) {

            main.Логгер.error( "[Encoder.java]: " + ex.toString() );
            return null;

        } catch ( Exception ex ) {

            main.Логгер.error( "[Encoder.java]: " + ex.toString() );
            return null;
        }

    }


    public static byte[] createHeaderChunk( int width, int height ) throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream(13);
        DataOutputStream chunk = new DataOutputStream(baos);

        chunk.writeInt(width);
        chunk.writeInt(height);

        chunk.writeByte(8); // Bitdepth
        chunk.writeByte(6); // Colortype ARGB
        chunk.writeByte(0); // Compression
        chunk.writeByte(0); // Filter
        chunk.writeByte(0); // Interlace

        return toChunk("IHDR", baos.toByteArray());

    }


    public static byte[] createDataChunk( int width, int height, byte[] alpha, byte[] red, byte[] green, byte[] blue ) throws IOException {

        int source = 0;
        int dest = 0;

        byte[] raw = new byte[4*(width*height) + height];

        for (int y = 0; y < height; y++) {

            raw[dest++] = 0; // No filter

            for (int x = 0; x < width; x++) {

                raw[dest++] = red[source];
                raw[dest++] = green[source];
                raw[dest++] = blue[source];
                raw[dest++] = alpha[source++];

            }

        }

        return toChunk("IDAT", toZLIB(raw));

    }


    public static byte[] createTrailerChunk() throws IOException {

        return toChunk( "IEND", new byte[] {} );
    }


    public static byte[] toChunk( String id, byte[] raw ) throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream(raw.length + 12);
        DataOutputStream chunk = new DataOutputStream(baos);

        chunk.writeInt(raw.length);

        byte[] bid = new byte[4];

        for (int i = 0; i < 4; i++) {

            bid[i] = (byte) id.charAt(i);
        }

        chunk.write(bid);

        chunk.write(raw);

        CRC32 crc = new CRC32();

        crc.reset();
        crc.update( bid );
        crc.update( raw );

        chunk.writeInt( ( int ) crc.getValue() );

        return baos.toByteArray();

    }


    /* This method is called to encode the image data as a zlib
       block as required by the Encoder specification. This file comes
       with a minimal ZLIB encoder which uses uncompressed deflate
       blocks (fast, short, easy, but no compression). If you want
       compression, call another encoder (such as JZLib?) here. */
    public static byte[] toZLIB(byte[] raw) throws IOException {

        return ZLIB.toZLIB( raw );
    }

}


class ZLIB {

    static final int BLOCK_SIZE = 32000;


    public static byte[] toZLIB(byte[] raw) throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream( raw.length + 6 + ( raw.length / BLOCK_SIZE ) * 5 );
        DataOutputStream zlib = new DataOutputStream(baos);

        byte tmp = ( byte ) 8;

        // CM = 8, CMINFO = 0
        zlib.writeByte( tmp );

        // FCHECK (FDICT/FLEVEL=0)
        zlib.writeByte( ( 31 - ( ( tmp << 8 ) % 31 ) ) % 31 );

        int pos = 0;

        while (raw.length - pos > BLOCK_SIZE) {

            writeUncompressedDeflateBlock( zlib, false, raw, pos, ( char ) BLOCK_SIZE );
            pos += BLOCK_SIZE;
        }

        writeUncompressedDeflateBlock( zlib, true, raw, pos, ( char ) ( raw.length - pos ) );

        // zlib check sum of uncompressed data
        zlib.writeInt( calcADLER32( raw ) );

        return baos.toByteArray();
    }


    private static void writeUncompressedDeflateBlock( DataOutputStream zlib, boolean last,
        byte[] raw, int off, char len ) throws IOException {

        // Final flag, Compression type 0
        zlib.writeByte( ( byte ) ( last ? 1 : 0 ) );

        // Length LSB
        zlib.writeByte( ( byte ) ( len & 0xFF ) );

        // Length MSB
        zlib.writeByte( ( byte ) ( ( len & 0xFF00 ) >> 8 ) );

        // Length 1st complement LSB
        zlib.writeByte( ( byte ) ( ~len & 0xFF ) );

        // Length 1st complement MSB
        zlib.writeByte( ( byte ) ( ( ~len & 0xFF00 ) >> 8 ) );

        // Data
        zlib.write( raw, off, len );
    }


    private static int calcADLER32( byte[] raw ) {

        int s1 = 1;
        int s2 = 0;

        for ( int i = 0; i < raw.length; i++ ) {

            int abs = raw[i] >=0 ? raw[i] : ( raw[i] + 256 );
            s1 = ( s1 + abs ) % 65521;
            s2 = ( s2 + s1 ) % 65521;
        }

        return ( s2 << 16 ) + s1;
    }

}
