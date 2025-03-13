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

public class ConfirmTest extends AbstractTest {

  @Test
  public void testCase01() {
    final Http.TestingExchange http;
    http = Http.TestingExchange.create(config -> {
      config.set(Sql.Transaction.class, trx);

      config.method(Http.Method.GET);

      config.path("/index.html");

      config.queryParam("demo", Testing.encode(Kino.Page.CONFIRM, 901));
    });

    handle(http);

    assertEquals(http.responseStatus(), Http.Status.OK);

    assertEquals(
        writeResponseBody(http),

        """
        back-link: SEATS:901:999

        # Order #901

        title: Title 1
        date: Sat 25/Jan
        time: 13:00
        screen: Screen 1

        ## Order Details

        B1    | $9.99
        B2    | $9.99
        Total | $19.98
        action: CONFIRM:901
        """
    );
  }

  @Test
  public void testCase02() {
    final Http.TestingExchange http;
    http = Http.TestingExchange.create(config -> {
      config.set(Sql.Transaction.class, trx);

      config.method(Http.Method.POST);

      config.path("/demo/landing");

      config.queryParam("demo", Testing.encode(Kino.Page.CONFIRM, 901));
    });

    handle(http);

    assertEquals(http.responseStatus(), Http.Status.OK);

    assertEquals(
        writeResponseBody(http),

        """
        # Ticket #901

        Ammount Paid: $19.98
        Purchase Time: Sat 25/Jan 10:00

        ## Tickets

        Title 1  | Sat 25/Jan | 13:00  | Screen 1 | B1    | $9.99
        Title 1  | Sat 25/Jan | 13:00  | Screen 1 | B2    | $9.99
        """
    );
  }

  @Override
  protected final String testData() {
    return """
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

    insert into SHOW (SHOW_ID, SCREENING_ID, SHOWDATE, SHOWTIME, SEAT_PRICE)
    values (61, 41, '2025-01-25', '13:00:00', 9.99)
    ,      (62, 41, '2025-01-25', '17:00:00', 14.99)
    ,      (63, 41, '2025-01-25', '21:00:00', 19.99)
    ,      (64, 42, '2025-01-25', '14:00:00', 9.99)
    ,      (65, 42, '2025-01-25', '18:00:00', 14.99)
    ,      (66, 41, '2025-01-26', '13:00:00', 9.99);

    insert into SEAT (SEAT_ID, SCREEN_ID, SEAT_ROW, SEAT_COL, GRID_Y, GRID_X)
    values (101, 31, 'A', 1, 4, 4)
    ,      (102, 31, 'A', 2, 4, 5)
    ,      (103, 31, 'B', 1, 6, 2)
    ,      (104, 31, 'B', 2, 6, 3)
    ,      (105, 31, 'B', 3, 6, 6)
    ,      (106, 31, 'B', 4, 6, 7);

    insert into RESERVATION (RESERVATION_ID, SHOW_ID)
    values (901, 61)
    ,      (902, 61);

    insert into SELECTION (RESERVATION_ID, SEAT_ID, SHOW_ID)
    values (901, 103, 61)
    ,      (901, 104, 61);
    """;
  }

}