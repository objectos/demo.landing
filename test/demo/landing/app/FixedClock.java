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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public final class FixedClock extends Clock {

  private final Instant startTime;

  private final ZoneId zone;

  Duration offset = Duration.ZERO;

  public FixedClock(int year, int month, int day) {
    LocalDateTime dateTime;
    dateTime = LocalDateTime.of(year, month, day, 10, 0);

    startTime = dateTime.toInstant(ZoneOffset.UTC);

    zone = ZoneOffset.UTC;
  }

  public FixedClock(long millis) {
    startTime = Instant.ofEpochMilli(millis);

    zone = ZoneOffset.UTC;
  }

  public FixedClock(ZonedDateTime epoch) {
    startTime = epoch.toInstant();

    zone = epoch.getZone();
  }

  @Override
  public final Instant instant() {
    return startTime.plus(offset);
  }

  @Override
  public final ZoneId getZone() {
    return zone;
  }

  @Override
  public Clock withZone(ZoneId zone) {
    throw new UnsupportedOperationException();
  }

}