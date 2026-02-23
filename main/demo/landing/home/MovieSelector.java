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

import demo.landing.ui.Shell;
import module java.base;
import module objectos.way;

/// Renders the "Now Showing" movie selector.
final class MovieSelector extends Html.Template {

  private final List<NowShowing> movies;

  MovieSelector(List<NowShowing> movies) {
    this.movies = movies;
  }

  @Override
  protected final void render() {
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
    for (NowShowing item : movies) {
      renderItem(item);
    }
  }

  private void renderItem(NowShowing movie) {
    li(
        css("""
        flex:0_0_128rx
        """),

        div(
            css("""
            group
            hover/cursor:pointer
            """),

            onclick(Shell.link("/demo.landing/movie/" + movie.id())),

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