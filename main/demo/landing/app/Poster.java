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

import objectos.http.Handler;
import objectos.http.Request;
import objectos.http.Result;
import objectos.http.StaticFile;
import objectos.way.Sql;

final class Poster implements Handler {

  @Override
  public final Result handle(Request req) {
    final int id;
    id = req.pathParamAsInt("id", Integer.MIN_VALUE);

    if (id < 1) {
      return req;
    }

    if (id > 4) {
      return req;
    }

    final Sql.Transaction trx;
    trx = req.attr(Sql.Transaction.class);

    final PosterModel model;
    model = PosterModel.query(trx, id);

    return StaticFile.of(model);
  }

}
