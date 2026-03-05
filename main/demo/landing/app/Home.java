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

import module java.base;
import module objectos.way;

/// The "/home" controller.
final class Home implements Http.Handler {

  private final AppCtx kino;

  Home(AppCtx kino) {
    this.kino = kino;
  }

  @Override
  public final void handle(Http.Exchange http) {
    final String hashValue;
    hashValue = http.header(AppCtx.DEMO_LOCATION_HASH);

    final String hashRedirect;
    hashRedirect = kino.decodeHash(hashValue);

    if (hashRedirect != null) {
      http.found(hashRedirect);

      return;
    }

    final Sql.Transaction trx;
    trx = http.get(Sql.Transaction.class);

    final List<HomeModel> rows;
    rows = HomeModel.query(trx);

    final AppReservation reservation;
    reservation = AppReservation.parse(http);

    final List<HomeView.Movie> movies;
    movies = rows.stream().map(row -> toUi(reservation, row)).toList();

    final HomeView view;
    view = new HomeView(movies);

    http.ok(view);
  }

  private HomeView.Movie toUi(AppReservation reservation, HomeModel row) {
    final String title;
    title = row.title();

    final int id;
    id = row.id();

    final JsAction onclick;
    onclick = kino.clickAction(AppView.MOVIE, id, reservation);

    final String imgsrc;
    imgsrc = "/demo.landing/poster" + id + ".jpg";

    return new HomeView.Movie(title, onclick, imgsrc);
  }

}