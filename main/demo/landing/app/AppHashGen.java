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

import module java.base;

final class AppHashGen {

  private static final int BYTE_MASK = 0xFF;

  private static final int LENGTH = 17;

  private final Clock clock;

  private final HexFormat hexFormat = HexFormat.of();

  private final byte[] key;

  private final AppView[] views = AppView.values();

  AppHashGen(Clock clock, byte[] key) {
    this.clock = Objects.requireNonNull(clock, "clock == null");

    if (key.length < LENGTH) {
      throw new IllegalArgumentException("Key should have at least " + LENGTH + " bytes");
    }

    this.key = key;
  }

  public static AppHashGen create(Clock clock, byte[] key) {
    return new AppHashGen(clock, key);
  }

  /*
  
   random = 4 bytes
  
   view = 1 byte
  
   rid = 8 bytes
  
   id = 4 byte
   ------------------
   total = 17 bytes
  
   */

  public final AppHash decode(String raw) {
    if (raw == null) {
      // a null value means a request with no URL fragment
      // => we should present the first view
      return AppHash.of(AppView.HOME);
    }

    final byte[] bytes;

    try {
      bytes = hexFormat.parseHex(raw);
    } catch (IllegalArgumentException expected) {
      return AppHash.of(AppView.NOT_FOUND);
    }

    if (bytes.length != LENGTH) {
      // wrong length
      return AppHash.of(AppView.NOT_FOUND);
    }

    int index;
    index = 0;

    int random = 0;
    random |= (bytes[index++] & BYTE_MASK) << 24;
    random |= (bytes[index++] & BYTE_MASK) << 16;
    random |= (bytes[index++] & BYTE_MASK) << 8;
    random |= (bytes[index++] & BYTE_MASK) << 0;

    obfuscate(bytes, random);

    int pageOrdinal;
    pageOrdinal = bytes[index++] & BYTE_MASK;

    if (pageOrdinal < 0 || pageOrdinal >= views.length) {
      return AppHash.of(AppView.NOT_FOUND);
    }

    final AppView view;
    view = views[pageOrdinal];

    // next 8 bytes = rid (big endian)
    long rid = 0L;
    rid |= (long) (bytes[index++] & BYTE_MASK) << 56;
    rid |= (long) (bytes[index++] & BYTE_MASK) << 48;
    rid |= (long) (bytes[index++] & BYTE_MASK) << 40;
    rid |= (long) (bytes[index++] & BYTE_MASK) << 32;
    rid |= (long) (bytes[index++] & BYTE_MASK) << 24;
    rid |= (long) (bytes[index++] & BYTE_MASK) << 16;
    rid |= (long) (bytes[index++] & BYTE_MASK) << 8;
    rid |= (long) (bytes[index++] & BYTE_MASK) << 0;

    // next 4 byte = id
    int id = 0;
    id |= (bytes[index++] & BYTE_MASK) << 24;
    id |= (bytes[index++] & BYTE_MASK) << 16;
    id |= (bytes[index++] & BYTE_MASK) << 8;
    id |= (bytes[index++] & BYTE_MASK) << 0;

    return AppHash.of(view, rid, id);
  }

  public final String encode(AppHash query) {
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
    final AppView view;
    view = query.view();

    bytes[index++] = (byte) (view.ordinal() & BYTE_MASK);

    // next 8 bytes = rid (big endian)

    final AppReservation reservation;
    reservation = query.reservation();

    final long rid;
    rid = reservation.id();

    bytes[index++] = (byte) ((rid >>> 56) & BYTE_MASK);
    bytes[index++] = (byte) ((rid >>> 48) & BYTE_MASK);
    bytes[index++] = (byte) ((rid >>> 40) & BYTE_MASK);
    bytes[index++] = (byte) ((rid >>> 32) & BYTE_MASK);
    bytes[index++] = (byte) ((rid >>> 24) & BYTE_MASK);
    bytes[index++] = (byte) ((rid >>> 16) & BYTE_MASK);
    bytes[index++] = (byte) ((rid >>> 8) & BYTE_MASK);
    bytes[index++] = (byte) (rid & BYTE_MASK);

    // next 4 bytes = id
    int id;
    id = query.id();

    bytes[index++] = (byte) ((id >>> 24) & BYTE_MASK);
    bytes[index++] = (byte) ((id >>> 16) & BYTE_MASK);
    bytes[index++] = (byte) ((id >>> 8) & BYTE_MASK);
    bytes[index++] = (byte) ((id >>> 0) & BYTE_MASK);

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
