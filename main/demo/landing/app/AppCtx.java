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

import static objectos.way.Http.Method.GET;
import static objectos.way.Http.Method.POST;

import demo.landing.LandingDemo;
import module java.base;
import module objectos.way;

/// Application entry point and system-wide context.
public final class AppCtx implements LandingDemo {

  public static final class Builder implements LandingDemo.Options {

    private Clock clock = Clock.systemDefaultZone();

    private byte[] codecKey;

    private Sql.Database database;

    private Note.Sink noteSink = Note.NoOpSink.create();

    private Instant reservationEpoch;

    private RandomGenerator reservationRandom;

    @Override
    public final void clock(Clock value) {
      clock = Objects.requireNonNull(value, "value == null");
    }

    @Override
    public final void codecKey(byte[] value) {
      final byte[] notNull;
      notNull = Objects.requireNonNull(value, "value == null");

      codecKey = notNull.clone();
    }

    @Override
    public final void database(Sql.Database value) {
      database = Objects.requireNonNull(value, "value == null");
    }

    @Override
    public final void noteSink(Note.Sink value) {
      noteSink = Objects.requireNonNull(value, "value == null");
    }

    @Override
    public final void reservationEpoch(Instant value) {
      reservationEpoch = Objects.requireNonNull(value, "value == null");
    }

    @Override
    public final void reservationRandom(RandomGenerator value) {
      reservationRandom = Objects.requireNonNull(value, "value == null");
    }

    final AppCtx build() {
      Objects.requireNonNull(codecKey, "codecKey == null");
      Objects.requireNonNull(database, "database == null");

      if (reservationEpoch == null) {
        final LocalDateTime dateTime;
        dateTime = LocalDateTime.of(2025, 1, 1, 0, 0);

        final ZoneOffset offset;
        offset = ZoneOffset.UTC;

        reservationEpoch = dateTime.toInstant(offset);
      }

      if (reservationRandom == null) {
        reservationRandom = new SecureRandom();
      }

      return new AppCtx(this);
    }

  }

  private static final Note.Ref1<Throwable> TRANSACTIONAL = Note.Ref1.create(AppCtx.class, "Transactional", Note.ERROR);

  private final Clock clock;

  private final byte[] codecKey;

  private final Sql.Database database;

  private final HexFormat hexFormat = HexFormat.of();

  private final Note.Sink noteSink;

  private final Instant reservationEpoch;

  private final RandomGenerator reservationRandom;

  private AppCtx(Builder builder) {
    clock = builder.clock;

    codecKey = builder.codecKey;

    database = builder.database;

    noteSink = builder.noteSink;

    reservationEpoch = builder.reservationEpoch;

    reservationRandom = builder.reservationRandom;
  }

  /// Creates a new instance with the specified configuration.
  public static AppCtx create(Consumer<? super Builder> opts) {
    final Builder builder;
    builder = new Builder();

    opts.accept(builder);

    return builder.build();
  }

  /// Creates a new instance of the demo's `Css.StyleSheet` configuration.
  ///
  /// @return a new instance of the demo's `Css.StyleSheet` configuration
  public static Css.Library styles() {
    return new KinoStyles();
  }

  // ##################################################################
  // # BEGIN: Routes
  // ##################################################################

  @Override
  public final Http.Routing.Module localRoutes() {
    return local -> {
      local.path("/demo.landing/clear-reservation", POST, trx(new LocalClear(this)));

      local.path("/demo.landing/create-show", POST, trx(new LocalCreate(this)));
    };
  }

  @Override
  public final Http.Routing.Module publicRoutes() {
    return www -> {
      www.path("/demo.landing/home", GET, trx(new Home(this)));
    };
  }

  private Http.Handler trx(Http.Handler handler) {
    return http -> {

      final Sql.Transaction trx;
      trx = database.beginTransaction(Sql.READ_COMMITED);

      try {
        trx.sql("set schema CINEMA");

        trx.update();

        http.set(Sql.Transaction.class, trx);

        handler.handle(http);

        trx.commit();
      } catch (Throwable t) {
        noteSink.send(TRANSACTIONAL, t);

        throw trx.rollbackAndWrap(t);
      } finally {
        trx.close();
      }

    };
  }

  // ##################################################################
  // # END: Routes
  // ##################################################################

  // ##################################################################
  // # BEGIN: UI
  // ##################################################################

  public static final Html.Id SHELL = Html.Id.of("demo.landing");

  public static final Http.HeaderName DEMO_LOCATION_HASH = Http.HeaderName.of("Demo-Location-Hash");

  public static final JsAction ONLOAD = Js.byId(SHELL).render("/demo.landing/home", opts -> {
    opts.header(DEMO_LOCATION_HASH.headerCase(), Js.window().location().href());
  });

  public final JsAction clickAction(AppView view, int id, AppReservation reservation) {
    final String href;
    href = href(view, id, reservation.id());

    final String hash;
    hash = encodeHash(view, id, reservation);

    return Js.byId(SHELL).render(href, opts -> {
      opts.history("/index.html#demo=" + hash + ";");
    });
  }

  private String href(AppView view, int id, long reservationId) {
    return "/demo.landing/" + view.slug + "/" + id + "?reservationId" + reservationId;
  }

