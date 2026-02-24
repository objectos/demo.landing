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

/// The details of a particular movie.
record MovieDetails(
    int movieId,
    String title,
    String runtime,
    String releaseDate,
    String genres,
    String synopsys
) {

  MovieDetails(ResultSet rs, int idx) throws SQLException {
    this(
        rs.getInt(idx++),
        rs.getString(idx++),
        rs.getString(idx++),
        rs.getString(idx++),
        rs.getString(idx++),
        rs.getString(idx++)
    );
  }

  public static Optional<MovieDetails> queryOptional(Sql.Transaction trx, int id) {
    trx.sql("""
    select
      MOVIE.MOVIE_ID,
      MOVIE.TITLE,
      concat (MOVIE.RUNTIME / 60, 'h ', MOVIE.RUNTIME % 60, 'm'),
      formatdatetime (MOVIE.RELEASE_DATE, 'MMM dd, yyyy'),
      listagg (GENRE.NAME, ', ') within group (
        order by
          GENRE.NAME
      ),
      MOVIE.SYNOPSYS
    from
      MOVIE
      natural join MOVIE_GENRE
      natural join GENRE
    where
      MOVIE.MOVIE_ID = ?
    group by
      MOVIE.MOVIE_ID
    """);

    trx.param(id);

    return trx.queryOptional(MovieDetails::new);
  }

}