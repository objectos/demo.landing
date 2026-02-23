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
package demo.landing.home;

import module java.base;
import module objectos.way;

/// A movie that is now showing at the theater.
record NowShowing(
    int id,
    String title
) {

  private NowShowing(ResultSet rs, int idx) throws SQLException {
    this(
        rs.getInt(idx++),
        rs.getString(idx++)
    );
  }

  public static List<NowShowing> query(Sql.Transaction trx) {
    trx.sql("""
    select
      MOVIE.MOVIE_ID,
      MOVIE.TITLE

    from
      MOVIE

    order by
      MOVIE_ID
    """);

    return trx.query(NowShowing::new);
  }

}