  // ##################################################################
  // # END: UI
  // ##################################################################

  // ##################################################################
  // # BEGIN: History/Hash
  // ##################################################################

  /*
  
  random = 4 bytes
  
  view = 1 byte
  
  id = 4 byte
  
  rid = 8 bytes
  ------------------
  total = 17 bytes
  
  */

  public final String decodeHash(String hash) {
    if (hash == null) {
      return null;
    }

    final int length;
    length = hash.length();

    // '#' + 'demo' + '=' + (17 * 2) + ';'
    if (length != 41) {
      return null;
    }

    if (!hash.startsWith("#demo=")) {
      return null;
    }

    final char last;
    last = hash.charAt(41 - 1);

    if (last != ';') {
      return null;
    }

    final String value;
    value = hash.substring(6, 41 - 1);

    return decodeHash0(value);
  }

  private static final int BYTE_MASK = 0xFF;

  private static final int HASH_LENGTH = 17;

  private final AppView[] views = AppView.values();

  private String decodeHash0(String raw) {
    if (raw == null) {
      // a null value means a request with no URL fragment
      // => we should present the first view
      return href(AppView.HOME, 0, 0);
    }

    final byte[] bytes;

    try {
      bytes = hexFormat.parseHex(raw);
    } catch (IllegalArgumentException expected) {
      return href(AppView.NOT_FOUND, 0, 0);
    }

    if (bytes.length != HASH_LENGTH) {
      // wrong length
      return href(AppView.NOT_FOUND, 0, 0);
    }

    int index;
    index = 0;

    int random = 0;
    random |= (bytes[index++] & BYTE_MASK) << 24;
    random |= (bytes[index++] & BYTE_MASK) << 16;
    random |= (bytes[index++] & BYTE_MASK) << 8;
    random |= (bytes[index++] & BYTE_MASK) << 0;

    obfuscate(bytes, random);

    int viewOrdinal;
    viewOrdinal = bytes[index++] & BYTE_MASK;

    if (viewOrdinal < 0 || viewOrdinal >= views.length) {
      // invalid view ordinal
      return href(AppView.NOT_FOUND, 0, 0);
    }

    final AppView view;
    view = views[viewOrdinal];

    // next 4 bytes = id
    int id = 0;
    id |= (bytes[index++] & BYTE_MASK) << 24;
    id |= (bytes[index++] & BYTE_MASK) << 16;
    id |= (bytes[index++] & BYTE_MASK) << 8;
    id |= (bytes[index++] & BYTE_MASK) << 0;

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

    return href(view, id, rid);
  }

  private void obfuscate(byte[] bytes, int random) {
    final int offset;
    offset = random == Integer.MIN_VALUE ? Integer.MAX_VALUE : Math.abs(random);

    for (int idx = 4, len = bytes.length; idx < len; idx++) {
      byte b;
      b = bytes[idx];

      int keyIndex;
      keyIndex = (idx + offset) % codecKey.length;

      byte k;
      k = codecKey[keyIndex];

      bytes[idx] = (byte) (b ^ k);
    }
  }

  public final String encodeHash(AppView view, int id, AppReservation reservation) {
    final byte[] bytes;
    bytes = new byte[HASH_LENGTH];

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
    bytes[index++] = (byte) (view.ordinal() & BYTE_MASK);

    // next 4 bytes = id
    bytes[index++] = (byte) ((id >>> 24) & BYTE_MASK);
    bytes[index++] = (byte) ((id >>> 16) & BYTE_MASK);
    bytes[index++] = (byte) ((id >>> 8) & BYTE_MASK);
    bytes[index++] = (byte) ((id >>> 0) & BYTE_MASK);

    // next 8 bytes = rid (big endian)
    final long rid;
    rid = reservation.id();

    bytes[index++] = (byte) ((rid >>> 56) & BYTE_MASK);
    bytes[index++] = (byte) ((rid >>> 48) & BYTE_MASK);
    bytes[index++] = (byte) ((rid >>> 40) & BYTE_MASK);
    bytes[index++] = (byte) ((rid >>> 32) & BYTE_MASK);
    bytes[index++] = (byte) ((rid >>> 24) & BYTE_MASK);
    bytes[index++] = (byte) ((rid >>> 16) & BYTE_MASK);
    bytes[index++] = (byte) ((rid >>> 8) & BYTE_MASK);
    bytes[index++] = (byte) ((rid >>> 0) & BYTE_MASK);

    obfuscate(bytes, random);

    return hexFormat.formatHex(bytes);
  }

  // ##################################################################
  // # END: History/Hash
  // ##################################################################

  // ##################################################################
  // # BEGIN: Date/Time
  // ##################################################################

  public final LocalDateTime now() {
    return LocalDateTime.now(clock);
  }

  public final LocalDate today() {
    return LocalDate.now(clock);
  }

  // ##################################################################
  // # END: Date/Time
  // ##################################################################

  // ##################################################################
  // # BEGIN: Notes
  // ##################################################################

  public final void send(Note.Int1 note, int value) {
    noteSink.send(note, value);
  }

  // ##################################################################
  // # END: Notes
  // ##################################################################

}