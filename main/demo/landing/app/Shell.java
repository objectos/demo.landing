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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import objectos.way.Html;

/// The demo UI shell responsible for displaying the application on the
/// top/right and the source code on the bottom/left.
final class Shell extends Kino.View {

  static final class Builder {

    Html.Component app;

    private SourceModel[] sources;

    private Builder() {}

    final void sources(SourceModel... values) {
      sources = values.clone();
    }

  }

  static final Html.Id SRC = Html.Id.of("source-frame");

  static final Html.AttributeName BTN = Html.AttributeName.of("data-button");

  static final Html.AttributeName PNL = Html.AttributeName.of("data-panel");

  static final Html.AttributeName SEL = Html.AttributeName.of("data-selected");

  private final Html.Component main;

  private final Html.Component sourceCode;

  private final Html.Component sourceSelector;

  Shell(Html.Component mainView, SourceModel... more) {
    main = new ShellMain(mainView);

    final List<SourceModel> sources;
    sources = new ArrayList<>();

    for (SourceModel item : more) {
      sources.add(item);
    }

    sources.add(Source.Kino);
    sources.add(Source.Shell);
    sources.add(Source.ShellMain);
    sources.add(Source.ShellSourceCode);
    sources.add(Source.ShellSourceSelector);
    sources.add(Source.SourceModel_);

    sourceCode = new ShellSourceCode(sources);

    sourceSelector = new ShellSourceSelector(sources);
  }

  private Shell(Builder builder) {
    this(builder.app, builder.sources);
  }

  public static Shell create(Consumer<Builder> config) {
    final Builder builder;
    builder = new Builder();

    config.accept(builder);

    return new Shell(builder);
  }

  @Override
  protected final void render() {
    // the demo shell UI
    div(
        Kino.SHELL,

        css("""
        display:grid
        grid-template:'a'_448rx_'b'_auto_'c'_448rx_/_1fr

        lg/grid-template:'c_a'_512rx_'b_b'_auto_/_1fr_1fr

        xl/grid-template:'b_c_a'_512rx_/_200rx_1fr_1fr
        """),

        c(main),

        c(sourceSelector),

        c(sourceCode)
    );
  }

}