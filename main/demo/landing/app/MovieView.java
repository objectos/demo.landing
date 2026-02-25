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

import java.util.List;

/// Renders the details of a movie and lists its available screenings.
final class MovieView extends UiShell {

  private final MovieDetails details;

  private final List<MovieScreening> screenings;

  MovieView(MovieDetails details, List<MovieScreening> screenings) {
    this.details = details;

    this.screenings = screenings;
  }

  @Override
  final List<SourceModel> viewSources() {
    return List.of(
        Source.Movie,
        Source.MovieDetails,
        Source.MovieScreening,
        Source.MovieShowtime
    );
  }

  @Override
  final void renderMain() {
    backLink("/demo.landing/home");

    div(
        css("""
        display:grid
        gap:16rx
        grid-template-columns:1fr_160rx
        """),

        f(this::renderMovieDetails)
    );

    div(
        css("""
        display:flex
        flex-direction:column
        gap:16rx
        padding:64rx_0_0
        """),

        h3(
            css("""
            font-size:24rx
            font-weight:300
            line-height:1
            """),

            text(testableH1("Showtimes"))
        ),

        f(this::renderMovieScreenings)
    );
  }

  // ##################################################################
  // # BEGIN: Movie Details
  // ##################################################################

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

  // ##################################################################
  // # END: Movie Details
  // ##################################################################

  // ##################################################################
  // # BEGIN: Movie Screening
  // ##################################################################

  private void renderMovieScreenings() {
    for (MovieScreening screening : screenings) {
      renderMovieScreening(screening);
    }
  }

  private void renderMovieScreening(MovieScreening screening) {
    div(
        css("""
        border:1px_solid_var(--color-border)
        display:flex
        gap:32rx
        padding:16rx
        position:relative
        """),

        div(
            css("""
            align-items:center
            display:flex
            flex-direction:column
            gap:8rx
            justify-content:center
            """),

            c(
                UiIcon.CALENDAR_CHECK.css("""
                stroke:icon
                """)
            ),

            span(
                css("""
                text-align:center
                width:6rch
                """),

                text(testableH2(screening.date()))
            )
        ),

        div(
            h4(
                css("""
                font-weight:500
                line-height:1
                padding-top:8rx
                """),

                text(testableField("screen", screening.screenName()))
            ),

            div(
                css("""
                font-size:14rx
                font-weight:300
                padding:8rx_0
                """),

                text(testableField("features", screening.features()))
            ),

            ul(
                css("""
                display:flex
                flex-wrap:wrap
                gap:12rx
                """),

                testableNewLine(),

                f(this::renderMovieShowtimes, screening.showtimes())
            )
        )
    );
  }

  // ##################################################################
  // # END: Movie Screening
  // ##################################################################

  // ##################################################################
  // # BEGIN: Movie Showtime
  // ##################################################################

  private void renderMovieShowtimes(List<MovieShowtime> showtimes) {
    for (MovieShowtime showtime : showtimes) {
      final int showId;
      showId = showtime.showId();

      testableCell(Integer.toString(showId), 2);

      final String time;
      time = showtime.time();

      li(
          div(
              css("""
              border:1px_solid_var(--color-border)
              border-radius:9999px
              display:flex
              padding:8rx_16rx

              active/background-color:var(--color-btn-ghost-active)
              hover/background-color:var(--color-btn-ghost-hover)
              hover/cursor:pointer
              """),

              onclick(Kino.link("/demo.landing/show/" + showId)),

              rel("nofollow"),

              span(testableCell(time, 5))
          ),

          testableNewLine()
      );
    }
  }

  // ##################################################################
  // # END: Movie Showtime
  // ##################################################################

}