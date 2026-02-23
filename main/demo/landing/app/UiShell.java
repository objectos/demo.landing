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

/// The demo UI shell responsible for displaying the application on the
/// top/right and the source code on the bottom/left.
public final class UiShell extends Html.Template {

  public static final Html.Id ID = Html.Id.of("demo.landing");

  static final Html.Id SRC = Html.Id.of("source-frame");

  static final Html.AttributeName BTN = Html.AttributeName.of("data-button");

  static final Html.AttributeName PNL = Html.AttributeName.of("data-panel");

  static final Html.AttributeName SEL = Html.AttributeName.of("data-selected");

  private final UiContent app;

  private final UiSources sources;

  private UiShell(UiContent app, UiSources sources) {
    this.app = app;

    this.sources = sources;
  }

  public static UiShell of(UiContent app, UiSources sources) {
    Objects.requireNonNull(app, "app == null");
    Objects.requireNonNull(sources, "sources == null");

    return new UiShell(app, sources);
  }

  public static JsAction link(String url) {
    return Js.byId(ID).render(url);
  }

  public static final JsAction onload() {
    return Js.byId(ID).render("/demo.landing/home");
  }

  @Override
  protected final void render() {
    // the demo shell UI
    div(
        ID,

        css("""
        display:grid
        grid-template:'a'_448rx_'b'_auto_'c'_448rx_/_1fr

        lg/grid-template:'c_a'_512rx_'b_b'_auto_/_1fr_1fr

        xl/grid-template:'b_c_a'_512rx_/_200rx_1fr_1fr
        """),

        c(app),

        c(sources)
    );
  }

}