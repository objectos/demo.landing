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

/// Renders the movie details.
final class MovieDetailsUi extends Html.Template {

  private final MovieDetails details;

  MovieDetailsUi(MovieDetails details) {
    this.details = details;
  }

  @Override
  protected final void render() {
    div(
        css("""
        display:grid
        gap:16rx
        grid-template-columns:1fr_160rx
        """),

        f(this::renderMovieDetails)
    );
  }

  private void renderMovieDetails() {
    div(
        h2(testableH1(details.title())),

        p(testableField("synopsys", details.synopsys())),

        dl(
            css("""
            display:grid
            font-size:14rx
            grid-template-columns:repeat(2,1fr)

            &_dt/font-weight:600
            &_dt/padding-top:12rx
            """),

            div(
                dt("Rating"),
                dd("N/A")
            ),

            div(
                dt("Runtime"),
                dd(testableField("runtime", details.runtime()))
            ),

            div(
                dt("Release date"),
                dd(testableField("release-date", details.releaseDate()))
            ),

            div(
                dt("Genres"),
                dd(testableField("genres", details.genres()))
            )
        )
    );

    div(
        css("""
        padding-top:16rx
        """),

        img(
            css("""
            border-radius:6rx
            width:100%
            """),

            alt(details.title()),

            src("/demo/landing/poster" + details.movieId() + ".jpg")
        )
    );
  }

}