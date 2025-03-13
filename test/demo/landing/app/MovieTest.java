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

import demo.landing.AbstractTest;
import objectos.way.Http;
import objectos.way.Sql;
import org.testng.annotations.Test;

public class MovieTest extends AbstractTest {

  @Test
  public void testCase01() {
    final Http.TestingExchange http;
    http = Http.TestingExchange.create(config -> {
      config.set(Sql.Transaction.class, trx);

      config.method(Http.Method.GET);

      config.path("/index.html");

      config.queryParam("demo", Testing.encode(Kino.Page.MOVIE, 11));
    });

    handle(http);

    assertEquals(http.responseStatus(), Http.Status.OK);

    assertEquals(
        writeResponseBody(http),

        """
        back-link: NOW_SHOWING

        # Title 1

        synopsys: Synopsys 1
        runtime: 2h 11m
        release-date: Jan 10, 2025
        genres: Adventure, Drama

        # Showtimes

        ## Sat 25/Jan

        screen: Screen 1
        features: Feature B, Feature A

        Query[page=SEATS, id=61, aux=0]  | 13:00
        Query[page=SEATS, id=62, aux=0]  | 17:00
        Query[page=SEATS, id=63, aux=0]  | 21:00

        ## Sat 25/Jan

        screen: Screen 2
        features: Feature A

        Query[page=SEATS, id=64, aux=0]  | 14:00
        Query[page=SEATS, id=65, aux=0]  | 18:00

        ## Sun 26/Jan

        screen: Screen 1
        features: Feature B, Feature A

        Query[page=SEATS, id=66, aux=0]  | 13:00
        """
    );
  }

  @Test
  public void testCase02() {
    final Http.TestingExchange http;
    http = Http.TestingExchange.create(config -> {
      config.set(Sql.Transaction.class, trx);

      config.method(Http.Method.GET);

      config.path("/index.html");

      config.queryParam("demo", "movie");
      config.queryParam("movie", Integer.MAX_VALUE);
    });

    handle(http);

    assertEquals(http.responseStatus(), Http.Status.OK);

    assertEquals(
        writeResponseBody(http),

        """
        # Something Went Wrong

        """
    );
  }

  @Override
  protected final String testData() {
    return """
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
  }

}