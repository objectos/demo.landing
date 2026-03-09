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

import module java.base;
import module objectos.way;

/// The `/movie/{id}` controller.
final class Movie implements Http.Handler {

  private final AppCtx ctx;

  Movie(AppCtx ctx) {
    this.ctx = ctx;
  }

  @Override
  public final void handle(Http.Exchange http) {
    final Sql.Transaction trx;
    trx = http.get(Sql.Transaction.class);

    final int movieId;
    movieId = http.pathParamAsInt("id", Integer.MIN_VALUE);

    final Optional<MovieDetails> maybeDetails;
    maybeDetails = MovieDetails.queryOptional(trx, movieId);

    if (maybeDetails.isEmpty()) {
      return;
    }

    final AppReservation reservation;
    reservation = AppReservation.parse(http);

    final MovieDetails details;
    details = maybeDetails.get();

    final LocalDateTime now;
    now = ctx.now();

    final List<MovieScreening> rows;
    rows = MovieScreening.query(trx, movieId, now);

    final List<MovieView.Screening> screenings;
    screenings = rows.stream().map(row -> toUi(reservation, row)).toList();

    final UiShell shell;
    shell = UiShell.of(opts -> {
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

    http.ok(shell);
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