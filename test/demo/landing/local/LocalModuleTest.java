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
package demo.landing.local;

import static org.testng.Assert.assertEquals;

import demo.landing.AbstractTest;
import demo.landing.app.Testing;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import objectos.way.Http;
import objectos.way.Sql;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class LocalModuleTest extends AbstractTest {

  private record Reservation(long id) {
    Reservation(ResultSet rs, int idx) throws SQLException {
      this(rs.getLong(idx++));
    }
  }

  private record Show(int screening, String time) {
    Show(ResultSet rs, int idx) throws SQLException {
      this(
          rs.getInt(idx++),
          rs.getString(idx++)
      );
    }
  }

  @BeforeClass
  public void clearAllReservations() {
    Testing.commit("delete from RESERVATION");
  }

  @Test
  public void clearReservation01() {
    testData("""
    insert into RESERVATION (RESERVATION_ID, SHOW_ID, RESERVATION_TIME, TICKET_TIME)
    values (901, 61, '2025-01-24 10:00', null)
    ,      (902, 61, '2025-01-25 09:54', null)
    ,      (903, 61, '2025-01-25 09:55', null)
    ,      (904, 61, '2025-01-25 09:56', null)
    ,      (905, 61, '2025-01-25 10:00', null);
    """);

    final Http.TestingExchange http;
    http = Http.TestingExchange.create(config -> {
      config.set(Sql.Transaction.class, trx);
    });

    final LocalModule module;
    module = new LocalModule(Testing.INJECTOR);

    module.clearReservation(http);

    assertEquals(http.responseStatus(), Http.Status.OK);

    List<Reservation> result;
    result = queryReservation();

    assertEquals(result.size(), 3);
    assertEquals(result.get(0).id, 903);
    assertEquals(result.get(1).id, 904);
    assertEquals(result.get(2).id, 905);
  }

  @Test
  public void clearReservation02() {
    testData("""
    insert into RESERVATION (RESERVATION_ID, SHOW_ID, RESERVATION_TIME, TICKET_TIME)
    values (901, 61, '2025-01-24 10:00', '2025-01-24 11:00')
    ,      (902, 61, '2025-01-25 09:54', null)
    ,      (903, 61, '2025-01-25 09:55', null)
    ,      (904, 61, '2025-01-25 09:56', null)
    ,      (905, 61, '2025-01-25 10:00', null);
    """);

    final Http.TestingExchange http;
    http = Http.TestingExchange.create(config -> {
      config.set(Sql.Transaction.class, trx);
    });

    final LocalModule module;
    module = new LocalModule(Testing.INJECTOR);

    module.clearReservation(http);

    assertEquals(http.responseStatus(), Http.Status.OK);

    List<Reservation> result;
    result = queryReservation();

    assertEquals(result.size(), 4);
    assertEquals(result.get(0).id, 901);
    assertEquals(result.get(1).id, 903);
    assertEquals(result.get(2).id, 904);
    assertEquals(result.get(3).id, 905);
  }

  @Test
  public void clearReservation03() {
    testData("""
    insert into RESERVATION (RESERVATION_ID, SHOW_ID, RESERVATION_TIME, TICKET_TIME)
    values (901, 61, '2025-01-24 10:00', null)
    ,      (902, 61, '2025-01-25 09:54', null)
    ,      (903, 61, '2025-01-25 09:55', null)
    ,      (904, 61, '2025-01-25 09:56', null)
    ,      (905, 61, '2025-01-25 10:00', null);

    insert into SELECTION (RESERVATION_ID, SEAT_ID, SHOW_ID)
    values (901, 103, 61)
    ,      (902, 104, 61);
    """);

    final Http.TestingExchange http;
    http = Http.TestingExchange.create(config -> {
      config.set(Sql.Transaction.class, trx);
    });

    final LocalModule module;
    module = new LocalModule(Testing.INJECTOR);

    module.clearReservation(http);

    assertEquals(http.responseStatus(), Http.Status.OK);

    List<Reservation> result;
    result = queryReservation();

    assertEquals(result.size(), 3);
    assertEquals(result.get(0).id, 903);
    assertEquals(result.get(1).id, 904);
    assertEquals(result.get(2).id, 905);
  }

  @Test
  public void createShow01() {
    final Http.TestingExchange http1;
    http1 = Http.TestingExchange.create(config -> {
      config.set(Sql.Transaction.class, trx);
    });

    final LocalModule module;
    module = new LocalModule(Testing.INJECTOR);

    module.createShow(http1);

    assertEquals(http1.responseStatus(), Http.Status.OK);

    final List<Show> result1;
    result1 = queryShow(LocalDate.of(2025, 1, 27));

    assertEquals(result1.size(), 9);
    assertEquals(result1.get(0), new Show(41, "13:00:00"));
    assertEquals(result1.get(1), new Show(41, "16:00:00"));
    assertEquals(result1.get(2), new Show(41, "20:00:00"));

    assertEquals(result1.get(3), new Show(42, "13:00:00"));
    assertEquals(result1.get(4), new Show(42, "16:00:00"));
    assertEquals(result1.get(5), new Show(42, "19:00:00"));

    assertEquals(result1.get(6), new Show(43, "14:00:00"));
    assertEquals(result1.get(7), new Show(43, "16:00:00"));
    assertEquals(result1.get(8), new Show(43, "19:00:00"));

    final Http.TestingExchange http2;
    http2 = Http.TestingExchange.create(config -> {
      config.set(Sql.Transaction.class, trx);
    });

    module.createShow(http2);

    assertEquals(http2.responseStatus(), Http.Status.OK);

    final List<Show> result2;
    result2 = queryShow(LocalDate.of(2025, 1, 27));

    assertEquals(result2.size(), 9);
  }

  private List<Reservation> queryReservation() {
    trx.sql("select RESERVATION_ID from RESERVATION order by 1");

    return trx.query(Reservation::new);
  }

  private List<Show> queryShow(LocalDate dt) {
    trx.sql("select SCREENING_ID, SHOWTIME from SHOW where SHOWDATE = ? order by 1, 2");

    trx.add(dt);

    return trx.query(Show::new);
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

    insert into SCREENING_TIME (SCREENING_ID, SCREENING_TIME, SEAT_PRICE)
    values (41, '13:00:00', 9.99)
    ,      (41, '16:00:00', 14.99)
    ,      (41, '20:00:00', 19.99)
    ,      (42, '13:00:00', 9.99)
    ,      (42, '16:00:00', 14.99)
    ,      (42, '19:00:00', 19.99)
    ,      (43, '14:00:00', 9.99)
    ,      (43, '16:00:00', 14.99)
    ,      (43, '19:00:00', 19.99);
    """;
  }

}