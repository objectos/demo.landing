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

import java.time.LocalDateTime;
import java.util.List;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(Testing.class)
public class MovieScreeningTest {

  private final String data = """
  insert into MOVIE (MOVIE_ID, TITLE, SYNOPSYS, RUNTIME, RELEASE_DATE)
  values (11, 'Title 1', 'Synopsys 1', 131, '2025-01-10')
  ,      (12, 'Title 2', 'Synopsys 2', 150, '2025-01-20');

  insert into GENRE (GENRE_ID, NAME)
  values (21, 'Action')
  ,      (22, 'Adventure')
  ,      (23, 'Comedy')
  ,      (24, 'Drama');

  insert into MOVIE_GENRE (MOVIE_ID, GENRE_ID)
  values (11, 24) /* Drama */
  ,      (11, 22) /* Adventure */
  ,      (12, 23) /* Comedy */;

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

      final LocalDateTime jan25;
      jan25 = LocalDateTime.of(2025, 1, 25, 10, 0);

      final List<MovieScreening> all;
      all = MovieScreening.query(trx, 11, jan25);

      assertEquals(all.size(), 3);

      final MovieScreening s0;
      s0 = all.get(0);

      assertEquals(s0.screenId(), 31);
      assertEquals(s0.screenName(), "Screen 1");
      assertEquals(s0.features(), "Feature B, Feature A");
      assertEquals(s0.date(), "Sat 25/Jan");
      assertEquals(
          s0.showtimes(),

          List.of(
              new MovieShowtime(61, "13:00"),
              new MovieShowtime(62, "17:00"),
              new MovieShowtime(63, "21:00")
          )
      );

      final MovieScreening s1;
      s1 = all.get(1);

      assertEquals(s1.screenId(), 32);
      assertEquals(s1.screenName(), "Screen 2");
      assertEquals(s1.features(), "Feature A");
      assertEquals(s1.date(), "Sat 25/Jan");
      assertEquals(
          s1.showtimes(),

          List.of(
              new MovieShowtime(64, "14:00"),
              new MovieShowtime(65, "18:00")
          )
      );

      final MovieScreening s2;
      s2 = all.get(2);

      assertEquals(s2.screenId(), 31);
      assertEquals(s2.screenName(), "Screen 1");
      assertEquals(s2.features(), "Feature B, Feature A");
      assertEquals(s2.date(), "Sun 26/Jan");
      assertEquals(
          s2.showtimes(),

          List.of(
              new MovieShowtime(66, "13:00")
          )
      );
    });
  }

  @Test
  public void testCase02() {
    Testing.rollback(trx -> {
      Testing.load(trx, data);

      final LocalDateTime jan25;
      jan25 = LocalDateTime.of(2025, 1, 25, 13, 0);

      final List<MovieScreening> all;
      all = MovieScreening.query(trx, 11, jan25);

      assertEquals(all.size(), 3);

      final MovieScreening s0;
      s0 = all.get(0);

      assertEquals(s0.screenId(), 31);
      assertEquals(s0.screenName(), "Screen 1");
      assertEquals(s0.features(), "Feature B, Feature A");
      assertEquals(s0.date(), "Sat 25/Jan");
      assertEquals(
          s0.showtimes(),

          List.of(
              new MovieShowtime(62, "17:00"),
              new MovieShowtime(63, "21:00")
          )
      );

      final MovieScreening s1;
      s1 = all.get(1);

      assertEquals(s1.screenId(), 32);
      assertEquals(s1.screenName(), "Screen 2");
      assertEquals(s1.features(), "Feature A");
      assertEquals(s1.date(), "Sat 25/Jan");
      assertEquals(
          s1.showtimes(),

          List.of(
              new MovieShowtime(64, "14:00"),
              new MovieShowtime(65, "18:00")
          )
      );

      final MovieScreening s2;
      s2 = all.get(2);

      assertEquals(s2.screenId(), 31);
      assertEquals(s2.screenName(), "Screen 1");
      assertEquals(s2.features(), "Feature B, Feature A");
      assertEquals(s2.date(), "Sun 26/Jan");
      assertEquals(
          s2.showtimes(),

          List.of(
              new MovieShowtime(66, "13:00")
          )
      );
    });
  }

  @Test
  public void testCase03() {
    Testing.rollback(trx -> {
      Testing.load(trx, data);

      final LocalDateTime jan25;
      jan25 = LocalDateTime.of(2025, 1, 25, 20, 0);

      final List<MovieScreening> all;
      all = MovieScreening.query(trx, 11, jan25);

      assertEquals(all.size(), 2);

      final MovieScreening s0;
      s0 = all.get(0);

      assertEquals(s0.screenId(), 31);
      assertEquals(s0.screenName(), "Screen 1");
      assertEquals(s0.features(), "Feature B, Feature A");
      assertEquals(s0.date(), "Sat 25/Jan");
      assertEquals(
          s0.showtimes(),

          List.of(
              new MovieShowtime(63, "21:00")
          )
      );

      final MovieScreening s2;
      s2 = all.get(1);

      assertEquals(s2.screenId(), 31);
      assertEquals(s2.screenName(), "Screen 1");
      assertEquals(s2.features(), "Feature B, Feature A");
      assertEquals(s2.date(), "Sun 26/Jan");
      assertEquals(
          s2.showtimes(),

          List.of(
              new MovieShowtime(66, "13:00")
          )
      );
    });
  }

}