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

/// Renders the home page view. More specifically, it renders:
///
/// - a top header with the "Now Showing" title.
/// - a list of the movies that are
/// currently playing.
final class HomeView extends UiShell {

  private final List<HomeModel> movies;

  HomeView(List<HomeModel> movies) {
    this.movies = movies;
  }

  @Override
  final List<SourceModel> viewSources() {
    return List.of(
        Source.Home,
        Source.HomeModel,
        Source.HomeView
    );
  }

  @Override
  final void renderMain() {
    div(
        css("""
        margin-bottom:32rx
        """),

        h2("Now Showing"),

        p("Please choose a movie")
    );

    ul(
        css("""
        display:flex
        flex-wrap:wrap
        gap:16rx
        justify-content:space-evenly
        """),

        f(this::renderMovies)
    );
  }

  private void renderMovies() {
    for (HomeModel movie : movies) {
      renderMovie(movie);
    }
  }

  private void renderMovie(HomeModel movie) {
    li(
        css("""
        flex:0_0_128rx
        """),

        div(
            css("""
            group
            hover/cursor:pointer
            """),

            onclick(follow("/demo.landing/movie/" + movie.id())),

            img(
                css("""
                aspect-ratio:2/3
                background-color:var(--color-neutral-400)
                border-radius:6rx

                &:is(:where(.group):hover_*)/outline:2px_solid_var(--color-gray-500)
                """),

                src("/demo/landing/poster" + movie.id() + ".jpg")
            ),

            h3(
                css("""
                font-size:14rx
                line-height:18rx
                text-align:center
                padding-top:8rx

                &:is(:where(.group):hover_*)/text-decoration:underline
                """),

                text(
                    testableField("movie.title", movie.title())
                )
            )
        )
    );
  }

}