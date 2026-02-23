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
package demo.landing.home;

import demo.landing.ui.Content;
import demo.landing.ui.Shell;
import demo.landing.ui.Source;
import demo.landing.ui.Sources;
import module java.base;
import module objectos.way;

/// The "/home" controller.
public final class Home implements Http.Handler {

  private final Sources sources = Sources.of(
      Source.Home,
      Source.NowShowing
  );

  private Home() {}

  public static Home create() {
    return new Home();
  }

  @Override
  public final void handle(Http.Exchange http) {
    final Sql.Transaction trx;
    trx = http.get(Sql.Transaction.class);

    final List<NowShowing> movies;
    movies = NowShowing.query(trx);

    http.ok(
        Shell.of(
            Content.of(
                new Header(),

                new MovieSelector(movies)
            ),

            sources
        )
    );
  }

}