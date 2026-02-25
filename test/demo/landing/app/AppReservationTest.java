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

import static org.testng.Assert.assertEquals;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.testng.annotations.Test;

public class AppReservationTest {

  @Test
  public void testCase01() {
    final FixedClock clock;
    clock = new FixedClock(2025, 1, 13);

    final LocalDateTime dateTime;
    dateTime = LocalDateTime.of(2025, 1, 1, 0, 0);

    final ZoneOffset offset;
    offset = ZoneOffset.UTC;

    final Instant epoch;
    epoch = dateTime.toInstant(offset);

    final FixedGenerator generator;
    generator = new FixedGenerator(12345L);

    final AppReservation r;
    r = new AppReservation(clock, epoch, generator);

    test(r, "11111111110001101000010000000000000000011000000111001");

    clock.offset = Duration.ofMillis(1);

    test(r, "11111111110001101000010000000100000000011000000111001");

    clock.offset = Duration.ofMillis(2);

    test(r, "11111111110001101000010000001000000000011000000111001");

    clock.offset = Duration.ofMillis(3);

    test(r, "11111111110001101000010000001100000000011000000111001");
  }

  private void test(AppReservation r, String expected) {
    final long value;
    value = r.next();

    final String result;
    result = Long.toBinaryString(value);

    assertEquals(result, expected);
  }

}