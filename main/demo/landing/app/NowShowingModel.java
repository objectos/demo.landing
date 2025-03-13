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
import java.util.List;
import objectos.way.Sql;

/**
 * Represents a movie in the "Now Showing" view.
 */
record NowShowingModel(
    int id,
    String title
) {

  private NowShowingModel(ResultSet rs, int idx) throws SQLException {
    this(
        rs.getInt(idx++),
        rs.getString(idx++)
    );
  }

  public static List<NowShowingModel> query(Sql.Transaction trx) {
    trx.sql("""
    select
      MOVIE.MOVIE_ID,
      MOVIE.TITLE

    from
      MOVIE

    order by
      MOVIE_ID
    """);

    return trx.query(NowShowingModel::new);
  }

}