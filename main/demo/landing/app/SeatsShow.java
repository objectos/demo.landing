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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import objectos.way.Sql;

record SeatsShow(
    int showId,
    String date,
    String time,

    int screenId,
    String screen,
    int capacity,

    int movieId,
    String title
) {

  private SeatsShow(ResultSet rs, int idx) throws SQLException {
    this(
        rs.getInt(idx++),
        rs.getString(idx++),
        rs.getString(idx++),

        rs.getInt(idx++),
        rs.getString(idx++),
        rs.getInt(idx++),

        rs.getInt(idx++),
        rs.getString(idx++)
    );
  }

  public static Optional<SeatsShow> queryOptional(Sql.Transaction trx, int id) {
    trx.sql("""
    select
      SHOW.SHOW_ID,
      formatdatetime(SHOW.SHOWDATE, 'EEE dd/LLL'),
      formatdatetime(SHOW.SHOWTIME, 'kk:mm'),

      SCREEN.SCREEN_ID,
      SCREEN.NAME,
      SCREEN.SEATING_CAPACITY,

      MOVIE.MOVIE_ID,
      MOVIE.TITLE
    from
      SHOW
      natural join SCREENING
      natural join MOVIE
      join SCREEN on SCREENING.SCREEN_ID = SCREEN.SCREEN_ID
    where
      SHOW.SHOW_ID = ?
    """);

    trx.param(id);

    return trx.queryOptional(SeatsShow::new);
  }

  public static Optional<SeatsShow> queryBackButton(Sql.Transaction trx, long reservationId) {
    reservation(trx, reservationId);

    return trx.queryOptional(SeatsShow::new);
  }

  public static SeatsShow queryReservation(Sql.Transaction trx, long reservationId) {
    reservation(trx, reservationId);

    return trx.querySingle(SeatsShow::new);
  }

  private static void reservation(Sql.Transaction trx, long reservationId) {
    trx.sql("""
    select
      SHOW.SHOW_ID,
      formatdatetime(SHOW.SHOWDATE, 'EEE dd/LLL'),
      formatdatetime(SHOW.SHOWTIME, 'kk:mm'),

      SCREEN.SCREEN_ID,
      SCREEN.NAME,
      SCREEN.SEATING_CAPACITY,

      MOVIE.MOVIE_ID,
      MOVIE.TITLE
    from
      RESERVATION
      natural join SHOW
      natural join SCREENING
      natural join MOVIE
      join SCREEN on SCREENING.SCREEN_ID = SCREEN.SCREEN_ID
    where
      RESERVATION.RESERVATION_ID = ?
    """);

    trx.param(reservationId);
  }

}