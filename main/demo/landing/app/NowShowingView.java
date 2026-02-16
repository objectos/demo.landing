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

/**
 * Renders the "Now Showing" view.
 */
final class NowShowingView extends Kino.View {

  private final Kino.Ctx ctx;

  private final List<NowShowingModel> items;

  NowShowingView(Kino.Ctx ctx, List<NowShowingModel> items) {
    this.ctx = ctx;

    this.items = items;
  }

  @Override
  protected final void render() {
    div(
        css("""
        margin-bottom:32rx
        """),

        h2(
            text("Now Showing")
        ),

        p(
            text("Please choose a movie")
        )
    );

    ul(
        css("""
        display:flex
        flex-wrap:wrap
        gap:16rx
        justify-content:space-evenly
        """),

        f(this::renderItems)
    );
  }

  private void renderItems() {
    for (NowShowingModel item : items) {
      li(
          css("""
          flex:0_0_128rx
          """),

          a(
              css("""
              group
              """),

              onclick(FOLLOW),

              href(ctx.href(Kino.Page.MOVIE, item.id())),

              rel("nofollow"),

              img(
                  css("""
                  aspect-ratio:2/3
                  background-color:var(--color-neutral-400)
                  border-radius:6rx

                  &:is(:where(.group):hover_*)/outline:2px_solid_var(--color-gray-500)
                  """),

                  src("/demo/landing/poster" + item.id() + ".jpg")
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
                      testableField("movie.title", item.title())
                  )
              )
          )
      );
    }
  }

}