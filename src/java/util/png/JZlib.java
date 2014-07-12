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
    JZLib 1.0.7, originally copyright follows.

    ---

    Modifications by Cody Konior 2007-04-21

    - Moved to new package

   Modifications by Cody Konior 2007-05-12

   - Added javadoc
   - Added empty constructor
   - Changed everything to package private so javadoc would only
     be generated for API relevant to encoding PNG

 */

/* -*-mode:java; c-basic-offset:2; -*- */
/*
Copyright (c) 2000,2001,2002,2003 ymnk, JCraft,Inc. All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

  1. Redistributions of source code must retain the above copyright notice,
     this list of conditions and the following disclaimer.

  2. Redistributions in binary form must reproduce the above copyright
     notice, this list of conditions and the following disclaimer in
     the documentation and/or other materials provided with the distribution.

  3. The names of the authors may not be used to endorse or promote products
     derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL JCRAFT,
INC. OR ANY CONTRIBUTORS TO THIS SOFTWARE BE LIABLE FOR ANY DIRECT, INDIRECT,
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
/*
 * This program is based on zlib-1.1.3, so all credit should go authors
 * Jean-loup Gailly(jloup@gzip.org) and Mark Adler(madler@alumni.caltech.edu)
 * and contributors of zlib.
 */

package java.util.png;

final public class JZlib{
  private static final String version = "1.0.2";

  /**
   * Returns a string that identifies which version of JZlib (the compression
   * library used by mobilebio_png) is in use to compress images to PNG.
   *
   * @return    a version string in the form of "1.0.2"
   *
   */
  public static String version() {
    return version;
  }

  protected JZlib() {
  }

  // compression levels
  static final int Z_NO_COMPRESSION=0;
  static final int Z_BEST_SPEED=1;
  static final int Z_BEST_COMPRESSION=9;
  static final int Z_DEFAULT_COMPRESSION=(-1);

  // compression strategy
  static final int Z_FILTERED=1;
  static final int Z_HUFFMAN_ONLY=2;
  static final int Z_DEFAULT_STRATEGY=0;

  static final int Z_NO_FLUSH=0;
  static final int Z_PARTIAL_FLUSH=1;
  static final int Z_SYNC_FLUSH=2;
  static final int Z_FULL_FLUSH=3;
  static final int Z_FINISH=4;

  static final int Z_OK=0;
  static final int Z_STREAM_END=1;
  static final int Z_NEED_DICT=2;
  static final int Z_ERRNO=-1;
  static final int Z_STREAM_ERROR=-2;
  static final int Z_DATA_ERROR=-3;
  static final int Z_MEM_ERROR=-4;
  static final int Z_BUF_ERROR=-5;
  static final int Z_VERSION_ERROR=-6;
}
