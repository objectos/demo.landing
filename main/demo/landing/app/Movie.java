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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import objectos.way.Html;
import objectos.way.Http;
import objectos.way.Sql;

final class Movie implements Kino.GET {

  private final Kino.Ctx ctx;

  Movie(Kino.Ctx ctx) {
    this.ctx = ctx;
  }

  @Override
  public final Html.Component get(Http.Exchange http) {
    final Sql.Transaction trx;
    trx = http.get(Sql.Transaction.class);

    final Kino.Query query;
    query = http.get(Kino.Query.class);

    final int movieId;
    movieId = query.idAsInt();

    final Optional<MovieDetails> maybeDetails;
    maybeDetails = MovieDetails.queryOptional(trx, movieId);

    if (maybeDetails.isEmpty()) {
      return NotFound.create();
    }

    final MovieDetails details;
    details = maybeDetails.get();

    final LocalDateTime today;
    today = ctx.today();

    final List<MovieScreening> screenings;
    screenings = MovieScreening.query(trx, movieId, today);

    return Shell.create(shell -> {
      shell.app = new MovieView(ctx, details, screenings);

      shell.sources(
          Source.Movie,
          Source.MovieDetails,
          Source.MovieScreening,
          Source.MovieView
      );
    });
  }

}