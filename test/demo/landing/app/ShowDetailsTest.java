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

import java.util.Optional;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(Testing.class)
public class ShowDetailsTest {

  private final String data = """
  insert into MOVIE (MOVIE_ID, TITLE, SYNOPSYS, RUNTIME, RELEASE_DATE)
  values (11, 'Title 1', 'Synopsys 1', 131, '2025-01-10')
  ,      (12, 'Title 2', 'Synopsys 2', 150, '2025-01-20');

  insert into SCREEN (SCREEN_ID, NAME, SEATING_CAPACITY)
  values (31, 'Screen 1', 40)
  ,      (32, 'Screen 2', 30);

  insert into SCREENING (SCREENING_ID, MOVIE_ID, SCREEN_ID)
  values (41, 11, 31)
  ,      (42, 11, 32)
  ,      (43, 12, 32);

  insert into FEATURE (FEATURE_ID, NAME)
  values (51, 'Feature B')
  ,      (52, 'Feature A');

  insert into SCREENING_FEATURE (SCREENING_ID, FEATURE_ID)
  values (41, 51)
  ,      (41, 52)
  ,      (42, 52)
  ,      (43, 51);

  insert into SHOW (SHOW_ID, SCREENING_ID, SHOWDATE, SHOWTIME, SEAT_PRICE)
  values (61, 41, '2025-01-25', '13:00:00', 9.99)
  ,      (62, 41, '2025-01-25', '17:00:00', 14.99)
  ,      (63, 41, '2025-01-25', '21:00:00', 19.99)
  ,      (64, 42, '2025-01-25', '14:00:00', 9.99)
  ,      (65, 42, '2025-01-25', '18:00:00', 14.99)
  ,      (66, 41, '2025-01-26', '13:00:00', 9.99);
  """;

  @Test
  public void testCase01() {
    Testing.rollback(trx -> {
      Testing.load(trx, data);

      final Optional<ShowDetails> maybe;
      maybe = ShowDetails.queryOptional(trx, 61);

      assertEquals(maybe.isPresent(), true);

      final ShowDetails show;
      show = maybe.get();

      assertEquals(show.showId(), 61);
      assertEquals(show.date(), "Sat 25/Jan");
      assertEquals(show.time(), "13:00");
      assertEquals(show.screenId(), 31);
      assertEquals(show.screen(), "Screen 1");
      assertEquals(show.capacity(), 40);
      assertEquals(show.movieId(), 11);
      assertEquals(show.title(), "Title 1");
    });
  }

  @Test
  public void testCase02() {
    Testing.rollback(trx -> {
      Testing.load(trx, data);

      Testing.load(trx, """
      insert into RESERVATION (RESERVATION_ID, SHOW_ID)
      values (904, 64);
      """);

      final Optional<ShowDetails> maybe;
      maybe = ShowDetails.queryBackButton(trx, 904L);

      assertEquals(maybe.isPresent(), true);

      final ShowDetails show;
      show = maybe.get();

      assertEquals(show.showId(), 64);
      assertEquals(show.date(), "Sat 25/Jan");
      assertEquals(show.time(), "14:00");
      assertEquals(show.screenId(), 32);
      assertEquals(show.screen(), "Screen 2");
      assertEquals(show.capacity(), 30);
      assertEquals(show.movieId(), 11);
      assertEquals(show.title(), "Title 1");
    });
  }

}