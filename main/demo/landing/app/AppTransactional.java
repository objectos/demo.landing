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

import module objectos.way;

/// Filters HTTP requests around a SQL transaction and performs a no-op in
/// testing environments.
abstract class AppTransactional implements Http.Handler {

  private final Sql.Database db;

  AppTransactional(App.Injector injector) {
    db = injector.getInstance(Sql.Database.class);
  }

  @Override
  public void handle(Http.Exchange http) {
    final Sql.Transaction trx;
    trx = db.beginTransaction(Sql.READ_COMMITED);

    try {
      trx.sql("set schema CINEMA");

      trx.update();

      handle(http, trx);

      trx.commit();
    } catch (Throwable e) {
      throw trx.rollbackAndWrap(e);
    } finally {
      trx.close();
    }
  }

  abstract void handle(Http.Exchange http, Sql.Transaction trx);

}