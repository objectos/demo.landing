/*
 * Copyright (C) 2024-2026 Objectos Software LTDA.
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
import objectos.http.Content;
import objectos.http.ContentProvider;
import objectos.http.MediaType;
import objectos.way.Sql;

record PosterModel(byte[] data) implements ContentProvider {

  private PosterModel(ResultSet rs, int idx) throws SQLException {
    this(
        rs.getBytes(idx++)
    );
  }

  public static PosterModel query(Sql.Transaction trx, int id) {
    trx.sql("""
    select
      DATA
    from
      MOVIE_POSTER
    where
      MOVIE_ID = ?
    """);

    trx.param(id);

    return trx.querySingle(PosterModel::new);
  }

  @Override
  public final Content toContent() {
    return Content.of(MediaType.IMAGE_JPEG, data);
  }

}
