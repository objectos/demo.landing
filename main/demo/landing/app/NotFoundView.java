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

final class NotFoundView extends Kino.View {

  @Override
  protected final void render() {
    h2(
        testableH1("Something Went Wrong")
    );

    p("Sorry, we could not find the page you're looking for.");

    div(
        css("""
        display:flex
        justify-content:center
        padding:48rx_0
        """),

        icon(
            Kino.Icon.FROWN,

            css("""
            height:auto
            stroke-width:0.5rx
            width:120rx
            """)
        )
    );

    div(
        css("""
        display:flex
        justify-content:center
        """),

        a(
            PRIMARY,

            onclick(FOLLOW),

            href("/index.html"),

            text("Get Tickets")
        )
    );
  }

}