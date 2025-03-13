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
import objectos.way.Css;

/**
 * Movie details and screening selection view.
 */
@Css.Source
final class MovieView extends Kino.View {

  private final Kino.Ctx ctx;

  private final MovieDetails details;

  private final List<MovieScreening> screenings;

  MovieView(Kino.Ctx ctx, MovieDetails details, List<MovieScreening> screenings) {
    this.ctx = ctx;

    this.details = details;

    this.screenings = screenings;
  }

  @Override
  protected final void render() {
    backLink(ctx, Kino.Page.NOW_SHOWING);

    div(
        css("""
        display:grid
        gap:16rx
        grid-template-columns:1fr_160rx
        """),

        renderFragment(this::renderMovieDetails)
    );

    div(
        css("""
        display:flex
        flex-direction:column
        gap:16rx
        padding:64rx_0_0
        """),

        renderFragment(this::renderMovieScreenings)
    );
  }

  private void renderMovieDetails() {
    div(
        h2(
            text(testableH1(details.title()))
        ),

        p(
            text(testableField("synopsys", details.synopsys()))
        ),

        dl(
            css("""
            display:grid
            font-size:14rx
            grid-template-columns:repeat(2,1fr)

            dt:font-weight:600
            dt:padding-top:12rx
            """),

            div(
                dt(
                    text("Rating")
                ),

                dd(
                    text("N/A")
                )
            ),

            div(
                dt(
                    text("Runtime")
                ),

                dd(
                    text(testableField("runtime", details.runtime()))
                )
            ),

            div(
                dt(
                    text("Release date")
                ),

                dd(
                    text(testableField("release-date", details.releaseDate()))
                )
            ),

            div(
                dt(
                    text("Genres")
                ),

                dd(
                    text(testableField("genres", details.genres()))
                )
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

  private void renderMovieScreenings() {
    h3(
        css("""
        font-size:24rx
        font-weight:300
        line-height:1
        """),

        text(testableH1("Showtimes"))
    );

    for (MovieScreening screening : screenings) {
      div(
          css("""
          border:1px_solid_border
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

              icon(
                  Kino.Icon.CALENDAR_CHECK,

                  css("""
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

                  renderFragment(this::renderShowtimes, screening.showtimes())
              )

          )
      );
    }
  }

  private void renderShowtimes(List<MovieScreening.Showtime> showtimes) {
    for (MovieScreening.Showtime showtime : showtimes) {
      final int showId;
      showId = showtime.showId();

      final Kino.Query query;
      query = Kino.Page.SEATS.query(showId);

      testableCell(query.toString(), 32);

      final String time;
      time = showtime.time();

      li(
          a(
              css("""
              border:1px_solid_border
              border-radius:9999rx
              display:flex
              padding:8rx_16rx

              active:background-color:btn-ghost-active
              hover:background-color:btn-ghost-hover
              """),

              dataOnClick(this::navigate),

              href(ctx.href(query)),

              span(testableCell(time, 5))
          ),

          testableNewLine()
      );
    }
  }

}