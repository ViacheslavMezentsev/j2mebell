/**
    MIDP PNG Encoder for J2ME
    (c) 2007 Cody Konior

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along
    with this program; if not, write to the Free Software Foundation, Inc.,
    51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.

    ---

    Cody Konior
    codykonior at users dot sourceforge dot net
    http://mobilebio.sourceforge.net

    ---

    Based substantially on:
    Minimal PNG Encoder by Christian Froeschlin
    (c) 2006 Christian Froeschlin

    --

    Modifications by Cody Konior 2007-04-21

   - Added java.lang.System import
   - Including modified JZlib 1.0.7
   - Removed calcADLER32()
   - Removed toZLIB()
   - Removed "Must be < 64k" comment
   - Wrote new toZLIB()
   - Moved to new package

    Modifications by Cody Konior 2007-05-12

   - Added javadoc
   - Changed everything to package private so javadoc would only
     be generated for API relevant to encoding PNG

*/

package java.util.png;

import java.io.*;
import java.util.zip.*;

import javax.microedition.lcdui.Image;

import net.sf.microlog.core.*;


/**
 * MIDP PNG Encoder for J2ME
 *
 * @author  Cody Konior
 * @version 1.1, 2007-05-12
 *
 */
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
     * @return        an Image object containing PNG data
     *
     */
    public static Image toImage(int width, int height, byte[] alpha, byte[] red, byte[] green, byte[] blue) {

        try {

            byte[] png = toPNG(width, height, alpha, red, green, blue);

            return Image.createImage(png, 0, png.length);

        } catch ( Exception ex ) {

            //Логгер.error( "[Encoder.java]: " + ex.toString() );
            return null;
        }

    }


    /**
     * Returns an Image object from the suplied values.
     *
     * @param png     a byte array containing PNG data
     * @return        an Image object containing PNG data
     *
     */
    public static Image toImage(byte[] png) {

        try {

            return Image.createImage(png, 0, png.length);

        } catch ( Exception ex ) {

            //Логгер.error( "[Encoder.java]: " + ex.toString() );
            return null;
        }

    }


    /**
     * Returns a PNG stored in a byte array from the supplied values.
     *
     * @param width   the width of the image
     * @param height  the height of the image
     * @param alpha   the byte array of the alpha channel
     * @param red     the byte array of the red channel
     * @param green   the byte array of the green channel
     * @param blue    the byte array of the blue channel
     * @return        a byte array containing PNG data
     *
     */
    public static byte[] toPNG(int width, int height, byte[] alpha, byte[] red, byte[] green, byte[] blue) throws IOException {

        byte[] signature = new byte[] {(byte) 137, (byte) 80, (byte) 78, (byte) 71, (byte) 13, (byte) 10, (byte) 26, (byte) 10};

        //Логгер.info( "[Encoder.java]: createHeaderChunk()" );
        byte[] header = createHeaderChunk(width, height);

        //Логгер.info( "[Encoder.java]: createDataChunk()" );
        byte[] data = createDataChunk(width, height, alpha, red, green, blue);

        //Логгер.info( "[Encoder.java]: createTrailerChunk()" );
        byte[] trailer = createTrailerChunk();

        //Логгер.info( "[Encoder.java]: ByteArrayOutputStream()" );
        ByteArrayOutputStream png = new ByteArrayOutputStream(signature.length + header.length + data.length + trailer.length);

        //Логгер.info( "[Encoder.java]: png.write()" );
        png.write(signature);
        png.write(header);
        png.write(data);
        png.write(trailer);

        return png.toByteArray();

    }


    /**
     * Returns a PNG stored in a byte array from the supplied Image.
     *
     * @param image   an Image object
     * @return        a byte array containing PNG data
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

            //Логгер.error( "[Encoder.java]: " + ex.toString() );
            return null;

        } catch ( Exception ex ) {

            //Логгер.error( "[Encoder.java]: " + ex.toString() );
            return null;
        }

  }


    private static byte[] createHeaderChunk(int width, int height) throws IOException {

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


    private static byte[] createDataChunk(int width, int height, byte[] alpha, byte[] red, byte[] green, byte[] blue) throws IOException {

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


    private static byte[] createTrailerChunk() throws IOException {

        return toChunk("IEND", new byte[] {});
    }


    private static byte[] toChunk(String id, byte[] raw) throws IOException {

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


    // Creates a single zlib block contain a single
    // uncompressed deflate block.
    private static byte[] toZLIB(byte[] raw) throws IOException {

        int iStatus = 0;

        byte[] bBuffer  = new byte[raw.length];    // Maximum length of buffer
        ZStream jStream = new ZStream();

        iStatus = jStream.deflateInit(JZlib.Z_BEST_COMPRESSION);

        if (iStatus != JZlib.Z_OK) {

            throw (new IOException("Failure in deflateInit(JZlib.Z_BEST_COMPRESSION)"));
        }

        jStream.next_in = raw;
        jStream.next_in_index = 0;
        jStream.next_out = bBuffer;
        jStream.next_out_index = 0;

        while (jStream.total_in != raw.length && jStream.total_out < bBuffer.length) {

            // force small buffers
            jStream.avail_in = jStream.avail_out = 1;

            iStatus = jStream.deflate(JZlib.Z_NO_FLUSH);

            if (iStatus != JZlib.Z_OK) {

                throw (new IOException("Failure in deflate(JZlib.Z_NO_FLUSH)"));
            }

        }

        while ( true ) {

            jStream.avail_out = 1;

            iStatus = jStream.deflate(JZlib.Z_FINISH);

            if (iStatus == JZlib.Z_STREAM_END) {

                break;

            } else if (iStatus != JZlib.Z_OK) {

                throw (new IOException("Failure in deflate(JZlib.Z_FINISH)"));
            }

        }

        iStatus = jStream.deflateEnd();

        if (iStatus != JZlib.Z_OK) {

            throw (new IOException("Failure in deflateEnd()"));
        }

        byte[] bReturn = new byte[(int)(jStream.total_out)];

        System.arraycopy(bBuffer, 0, bReturn, 0, (int)(jStream.total_out));

        return bReturn;
    }

}
