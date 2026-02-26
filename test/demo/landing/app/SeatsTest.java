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
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(Testing.class)
public class SeatsTest {

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
  ,      (106, 31, 'B', 4, 6, 7)
  ,      (107, 31, 'C', 1, 7, 4)
  ,      (108, 31, 'C', 2, 7, 5);
  """;

  @Test
  public void testCase01() {
    Testing.rollback(trx -> {
      Testing.load(trx, data);

      Testing.load(trx, """
      insert into RESERVATION (RESERVATION_ID, SHOW_ID)
      values (901, 61);
      """);

      final Http.Exchange http;
      http = Testing.http(config -> {
        config.set(Sql.Transaction.class, trx);

        config.method(Http.Method.POST);

        config.path("/demo.landing/seats");

        config.header(Http.HeaderName.WAY_REQUEST, "true");

        config.formParam("reservationId", 901);

        config.formParam("screenId", 31);

        config.formParam("seat", 103);
      });

      assertEquals(
          Testing.handle0(http),

          """
          HTTP/1.1 200 OK
          Date: Mon, 28 Apr 2025 13:01:00 GMT
          Content-Type: text/html; charset=utf-8
          Transfer-Encoding: chunked

          back-link: /demo.landing/seats/0?reservationId=901

          # Order #901

          title: Title 1
          date: Sat 25/Jan
          time: 13:00
          screen: Screen 1

          ## Order Details

          B1    | $9.99
          Total | $9.99
          reservationId: 901
          """
      );
    });
  }

  @Test
  public void testCase03() {
    Testing.rollback(trx -> {
      Testing.load(trx, data);

      Testing.load(trx, """
      insert into RESERVATION (RESERVATION_ID, SHOW_ID)
      values (901, 61);

      insert into SELECTION (RESERVATION_ID, SEAT_ID, SHOW_ID)
      values (901, 103, 61);
      """);

      final Http.Exchange http;
      http = Testing.http(config -> {
        config.set(Sql.Transaction.class, trx);

        config.method(Http.Method.POST);

        config.path("/demo.landing/seats");

        config.header(Http.HeaderName.WAY_REQUEST, "true");

        config.formParam("reservationId", 901);

        config.formParam("screenId", 31);

        config.formParam("seat", 103);

        config.formParam("seat", 104);

        config.formParam("seat", 105);
      });

      assertEquals(
          Testing.handle0(http),

          """
          HTTP/1.1 200 OK
          Date: Mon, 28 Apr 2025 13:01:00 GMT
          Content-Type: text/html; charset=utf-8
          Transfer-Encoding: chunked

          back-link: /demo.landing/seats/0?reservationId=901

          # Order #901

          title: Title 1
          date: Sat 25/Jan
          time: 13:00
          screen: Screen 1

          ## Order Details

          B1    | $9.99
          B2    | $9.99
          B3    | $9.99
          Total | $29.97
          reservationId: 901
          """
      );
    });
  }

  @Test
  public void testCase04() {
    Testing.rollback(trx -> {
      Testing.load(trx, data);

      Testing.load(trx, """
      insert into RESERVATION (RESERVATION_ID, SHOW_ID)
      values (901, 61);

      insert into SELECTION (RESERVATION_ID, SEAT_ID, SHOW_ID)
      values (901, 103, 61)
      ,      (901, 104, 61)
      ,      (901, 105, 61);
      """);

      final Http.Exchange http;
      http = Testing.http(config -> {
        config.set(Sql.Transaction.class, trx);

        config.method(Http.Method.POST);

        config.path("/demo.landing/seats");

        config.header(Http.HeaderName.WAY_REQUEST, "true");

        config.formParam("reservationId", 901);

        config.formParam("screenId", 31);

        config.formParam("seat", 103);

        config.formParam("seat", 104);
      });

      assertEquals(
          Testing.handle0(http),

          """
          HTTP/1.1 200 OK
          Date: Mon, 28 Apr 2025 13:01:00 GMT
          Content-Type: text/html; charset=utf-8
          Transfer-Encoding: chunked

          back-link: /demo.landing/seats/0?reservationId=901

          # Order #901

          title: Title 1
          date: Sat 25/Jan
          time: 13:00
          screen: Screen 1

          ## Order Details

