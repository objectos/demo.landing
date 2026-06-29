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

import java.util.List;
import objectos.http.Handler;
import objectos.http.Request;
import objectos.http.Result;
import objectos.script.JsAction;
import objectos.way.Sql;

/// The `/home` controller.
final class Home implements Handler {

  private final AppCtx ctx;

  Home(AppCtx ctx) {
    this.ctx = ctx;
  }

  @Override
  public final Result handle(Request req) {
    final AppReservation reservation;
    reservation = AppReservation.parse(req);

    final Sql.Transaction trx;
    trx = req.attr(Sql.Transaction.class);

    final List<HomeModel> rows;
    rows = HomeModel.query(trx);

    final List<HomeView.Movie> movies;
    movies = rows.stream().map(row -> toUi(reservation, row)).toList();

    return UiShell.of(opts -> {
      opts.homeAction = ctx.clickAction(AppView.HOME, reservation);

      opts.main = new HomeView(movies);

      opts.sources = List.of(
          Source.Home,
          Source.HomeModel,
          Source.HomeView
      );
    });
  }

  private HomeView.Movie toUi(AppReservation reservation, HomeModel original) {
    final String title;
    title = original.title();

    final int id;
    id = original.id();

    final JsAction onclick;
    onclick = ctx.clickAction(AppView.MOVIE, id, reservation);

    final String imgsrc;
    imgsrc = "/demo.landing/poster-" + id + ".jpg";

    return new HomeView.Movie(title, onclick, imgsrc);
  }

}