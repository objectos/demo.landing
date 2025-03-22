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
package demo.landing;

import demo.landing.local.LocalModule;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.random.RandomGenerator;
import objectos.way.App;
import objectos.way.Http;
import objectos.way.Note;
import objectos.way.Sql;

@App.DoNotReload
public final class LandingDemoConfig {

  public static final class Builder {

    private Clock clock = Clock.systemDefaultZone();

    private byte[] codecKey;

    private Sql.Database database;

    private Note.Sink noteSink = Note.NoOpSink.create();

    private Instant reservationEpoch;

    private RandomGenerator reservationRandom;

    private LandingDemo.Stage stage = LandingDemo.Stage.DEFAULT;

    public final void clock(Clock value) {
      clock = Objects.requireNonNull(value, "value == null");
    }

    public final void codecKey(byte[] value) {
      final byte[] notNull;
      notNull = Objects.requireNonNull(value, "value == null");

      codecKey = notNull.clone();
    }

    public final void database(Sql.Database value) {
      database = Objects.requireNonNull(value, "value == null");
    }

    public final void noteSink(Note.Sink value) {
      noteSink = Objects.requireNonNull(value, "value == null");
    }

    public final void reservationEpoch(Instant value) {
      reservationEpoch = Objects.requireNonNull(value, "value == null");
    }

    public final void reservationRandom(RandomGenerator value) {
      reservationRandom = Objects.requireNonNull(value, "value == null");
    }

    public final void stage(LandingDemo.Stage value) {
      stage = Objects.requireNonNull(value, "value == null");
    }

    private LandingDemoConfig build() {
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

      return new LandingDemoConfig(this);
    }

  }

  public final Clock clock;

  private final byte[] codecKey;

  public final Sql.Database database;

  public final Note.Sink noteSink;

  public final Instant reservationEpoch;

  public final RandomGenerator reservationRandom;

  public final LandingDemo.Stage stage;

  private LandingDemoConfig(Builder builder) {
    clock = builder.clock;

    codecKey = builder.codecKey;

    database = builder.database;

    noteSink = builder.noteSink;

    reservationEpoch = builder.reservationEpoch;

    reservationRandom = builder.reservationRandom;

    stage = builder.stage;
  }

  public static LandingDemoConfig create(Consumer<Builder> config) {
    final Builder builder;
    builder = new Builder();

    // config implicit null-check
    config.accept(builder);

    return builder.build();
  }

  public final byte[] codecKey() {
    return codecKey.clone();
  }

  public final void local(Http.Routing routing) {
    routing.install(
        new LocalModule(this)
    );
  }

}