          B1    | $9.99
          B2    | $9.99
          Total | $19.98
          reservationId: 901
          """
      );
    });
  }

  @Test
  public void testCase05() {
    Testing.rollback(trx -> {
      Testing.load(trx, data);

      Testing.load(trx, """
      insert into RESERVATION (RESERVATION_ID, SHOW_ID)
      values (901, 61);

      insert into SELECTION (RESERVATION_ID, SEAT_ID, SHOW_ID)
      values (901, 103, 61)
      ,      (901, 104, 61);
      """);

      final Http.Exchange http;
      http = Testing.http(config -> {
        config.set(Sql.Transaction.class, trx);

        config.method(Http.Method.POST);

        config.path("/demo.landing/seats");

        config.header(Http.HeaderName.WAY_REQUEST, "true");

        config.formParam("reservationId", 901);

        config.formParam("screenId", 31);

        config.formParam("seat", 103);

        config.formParam("seat", 104);
      });

      assertEquals(
          Testing.handle0(http),

          """
          HTTP/1.1 200 OK
          Date: Mon, 28 Apr 2025 13:01:00 GMT
          Content-Type: text/html; charset=utf-8
          Transfer-Encoding: chunked

          back-link: /demo.landing/seats/0?reservationId=901

          # Order #901

          title: Title 1
          date: Sat 25/Jan
          time: 13:00
          screen: Screen 1

          ## Order Details

          B1    | $9.99
          B2    | $9.99
          Total | $19.98
          reservationId: 901
          """
      );
    });
  }

  @Test(description = "POST: bad reservation id")
  public void testCase07() {
    Testing.rollback(trx -> {
      Testing.load(trx, data);

      Testing.load(trx, """
      insert into RESERVATION (RESERVATION_ID, SHOW_ID)
      values (901, 61);
      """);

      final Http.Exchange http;
      http = Testing.http(config -> {
        config.set(Sql.Transaction.class, trx);

        config.method(Http.Method.POST);

        config.path("/demo.landing/seats");

        config.header(Http.HeaderName.WAY_REQUEST, "true");

        config.formParam("reservationId", 902); // bad reservation id

        config.formParam("screenId", 31); // valid screen id

        config.formParam("seat", 103);

        config.formParam("seat", 104);
      });

      assertEquals(
          Testing.handle0(http),

          """
          HTTP/1.1 400 Bad Request
          Date: Mon, 28 Apr 2025 13:01:00 GMT
          Content-Type: text/html; charset=utf-8
          Transfer-Encoding: chunked

          # Something Went Wrong

          """
      );
    });
  }

  @Test(description = "POST: bad seat id")
  public void testCase08() {
    Testing.rollback(trx -> {
      Testing.load(trx, data);

      Testing.load(trx, """
      insert into RESERVATION (RESERVATION_ID, SHOW_ID)
      values (901, 61);
      """);

      final Http.Exchange http;
      http = Testing.http(config -> {
        config.set(Sql.Transaction.class, trx);

        config.method(Http.Method.POST);

        config.path("/demo.landing/seats");

        config.header(Http.HeaderName.WAY_REQUEST, "true");

        config.formParam("reservationId", 901); // valid reservation id

        config.formParam("screenId", 31); // valid screen id

        config.formParam("seat", 999); // bad seat id
      });

      assertEquals(
          Testing.handle0(http),

          """
          HTTP/1.1 400 Bad Request
          Date: Mon, 28 Apr 2025 13:01:00 GMT
          Content-Type: text/html; charset=utf-8
          Transfer-Encoding: chunked

          # Something Went Wrong

          """
      );
    });
  }

  @Test(description = "POST: bad reservation id (refers to sold ticket)")
  public void testCase09() {
    Testing.rollback(trx -> {
      Testing.load(trx, data);

      Testing.load(trx, """
      insert into RESERVATION (RESERVATION_ID, SHOW_ID, TICKET_TIME)
      values (901, 61, '2025-01-25 11:00');

      insert into SELECTION (RESERVATION_ID, SEAT_ID, SHOW_ID)
      values (901, 103, 61)
      ,      (901, 104, 61);
      """);

      final Http.Exchange http;
      http = Testing.http(config -> {
        config.set(Sql.Transaction.class, trx);

        config.method(Http.Method.POST);

        config.path("/demo.landing/seats");

        config.header(Http.HeaderName.WAY_REQUEST, "true");

        config.formParam("reservationId", 901);

        config.formParam("screenId", 31);

        config.formParam("seat", 105);
      });

      assertEquals(
          Testing.handle0(http),

          """
          HTTP/1.1 400 Bad Request
          Date: Mon, 28 Apr 2025 13:01:00 GMT
          Content-Type: text/html; charset=utf-8
          Transfer-Encoding: chunked

