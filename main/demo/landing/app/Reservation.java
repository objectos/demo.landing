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
import java.time.Duration;
import java.time.Instant;
import java.util.random.RandomGenerator;

/**
 * Generates a 64-bit Snowflake ID to uniquely identify an user making seat
 * reservations.
 */
final class Reservation {

  private static final long TIMESTAMP_BITS = 41;

  private static final long RANDOM_BITS = 64 - TIMESTAMP_BITS;

  private static final long MAX_RANDOM = (1L << RANDOM_BITS) - 1;

  private final Clock clock;

  // January 1st, 2025 @ 00:00 @ GMT-3
  private final Instant epoch;

  private final RandomGenerator randomGenerator;

  Reservation(Clock clock, Instant epoch, RandomGenerator randomGenerator) {
    this.clock = clock;

    this.epoch = epoch;

    this.randomGenerator = randomGenerator;
  }

  public final long next() {
    final Instant now;
    now = clock.instant();

    final Duration duration;
    duration = Duration.between(epoch, now);

    long epochTime;
    epochTime = duration.toMillis();

    final long timestamp;
    timestamp = epochTime << RANDOM_BITS;

    final long randomBits;
    randomBits = randomGenerator.nextLong(MAX_RANDOM);

    return timestamp | randomBits;
  }

}