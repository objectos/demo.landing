/*
 * Copyright (C) 2024-2026 Objectos Software LTDA.
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
import java.time.Duration;
import module java.base;
import module objectos.way;
import objectos.css.CssLibrary;

/// Application entry point and system-wide context.
public final class AppCtx implements LandingDemo {

  public static final class Builder implements LandingDemo.Options {

    private Clock clock;

    private byte[] codecKey;

    private Sql.Database database;

    private Note.Sink noteSink;

    private Instant reservationEpoch;

    private RandomGenerator reservationRandom;

    private boolean testing;

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

    @Override
    public final void testing() {
      testing = true;
    }

    final AppCtx build() {
      if (clock == null) {
        clock = Clock.systemDefaultZone();
      }

      Objects.requireNonNull(codecKey, "codecKey == null");

      Objects.requireNonNull(database, "database == null");

      if (noteSink == null) {
        noteSink = Note.NoOpSink.create();
      }

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

      return new AppCtx(
          clock,
          codecKey,
          database,
          noteSink,
          reservationEpoch,
          reservationRandom,
          testing
      );
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

  private final boolean testing;

  private AppCtx(
      Clock clock,
      byte[] codecKey,
      Sql.Database database,
      Note.Sink noteSink,
      Instant reservationEpoch,
      RandomGenerator reservationRandom,
      boolean testing) {
    this.clock = clock;

    this.codecKey = codecKey;

    this.database = database;

    this.noteSink = noteSink;

    this.reservationEpoch = reservationEpoch;

    this.reservationRandom = reservationRandom;

    this.testing = testing;
  }

  /// Creates a new instance with the specified configuration.
  public static AppCtx create(Consumer<? super Builder> opts) {
    final Builder builder;
    builder = new Builder();

    opts.accept(builder);

    return builder.build();
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
  public final Http.Routing.Module publicRoutes(Web.Resources webResources) {
    return www -> {
      www.path("/demo.landing/boot", GET, trx(new Boot(this)));

      www.path("/demo.landing/home", GET, trx(new Home(this)));

      www.path("/demo.landing/movie/{id}", GET, trx(new Movie(this)));

      www.path("/demo.landing/seats/{id}", path -> {
        path.allow(GET, trx(new Seats(this)));

        path.allow(POST, trx(new SeatsForm(this)));
      });

      www.path("/demo.landing/confirm", path -> {
        path.allow(GET, trx(new Confirm(this)));

        path.allow(POST, trx(new ConfirmForm(this)));
      });

      www.path("/demo.landing/ticket", GET, trx(new Ticket()));

      www.path("/demo.landing/poster{}", path -> path.handler(webResources));

      www.path("/demo.landing/{}", path -> path.handler(new NotFound(this)));
    };
  }

  private Http.Handler trx(Http.Handler handler) {
    return testing
        ? http -> handler.handle(http)
        : http -> {

          final Sql.Transaction trx;
          trx = database.beginTransaction(Sql.READ_COMMITED);

          try (trx) {
            trx.sql("set schema CINEMA");

            trx.update();

            http.set(Sql.Transaction.class, trx);

            handler.handle(http);

            trx.commit();
          } catch (Throwable t) {
            noteSink.send(TRANSACTIONAL, t);

            throw trx.rollbackAndWrap(t);
          }

        };
  }

  // ##################################################################
  // # END: Routes
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
      return href(AppView.HOME);
    }

    final int length;
    length = hash.length();

    // '#' + 'demo' + '=' + (17 * 2) + ';'
    if (length != 41) {
      return href(AppView.HOME);
    }

    if (!hash.startsWith("#demo=")) {
      return href(AppView.HOME);
    }

    final char last;
    last = hash.charAt(41 - 1);

    if (last != ';') {
      return href(AppView.HOME);
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
      return href(AppView.HOME);
    }

    final byte[] bytes;

    try {
      bytes = hexFormat.parseHex(raw);
    } catch (IllegalArgumentException expected) {
      return href(AppView.NOT_FOUND);
    }

    if (bytes.length != HASH_LENGTH) {
      // wrong length
      return href(AppView.NOT_FOUND);
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
      return href(AppView.NOT_FOUND);
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

  public final String encodeHash(AppView view, int id, long rid) {
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
  // # BEGIN: Reservation
  // ##################################################################

  private static final long TIMESTAMP_BITS = 41;

  private static final long RANDOM_BITS = 64 - TIMESTAMP_BITS;

  private static final long MAX_RANDOM = (1L << RANDOM_BITS) - 1;

  /// Generates a 64-bit Snowflake ID to uniquely identify an user making
  /// seat reservations.
  public final long nextReservation() {
    final Instant now;
    now = clock.instant();

    final Duration duration;
    duration = Duration.between(reservationEpoch, now);

    final long epochTime;
    epochTime = duration.toMillis();

    final long timestamp;
    timestamp = epochTime << RANDOM_BITS;

    final long randomBits;
    randomBits = reservationRandom.nextLong(MAX_RANDOM);

    return timestamp | randomBits;
  }

  // ##################################################################
  // # END: Reservation
  // ##################################################################

  // ##################################################################
  // # BEGIN: UI
  // ##################################################################

  public static final Html.Id SHELL = Html.Id.of("demo.landing");

  public static final Http.HeaderName DEMO_LOCATION_HASH = Http.HeaderName.of("Demo-Location-Hash");

  public static final JsAction ONLOAD = Js.byId(SHELL).render("/demo.landing/boot", opts -> {
    opts.header(DEMO_LOCATION_HASH.headerCase(), Js.window().location().hash());
  });

  /// The default 'submit' action.
  static final JsAction SUBMIT = Js.submit(opts -> {
    // disable history
    opts.history(false);

    // disable scroll
    opts.scroll(false);

    // update only the demo shell
    opts.update(AppCtx.SHELL);
  });

  @Override
  public final Html.Id shell() {
    return SHELL;
  }

  @Override
  public final JsAction loadAction() {
    return ONLOAD;
  }

  public final JsAction clickAction(AppView view, AppReservation reservation) {
    return clickAction(view, 0, reservation);
  }

  public final JsAction clickAction(AppView view, int id, AppReservation reservation) {
    return clickAction(view, id, reservation.id());
  }

  public final JsAction clickAction(AppView view, int id, long rid) {
    final String href;
    href = href(view, id, rid);

    final String hash;
    hash = encodeHash(view, id, rid);

    return Js.byId(SHELL).render(href, opts -> {
      opts.history("/index.html#demo=" + hash + ";");
    });
  }

  private String href(AppView view) {
    return href(view, 0, 0L);
  }

  public final String href(AppView view, AppReservation reservation) {
    return href(view, 0, reservation);
  }

  public final String href(AppView view, int id, AppReservation reservation) {
    return href(view, id, reservation.id());
  }

  private String href(AppView view, int id, long reservationId) {
    final StringBuilder href;
    href = new StringBuilder();

    href.append("/demo.landing/");

    href.append(view.slug);

    if (id > 0) {
      href.append('/');

      href.append(id);
    }

    if (reservationId != 0) {
      href.append("?reservationId=");

      href.append(reservationId);
    }

    return href.toString();
  }

  // ##################################################################
  // # END: UI
  // ##################################################################

  // ##################################################################
  // # BEGIN: CSS
  // ##################################################################

  public static CssLibrary stylesImpl() {
    return opts -> {
      opts.scanClasses(
          ConfirmView.class,
          HomeView.class,
          MovieView.class,
          NotFoundView.class,
          SeatsView.class,
          TicketView.class,
          UiIcon.class,
          UiShell.class
      );

      opts.theme("""
      :root {
        --font-sans: 'InterVariable', var(--default-font-sans);
        --font-mono: 'Hack', var(--default-font-mono);
        --color-body: var(--color-white);
        --color-border: var(--color-gray-200);
        --color-btn-ghost: var(--color-body);
        --color-btn-ghost-active: color-mix(in oklab, var(--color-btn-ghost) 85%, black 15%);
        --color-btn-ghost-hover: color-mix(in oklab, var(--color-btn-ghost) 90%, black 10%);
        --color-btn-ghost-text: var(--color-text);
        --color-btn-primary: var(--color-blue-600);
        --color-btn-primary-active: color-mix(in oklab, var(--color-btn-primary) 70%, black 30%);
        --color-btn-primary-hover: color-mix(in oklab, var(--color-btn-primary) 85%, black 15%);
        --color-btn-primary-text: var(--color-gray-50);
        --color-focus: var(--color-blue-600);
        --color-footer: var(--color-gray-700);
        --color-footer-text: var(--color-gray-100);
        --color-high-comment: var(--color-gray-500);
        --color-high-keyword: var(--color-blue-700);
        --color-high-literal: var(--color-red-600);
        --color-high-meta: var(--color-yellow-600);
        --color-high-string: var(--color-green-700);
        --color-html: var(--color-gray-50);
        --color-icon: var(--color-gray-800);
        --color-layer: var(--color-stone-100);
        --color-link: var(--color-blue-600);
        --color-link-hover: color-mix(in oklab, var(--color-link) 85%, black 15%);
        --color-logo: var(--color-gray-800);
        --color-logo-hover: var(--color-link);
        --color-text: var(--color-gray-800);
        --color-text-secondary: var(--color-gray-600);
      }
      """);

      opts.theme("""
      :root { @media (prefers-color-scheme: dark) {
        --color-body: var(--color-neutral-800);
        --color-border: var(--color-neutral-600);
        --color-btn-ghost-active: color-mix(in oklab, var(--color-btn-ghost) 85%, white 15%);
        --color-btn-ghost-hover: color-mix(in oklab, var(--color-btn-ghost) 90%, white 10%);
        --color-focus: var(--color-white);
        --color-high-comment: var(--color-fuchsia-400);
        --color-high-keyword: var(--color-blue-400);
        --color-high-literal: var(--color-red-400);
        --color-high-meta: var(--color-pink-400);
        --color-high-string: var(--color-green-300);
        --color-icon: var(--color-gray-200);
        --color-layer: var(--color-stone-900);
        --color-link: var(--color-blue-400);
        --color-link-hover: color-mix(in oklab, var(--color-link) 85%, white 15%);
        --color-logo: var(--color-neutral-100);
        --color-text: var(--color-neutral-100);
        --color-text-secondary: var(--color-neutral-300);
      }}
      """);
    };
  }

  // ##################################################################
  // # END: CSS
  // ##################################################################

  // ##################################################################
  // # BEGIN: Web Resources
  // ##################################################################

  private record Poster(int id, byte[] contents) implements Media.Bytes {
    Poster(ResultSet rs, int idx) throws SQLException {
      this(
          rs.getInt(idx++),
          rs.getBytes(idx++)
      );
    }

    public final String path() {
      return "/demo.landing/poster" + id + ".jpg";
    }

    @Override
    public final String contentType() {
      return "image/jpeg";
    }

    @Override
    public final byte[] toByteArray() {
      return contents;
    }
  }

  @Override
  public final Web.Resources.Library webResources() {
    return opts -> {
      try (Sql.Transaction trx = database.connect()) {
        trx.sql("set schema CINEMA");

        trx.update();

        trx.sql("""
          select
            MOVIE_ID,
            DATA
          from
            MOVIE_POSTER
          """);

        final List<Poster> posters;
        posters = trx.query(Poster::new);

        trx.commit();

        for (Poster poster : posters) {
          opts.addMedia(poster.path(), poster);
        }
      }
    };
  }

  // ##################################################################
  // # END: Web Resources
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

  public final <T1> void send(Note.Ref1<T1> note, T1 value) {
    noteSink.send(note, value);
  }

  // ##################################################################
  // # END: Notes
  // ##################################################################

  // ##################################################################
  // # BEGIN: Testing support
  // ##################################################################

  final AppCtx with(Clock clock, Instant registrationEpoch, RandomGenerator registrationRandom) {
    return new AppCtx(clock, codecKey, database, noteSink, registrationEpoch, registrationRandom, testing);
  }

  // ##################################################################
  // # END: Testing support
  // ##################################################################

}