          # Something Went Wrong

          """
      );
    });
  }

  @Test
  public void testCase10() {
    Testing.rollback(trx -> {
      Testing.load(trx, data);

      Testing.load(trx, """
      insert into RESERVATION (RESERVATION_ID, SHOW_ID)
      values (901, 61);
      """);

      final Http.Exchange http;
      http = Testing.http(config -> {
        config.set(Sql.Transaction.class, trx);

        config.method(Http.Method.POST);

        config.path("/demo.landing/seats");

        config.header(Http.HeaderName.WAY_REQUEST, "true");

        config.formParam("reservationId", 901);

        config.formParam("screenId", 31);

        config.formParam("seat", 101);
        config.formParam("seat", 102);
        config.formParam("seat", 103);
        config.formParam("seat", 104);
        config.formParam("seat", 105);
        config.formParam("seat", 106);
      });

      assertEquals(
          Testing.handle0(http),

          """
          HTTP/1.1 200 OK
          Date: Mon, 28 Apr 2025 13:01:00 GMT
          Content-Type: text/html; charset=utf-8
          Transfer-Encoding: chunked

          back-link: /demo.landing/seats/0?reservationId=901

          # Order #901

          title: Title 1
          date: Sat 25/Jan
          time: 13:00
          screen: Screen 1

          ## Order Details

          A1    | $9.99
          A2    | $9.99
          B1    | $9.99
          B2    | $9.99
          B3    | $9.99
          B4    | $9.99
          Total | $59.94
          reservationId: 901
          """
      );
    });
  }

  @Test(description = "Too many seats")
  public void testCase11() {
    Testing.rollback(trx -> {
      Testing.load(trx, data);

      Testing.load(trx, """
      insert into RESERVATION (RESERVATION_ID, SHOW_ID)
      values (901, 61);
      """);

      final Http.Exchange http;
      http = Testing.http(config -> {
        config.set(Sql.Transaction.class, trx);

        config.method(Http.Method.POST);

        config.path("/demo.landing/seats");

        config.header(Http.HeaderName.WAY_REQUEST, "true");

        config.formParam("reservationId", 901);

        config.formParam("screenId", 31);

        // more than 6 seats
        config.formParam("seat", 101);
        config.formParam("seat", 102);
        config.formParam("seat", 103);
        config.formParam("seat", 104);
        config.formParam("seat", 105);
        config.formParam("seat", 106);
        config.formParam("seat", 107);
      });

      assertEquals(
          Testing.handle0(http),

          """
          HTTP/1.1 400 Bad Request
          Date: Mon, 28 Apr 2025 13:01:00 GMT
          Content-Type: text/html; charset=utf-8
          Transfer-Encoding: chunked

          back-link: /demo.landing/movie/11

          # Show details

          title: Title 1
          alert: LIMIT
          date: Sat 25/Jan
          time: 13:00
          screen: Screen 1

          # Seats

          reservationId: 901
          screenId: 31
          """
      );
    });
  }

  @Test(description = "No seats selected")
  public void testCase12() {
    Testing.rollback(trx -> {
      Testing.load(trx, data);

      Testing.load(trx, """
      insert into RESERVATION (RESERVATION_ID, SHOW_ID)
      values (901, 61);
      """);

      final Http.Exchange http;
      http = Testing.http(config -> {
        config.set(Sql.Transaction.class, trx);

        config.method(Http.Method.POST);

        config.path("/demo.landing/seats");

        config.header(Http.HeaderName.WAY_REQUEST, "true");

        config.formParam("reservationId", 901);

        config.formParam("screenId", 31);

        // no seats selected
        config.formParam("dummy", "");
      });

      assertEquals(
          Testing.handle0(http),

          """
          HTTP/1.1 400 Bad Request
          Date: Mon, 28 Apr 2025 13:01:00 GMT
          Content-Type: text/html; charset=utf-8
          Transfer-Encoding: chunked

          back-link: /demo.landing/movie/11

          # Show details

          title: Title 1
          alert: EMPTY
          date: Sat 25/Jan
          time: 13:00
          screen: Screen 1

          # Seats

          reservationId: 901
          screenId: 31
          """
      );
    });
  }

}