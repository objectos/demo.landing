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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import objectos.http.Handler;
import objectos.http.Request;
import objectos.http.Result;
import objectos.script.JsAction;
import objectos.way.Sql;

/// The `/movie/{id}` controller.
final class Movie implements Handler {

  private final AppCtx ctx;

  Movie(AppCtx ctx) {
    this.ctx = ctx;
  }

  @Override
  public final Result handle(Request req) {
    final Sql.Transaction trx;
    trx = req.attr(Sql.Transaction.class);

    final int movieId;
    movieId = req.pathParamAsInt("id", Integer.MIN_VALUE);

    final Optional<MovieDetails> maybeDetails;
    maybeDetails = MovieDetails.queryOptional(trx, movieId);

    if (maybeDetails.isEmpty()) {
      return req;
    }

    final AppReservation reservation;
    reservation = AppReservation.parse(req);

    final MovieDetails details;
    details = maybeDetails.get();

    final LocalDateTime now;
    now = ctx.now();

    final List<MovieScreening> rows;
    rows = MovieScreening.query(trx, movieId, now);

    final List<MovieView.Screening> screenings;
    screenings = rows.stream().map(row -> toUi(reservation, row)).toList();

    return UiShell.of(opts -> {
      opts.backAction = opts.homeAction = ctx.clickAction(AppView.HOME, reservation);

      opts.main = new MovieView(details, screenings);

      opts.sources = List.of(
          Source.Movie,
          Source.MovieDetails,
          Source.MovieScreening,
          Source.MovieShowtime,
          Source.MovieView
      );
    });
  }

  private MovieView.Screening toUi(AppReservation reservation, MovieScreening original) {
    return new MovieView.Screening(
        original.screenName(),
        original.features(),
        original.date(),
        original.showtimes().stream().map(row -> toUi(reservation, row)).toList()
    );
  }

  private MovieView.Showtime toUi(AppReservation reservation, MovieShowtime original) {
    final int showId;
    showId = original.showId();

    final JsAction clickAction;
    clickAction = ctx.clickAction(AppView.SEATS, showId, reservation);

    return new MovieView.Showtime(showId, original.time(), clickAction);
  }

}