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

import objectos.http.HttpExchange;
import objectos.http.HttpHandler;
import objectos.way.Sql;

final class Poster implements HttpHandler {

  @Override
  public final void handle(HttpExchange http) {
    final int id;
    id = http.pathParamAsInt("id", Integer.MIN_VALUE);

    if (id < 1) {
      return;
    }

    if (id > 4) {
      return;
    }

    final Sql.Transaction trx;
    trx = http.req(Sql.Transaction.class);

    final PosterModel model;
    model = PosterModel.query(trx, id);

    http.staticFile(model);
  }

}
