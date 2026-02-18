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

import objectos.way.Http;
import objectos.way.Sql;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(Testing.class)
public class SeatsTestConcurrent {

  @BeforeClass
  public void beforeClass() {
    Testing.commit("""
    insert into MOVIE (MOVIE_ID, TITLE, SYNOPSYS, RUNTIME, RELEASE_DATE)
    values (1011, 'Title 1', 'Synopsys 1', 131, '2025-01-10')
    ,      (1012, 'Title 2', 'Synopsys 2', 150, '2025-01-20');

    insert into SCREEN (SCREEN_ID, NAME, SEATING_CAPACITY)
    values (1031, 'Screen 1', 40)
    ,      (1032, 'Screen 2', 30);

    insert into SCREENING (SCREENING_ID, MOVIE_ID, SCREEN_ID)
    values (1041, 1011, 1031)
    ,      (1042, 1011, 1032)
    ,      (1043, 1012, 1032);

    insert into SHOW (SHOW_ID, SCREENING_ID, SHOWDATE, SHOWTIME, SEAT_PRICE)
    values (1061, 1041, '2025-01-25', '13:00:00', 9.99)
    ,      (1062, 1041, '2025-01-25', '17:00:00', 14.99)
    ,      (1063, 1041, '2025-01-25', '21:00:00', 19.99)
    ,      (1064, 1042, '2025-01-25', '14:00:00', 9.99)
    ,      (1065, 1042, '2025-01-25', '18:00:00', 14.99)
    ,      (1066, 1041, '2025-01-26', '13:00:00', 9.99);

    insert into SEAT (SEAT_ID, SCREEN_ID, SEAT_ROW, SEAT_COL, GRID_Y, GRID_X)
    values (10101, 1031, 'A', 1, 4, 4)
    ,      (10102, 1031, 'A', 2, 4, 5)
    ,      (10103, 1031, 'B', 1, 6, 2)
    ,      (10104, 1031, 'B', 2, 6, 3)
    ,      (10105, 1031, 'B', 3, 6, 6)
    ,      (10106, 1031, 'B', 4, 6, 7);
    """);
  }

  @Test(priority = 1000)
  public void testCase01() {
    // two concurrent reservations

    // 901 selected 103 and 104
    Testing.commit("""
    insert into RESERVATION (RESERVATION_ID, SHOW_ID)
    values (10901, 1061);

    insert into SELECTION (RESERVATION_ID, SEAT_ID, SHOW_ID)
    values (10901, 10103, 1061)
    ,      (10901, 10104, 1061);
    """);

    Testing.rollback(trx -> {
      // 902 tries to select the same seats
      final Http.Exchange http0;
      http0 = Testing.http(config -> {
        trx.sql(Sql.SCRIPT, """
        insert into RESERVATION (RESERVATION_ID, SHOW_ID)
        values (10902, 1061)
        """);

        trx.batchUpdate();

        config.set(Sql.Transaction.class, trx);

        config.method(Http.Method.POST);

        config.path("/demo/landing");

        config.header(Http.HeaderName.WAY_REQUEST, "true");

        final long reservationId;
        reservationId = 10902;

        final int screenId;
        screenId = 1031;

        final String demo;
        demo = Testing.encode(Page.SEATS, reservationId, screenId);

        config.queryParam("demo", demo);

        config.formParam("seat", 10103);
      });

      assertEquals(
          Testing.handle0(http0),

          """
          HTTP/1.1 302 Found
          Date: Mon, 28 Apr 2025 13:01:00 GMT
          Content-Length: 0
          Location: /index.html?page=S&demo=7f9e0b7b9e2a4f461b1e3b5a0e

          """
      );

      final Http.Exchange http1;
      http1 = Testing.http(config -> {
        config.set(Sql.Transaction.class, trx);

        config.method(Http.Method.GET);

        config.path("/index.html");

        config.queryParam("page", Page.SEATS.key);

        config.queryParam("demo", "7f9e0b7b9e2a4f461b1e3b5a0e");
      });

      assertEquals(
          Testing.handle0(http1),

          """
          HTTP/1.1 200 OK
          Date: Mon, 28 Apr 2025 13:01:00 GMT
          Content-Type: text/html; charset=utf-8
          Transfer-Encoding: chunked

          back-link: /index.html?page=M&id=1011

          # Show details

          title: Title 1
          alert: BOOKED
          date: Sat 25/Jan
          time: 13:00
          screen: Screen 1

          # Seats

          action: SEATS:10902:1031
          """
      );
    });

  }

}