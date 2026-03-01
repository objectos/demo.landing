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

/// The "/movie/{id}" controller.
final class Movie extends AppTransactional {

  private final Clock clock;

  Movie(App.Injector injector) {
    super(injector);

    clock = injector.getInstance(Clock.class);
  }

  @Override
  final void handle(Http.Exchange http, Sql.Transaction trx) {
    final AppUrl url;
    url = AppUrl.parse(http);

    final int movieId;
    movieId = url.aux();

    final Optional<MovieDetails> maybeDetails;
    maybeDetails = MovieDetails.queryOptional(trx, movieId);

    if (maybeDetails.isPresent()) {
      final MovieDetails details;
      details = maybeDetails.get();

      final LocalDateTime now;
      now = LocalDateTime.now(clock);

      final List<MovieScreening> screenings;
      screenings = MovieScreening.query(trx, movieId, now);

      final MovieView view;
      view = new MovieView(url, details, screenings);

      http.ok(view);
    } else {
      final NotFoundView view;
      view = new NotFoundView();

      http.notFound(view);
    }
  }

}