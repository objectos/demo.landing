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

import demo.landing.app.Kino.Action;
import module java.base;
import module objectos.way;

/// Filters HTTP requests around a SQL transaction and performs a no-op in
/// testing environments.
final class AppTransactional implements Http.Filter {

  private final Kino.Stage stage;

  private final Sql.Database db;

  AppTransactional(Kino.Stage stage, Sql.Database db) {
    this.stage = stage;

    this.db = db;
  }

  public static AppTransactional of(Kino.Stage stage, Sql.Database db) {
    Objects.requireNonNull(stage, "stage == null");
    Objects.requireNonNull(db, "db == null");

    return new AppTransactional(stage, db);
  }

  public final Html.Component get(Http.Exchange http, Kino.GET action) {
    return execute(http, action::get);
  }

  public final Kino.PostResult post(Http.Exchange http, Kino.POST action) {
    return execute(http, action::post);
  }

  @Override
  public final void filter(Http.Exchange http, Http.Handler handler) {
    switch (stage) {
      case DEFAULT -> {
        final Sql.Transaction trx;
        trx = db.beginTransaction(Sql.READ_COMMITED);

        try {
          trx.sql("set schema CINEMA");

          trx.update();

          http.set(Sql.Transaction.class, trx);

          handler.handle(http);

          trx.commit();
        } catch (Throwable e) {
          throw trx.rollbackAndWrap(e);
        } finally {
          trx.close();
        }
      }

      // this is a no-op during testing.
      case TESTING -> handler.handle(http);
    }
  }

  private <T> T execute(Http.Exchange http, Action<T> action) {
    return switch (stage) {
      case DEFAULT -> {

        final Sql.Transaction trx;
        trx = db.beginTransaction(Sql.READ_COMMITED);

        try {

          trx.sql("set schema CINEMA");

          trx.update();

          http.set(Sql.Transaction.class, trx);

          final T result;
          result = action.execute(http);

          trx.commit();

          yield result;

        } catch (Throwable e) {
          throw trx.rollbackAndWrap(e);
        } finally {
          trx.close();
        }

      }

      // this is a no-op during testing.
      case TESTING -> action.execute(http);
    };
  }

}