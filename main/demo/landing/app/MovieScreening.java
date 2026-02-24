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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import objectos.way.Sql;

/// The showing of a movie in this theater.
record MovieScreening(
    int screenId,
    String screenName,
    String features,
    String date,
    List<MovieShowtime> showtimes
) {

  MovieScreening(ResultSet rs, int idx) throws SQLException {
    this(
        rs.getInt(idx++),
        rs.getString(idx++),
        rs.getString(idx++),
        rs.getString(idx++),
        MovieShowtime.of(rs.getArray(idx++))
    );
  }

  public static List<MovieScreening> query(Sql.Transaction trx, int id, LocalDateTime dateTime) {
    trx.sql("""
    select
      SCREEN.SCREEN_ID,
      SCREEN.NAME,
      (
        select
          listagg (FEATURE.NAME, ', ') within group (order by FEATURE.FEATURE_ID)
        from
          SCREENING_FEATURE
          natural join FEATURE
        where
          SCREENING_FEATURE.SCREENING_ID = SCREENING.SCREENING_ID
        group by
          SCREENING_FEATURE.SCREENING_ID
      ) as F,
      formatdatetime(SHOW.SHOWDATE, 'EEE dd/LLL'),
      array_agg (
        array [cast(SHOW.SHOW_ID as varchar), formatdatetime(SHOW.SHOWTIME, 'kk:mm')]
        order by SHOW.SHOWTIME
      )
    from
      SHOW
      natural join SCREENING
      natural join SCREEN
    where
      SCREENING.MOVIE_ID = ?
      and (
        (
          SHOW.SHOWDATE = ?
          and SHOW.SHOWTIME > ?
        )
        or SHOW.SHOWDATE > ?
      )
    group by
      SHOW.SHOWDATE,
      SCREEN.SCREEN_ID
    order by
      SHOW.SHOWDATE,
      SCREEN.SCREEN_ID
    """);

    trx.param(id);

    final LocalDate date;
    date = dateTime.toLocalDate();

    trx.param(date);

    final LocalTime time;
    time = dateTime.toLocalTime();

    trx.param(time);

    trx.param(date);

    return trx.query(MovieScreening::new);
  }

}