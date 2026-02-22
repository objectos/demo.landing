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

import java.util.Objects;
import module objectos.way;

final class UiBackLink extends Html.Template {

  private final String url;

  private UiBackLink(String url) {
    this.url = url;
  }

  public static Html.Component of(String url) {
    return new UiBackLink(
        Objects.requireNonNull(url, "url == null")
    );
  }

  @Override
  protected final void render() {
    testableField("back-link", url);

    div(
        css("""
        border-radius:9999px
        padding:6rx
        margin:6rx_0_0_-6rx
        position:absolute

        active/background-color:var(--color-btn-ghost-active)
        hover/background-color:var(--color-btn-ghost-hover)
        hover/cursor:pointer
        """),

        onclick(Kino.link(url)),

        c(
            UiIcon.ARROW_LEFT.css("""
            height:20rx
            width:20rx
            """)
        )
    );
  }

}
