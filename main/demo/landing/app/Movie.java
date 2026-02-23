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

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import objectos.way.Http;
import objectos.way.Sql;

final class Movie implements Http.Handler {

  private final Clock clock;

  Movie(Clock clock) {
    this.clock = clock;
  }

  @Override
  public final void handle(Http.Exchange http) {
    final Sql.Transaction trx;
    trx = http.get(Sql.Transaction.class);

    final String id;
    id = http.pathParam("id");

    final int movieId;
    movieId = Integer.parseInt(id);

    final Optional<MovieDetails> maybeDetails;
    maybeDetails = MovieDetails.queryOptional(trx, movieId);

    if (maybeDetails.isEmpty()) {
      return;
    }

    final MovieDetails details;
    details = maybeDetails.get();

    final LocalDateTime today;
    today = LocalDateTime.now(clock);

    final List<MovieScreening> screenings;
    screenings = MovieScreening.query(trx, movieId, today);

    http.ok(
        new Shell(
            new MovieView(details, screenings)

        //            Source.Movie,
        //            Source.MovieDetails,
        //            Source.MovieScreening,
        //            Source.MovieView
        )
    );
  }

}