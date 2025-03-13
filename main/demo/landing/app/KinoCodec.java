/*
 * Copyright (C) 2024-2025 Objectos Software LTDA.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package demo.landing.app;

import java.time.Clock;
import java.util.HexFormat;
import java.util.Objects;

final class KinoCodec {

  private static final int BYTE_MASK = 0xFF;

  private static final int LENGTH = 17;

  private final Kino.Query badRequest = Kino.Page.BAD_REQUEST.query();

  private final Clock clock;

  private final HexFormat hexFormat = HexFormat.of();

  private final byte[] key;

  private final Kino.Page[] views = Kino.Page.values();

  KinoCodec(Clock clock, byte[] key) {
    this.clock = Objects.requireNonNull(clock, "clock == null");

    if (key.length < LENGTH) {
      throw new IllegalArgumentException("Key should have at least " + LENGTH + " bytes");
    }

    this.key = key;
  }

  public static KinoCodec create(Clock clock, byte[] key) {
    return new KinoCodec(clock, key);
  }

  /*
  
   to simplify we assume the ID is always a long
  
   random = 4 bytes
  
   page = 1 byte
  
   id = 8 bytes
  
   aux = 4 byte
   ------------------
   total = 17 bytes
  
   */

  public final Kino.Query decode(String raw) {
    if (raw == null) {
      // a null value means a request with no query parameters
      // => we should present the first view
      return Kino.Page.NOW_SHOWING.query();
    }

    final byte[] bytes;

    try {
      bytes = hexFormat.parseHex(raw);
    } catch (IllegalArgumentException expected) {
      return badRequest;
    }

    if (bytes.length != LENGTH) {
      // wrong length
      return badRequest;
    }

    int index;
    index = 0;

    int random = 0;
    random |= bytes[index++] & BYTE_MASK << 24;
    random |= bytes[index++] & BYTE_MASK << 16;
    random |= bytes[index++] & BYTE_MASK << 8;
    random |= bytes[index++] & BYTE_MASK << 0;

    obfuscate(bytes, random);

    int pageOrdinal;
    pageOrdinal = bytes[index++] & BYTE_MASK;

    if (pageOrdinal < 0 || pageOrdinal >= views.length) {
      return badRequest;
    }

    Kino.Page page;
    page = views[pageOrdinal];

    // next 8 bytes = id (big endian)
    long id = 0L;
    id |= (long) (bytes[index++] & BYTE_MASK) << 56;
    id |= (long) (bytes[index++] & BYTE_MASK) << 48;
    id |= (long) (bytes[index++] & BYTE_MASK) << 40;
    id |= (long) (bytes[index++] & BYTE_MASK) << 32;
    id |= (long) (bytes[index++] & BYTE_MASK) << 24;
    id |= (long) (bytes[index++] & BYTE_MASK) << 16;
    id |= (long) (bytes[index++] & BYTE_MASK) << 8;
    id |= (long) (bytes[index++] & BYTE_MASK) << 0;

    // next 4 byte = aux
    int aux = 0;
    aux |= (bytes[index++] & BYTE_MASK) << 24;
    aux |= (bytes[index++] & BYTE_MASK) << 16;
    aux |= (bytes[index++] & BYTE_MASK) << 8;
    aux |= (bytes[index++] & BYTE_MASK) << 0;

    return page.query(id, aux);
  }

  public final String encode(Kino.Query query) {
    Objects.requireNonNull(query, "query == null");

    final byte[] bytes;
    bytes = new byte[LENGTH];

    int index;
    index = 0;

    final long millis;
    millis = clock.millis();

    final int random;
    random = (int) millis ^ (int) (millis >>> 32);

    bytes[index++] = (byte) ((random >>> 24) & BYTE_MASK);
    bytes[index++] = (byte) ((random >>> 16) & BYTE_MASK);
    bytes[index++] = (byte) ((random >>> 8) & BYTE_MASK);
    bytes[index++] = (byte) ((random >>> 0) & BYTE_MASK);

    // first byte = view
    final Kino.Page view;
    view = query.page();

    bytes[index++] = (byte) (view.ordinal() & BYTE_MASK);

    // next 8 bytes = id (big endian)

    final long id;
    id = query.id();

    bytes[index++] = (byte) ((id >>> 56) & BYTE_MASK);
    bytes[index++] = (byte) ((id >>> 48) & BYTE_MASK);
    bytes[index++] = (byte) ((id >>> 40) & BYTE_MASK);
    bytes[index++] = (byte) ((id >>> 32) & BYTE_MASK);
    bytes[index++] = (byte) ((id >>> 24) & BYTE_MASK);
    bytes[index++] = (byte) ((id >>> 16) & BYTE_MASK);
    bytes[index++] = (byte) ((id >>> 8) & BYTE_MASK);
    bytes[index++] = (byte) (id & BYTE_MASK);

    // next 4 bytes = aux
    int aux;
    aux = query.aux();

    bytes[index++] = (byte) ((aux >>> 24) & BYTE_MASK);
    bytes[index++] = (byte) ((aux >>> 16) & BYTE_MASK);
    bytes[index++] = (byte) ((aux >>> 8) & BYTE_MASK);
    bytes[index++] = (byte) ((aux >>> 0) & BYTE_MASK);

    obfuscate(bytes, random);

    return hexFormat.formatHex(bytes);
  }

  private void obfuscate(byte[] bytes, int random) {
    final int offset;
    offset = random == Integer.MIN_VALUE ? Integer.MAX_VALUE : Math.abs(random);

    for (int idx = 4, len = bytes.length; idx < len; idx++) {
      byte b;
      b = bytes[idx];

      int keyIndex;
      keyIndex = (idx + offset) % key.length;

      byte k;
      k = key[keyIndex];

      bytes[idx] = (byte) (b ^ k);
    }
  }

}
