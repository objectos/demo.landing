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
import java.util.Objects;
import java.util.function.Consumer;
import objectos.way.Css;
import objectos.way.Html;
import objectos.way.Syntax;

/**
 * The demo UI shell responsible for displaying the application on the
 * top/right and the source code on the bottom/left.
 */
@Css.Source // Indicates to the CSS Generator that it should scan this class for CSS utilities.
final class Shell extends Kino.View {

  static final class Builder {

    String appFrame;

    Html.Component app;

    String sourceFrame;

    private final List<SourceModel> sources = new ArrayList<>();

    private Builder() {}

    final void sources(SourceModel... values) {
      for (SourceModel value : values) {
        sources.add(value);
      }
    }

    private Shell build() {
      Objects.requireNonNull(appFrame, "appFrame == null");
      Objects.requireNonNull(app, "app == null");
      Objects.requireNonNull(sourceFrame, "sourceFrame == null");

      sources.add(Source.Kino);
      sources.add(Source.Shell);
      sources.add(Source.SourceModel_);

      return new Shell(this);
    }

  }

  static final Html.Id APP = Html.Id.of("demo-app");

  private final Builder builder;

  private Shell(Builder builder) {
    this.builder = builder;
  }

  public static Shell create(Consumer<Builder> config) {
    final Builder builder;
    builder = new Builder();

    config.accept(builder);

    return builder.build();
  }

  private final Html.Id sourceFrame = Html.Id.of("source-frame");

  private final Html.AttributeName dataButton = Html.AttributeName.of("data-button");

  private final Html.AttributeName dataPanel = Html.AttributeName.of("data-panel");

  private final Html.AttributeName dataSelected = Html.AttributeName.of("data-selected");

  @Override
  protected final void render() {
    // the demo container
    div(
        css("""
        display:grid
        grid-template:'a'_448rx_'b'_auto_'c'_448rx_/_1fr

        lg:grid-template:'c_a'_512rx_'b_b'_auto_/_1fr_1fr

        xl:grid-template:'b_c_a'_512rx_/_200rx_1fr_1fr
        """),

        f(this::renderApp),

        f(this::renderSourceMenu),

        f(this::renderSourceCode)
    );
  }

  private void renderApp() {
    // the app container
    div(
        APP,

        css("""
        border:1px_solid_border
        overflow:auto
        grid-area:a
        position:relative

        lg:border-bottom-width:1px
        lg:border-left-width:0px
        """),

        div(
            css("""
            align-items:center
            background-color:layer
            color:gray-500
            display:flex
            height:64rx
            justify-content:space-between
            padding:0_16rx
            position:sticky
            top:0px
            z-index:8000
            """),

            a(
                css("""
                align-items:center
                display:flex
                gap:6rx
                height:100%
                """),

                href("/index.html"),

                dataOnClick(this::navigate),

                objectosLogo(),

                span(
                    css("""
                    font-size:24rx
                    font-weight:300
                    line-height:1
                    transform:translateY(-1px)
                    """),

                    text("kino")
                )
            ),

            a(
                css("""
                align-items:center
                border-radius:6rx
                display:flex
                padding:8rx

                active:background-color:btn-ghost-active
                hover:background-color:btn-ghost-hover
                """),

                href("https://github.com/objectos/demo.landing"),

                gitHubLogo()
            )
        ),

        div(
            css("""
            padding:0_16rx_16rx

            h2:font-size:36rx
            h2:font-weight:200
            h2:line-height:1
            h2:padding:48rx_0_8rx
            """),

            dataFrame("demo-app", builder.appFrame),

            c(builder.app)
        )
    );
  }

  private Html.Instruction objectosLogo() {
    return svg(
        css("""
        width:auto
        height:24rx

        fill:logo
        transition:fill_300ms_ease
        """),

        xmlns("http://www.w3.org/2000/svg"), width("200"), height("49.28"), viewBox("0 0 200 49.28"),

        path(
            d("m189.6 38.21q-2.53 0-5.3-0.932-2.76-0.903-4.6-3.087-0.38-0.495-0.35-1.02 0.12-0.582 0.64-0.99 0.5-0.32 1.02-0.233 0.58 0.09 0.93 0.524 1.4 1.66 3.38 2.33 2.04 0.641 4.43 0.641 4.08 0 5.77-1.456 1.71-1.456 1.71-3.408 0-1.893-1.86-3.087-1.78-1.281-5.56-1.806-4.87-0.669-7.2-2.621-2.33-1.951-2.33-4.514 0-2.417 1.23-4.077 1.19-1.689 3.29-2.534 2.12-0.874 4.86-0.874 3.29 0 5.56 1.223 2.31 1.165 3.7 3.146 0.38 0.495 0.24 1.077-0.1 0.525-0.73 0.874-0.47 0.233-1.02 0.146-0.53-0.09-0.9-0.583-1.23-1.543-2.97-2.33-1.69-0.815-3.99-0.815-3.06 0-4.75 1.31-1.69 1.311-1.69 3.146 0 1.252 0.67 2.242 0.73 0.903 2.33 1.602 1.6 0.612 4.28 1.02 3.64 0.466 5.71 1.63 2.15 1.165 3.03 2.738 0.9 1.485 0.9 3.233 0 2.301-1.46 3.99-1.45 1.689-3.81 2.621-2.39 0.874-5.16 0.874zm-27.97 0q-3.9 0-6.99-1.748-3.05-1.805-4.85-4.863-1.75-3.088-1.75-6.932 0-3.873 1.75-6.931 1.8-3.117 4.85-4.864 3.09-1.806 6.99-1.806 3.89 0 6.95 1.806 3.05 1.747 4.8 4.864 1.81 3.058 1.81 6.931 0 3.844-1.81 6.932-1.75 3.058-4.8 4.863-3.06 1.748-6.95 1.748zm0-2.709q3.07 0 5.43-1.427 2.45-1.456 3.79-3.873 1.42-2.476 1.42-5.592 0-3.058-1.42-5.475-1.34-2.476-3.79-3.874-2.36-1.456-5.43-1.456-3.03 0-5.45 1.456-2.41 1.398-3.83 3.874-1.4 2.417-1.4 5.533 0 3.058 1.4 5.534 1.42 2.417 3.83 3.873 2.42 1.427 5.45 1.427zm-19.01 2.418q-2.65-0.06-4.75-1.224-2.09-1.194-3.26-3.291-1.16-2.126-1.16-4.805v-24.17q0-0.67 0.4-1.078 0.44-0.436 1.05-0.436 0.7 0 1.08 0.436 0.44 0.408 0.44 1.078v24.17q0 2.825 1.74 4.601 1.75 1.748 4.52 1.748h1.08q0.67 0 1.05 0.437 0.43 0.407 0.43 1.077 0 0.641-0.43 1.078-0.38 0.379-1.05 0.379zm-12.96-22.95q-0.58 0-0.96-0.35-0.35-0.378-0.35-0.961 0-0.582 0.35-0.932 0.38-0.378 0.96-0.378h13.16q0.59 0 0.94 0.378 0.38 0.35 0.38 0.932 0 0.583-0.38 0.961-0.35 0.35-0.94 0.35zm-13.09 23.24q-3.78 0-6.79-1.806-2.96-1.776-4.71-4.834-1.7-3.059-1.7-6.903 0-3.873 1.6-6.931t4.42-4.806q2.81-1.806 6.45-1.806 3.1 0 5.7 1.224 2.56 1.165 4.45 3.582 0.38 0.495 0.29 1.019-0.1 0.525-0.64 0.874-0.44 0.349-0.96 0.291-0.52-0.09-0.93-0.582-3.09-3.699-7.91-3.699-2.86 0-5.04 1.427-2.14 1.398-3.35 3.815-1.17 2.447-1.17 5.592 0 3.058 1.31 5.534 1.31 2.417 3.59 3.873 2.33 1.427 5.39 1.427 1.99 0 3.74-0.582 1.81-0.583 3.12-1.806 0.44-0.379 0.96-0.437 0.52-0.06 0.93 0.35 0.47 0.437 0.47 1.019 0.1 0.524-0.38 0.903-3.55 3.262-8.84 3.262zm-27.69-0.06q-3.84 0-6.85-1.69-2.96-1.747-4.66-4.805t-1.7-6.99q0-3.99 1.61-6.99 1.6-3.058 4.41-4.805 2.82-1.748 6.46-1.748 3.59 0 6.36 1.69 2.76 1.66 4.31 4.63 1.56 2.913 1.56 6.728 0 0.641-0.39 1.019-0.39 0.35-1.02 0.35h-21.35v-2.534h22.13l-2.14 1.602q0.1-3.146-1.07-5.563-1.16-2.446-3.35-3.786-2.13-1.427-5.04-1.427-2.77 0-4.95 1.427-2.14 1.34-3.4 3.786-1.21 2.417-1.21 5.621 0 3.146 1.31 5.592 1.31 2.417 3.64 3.815 2.33 1.369 5.34 1.369 1.89 0 3.78-0.641 1.94-0.67 3.06-1.747 0.39-0.379 0.92-0.379 0.58-0.06 0.97 0.291 0.54 0.437 0.54 0.962 0 0.553-0.44 0.99-1.55 1.398-4.08 2.33-2.47 0.903-4.75 0.903zm-30.93 11.13q-0.64 0-1.07-0.44-0.44-0.38-0.44-1.02 0-0.67 0.44-1.1 0.43-0.41 1.07-0.41 2.47 0 4.31-1.05 1.9-1.08 2.97-2.97 1.06-1.89 1.06-4.313v-25.16q0-0.67 0.39-1.049 0.44-0.408 1.07-0.408 0.68 0 1.07 0.408 0.43 0.379 0.43 1.049v25.16q0 3.291-1.45 5.821-1.46 2.57-4.03 4.02-2.52 1.46-5.82 1.46zm9.75-43.48q-0.92 0-1.6-0.641-0.63-0.67-0.63-1.66 0-1.107 0.68-1.631 0.73-0.582 1.6-0.582 0.82 0 1.5 0.582 0.73 0.524 0.73 1.631 0 0.99-0.68 1.66-0.63 0.641-1.6 0.641zm-21.55 32.42q-3.79 0-6.84-1.748-3.06-1.747-4.86-4.747-1.75-3.029-1.84-6.815v-23.44q0-0.67 0.39-1.048 0.43-0.408 1.06-0.408 0.68 0 1.07 0.408 0.39 0.378 0.39 1.048v15.14q1.55-2.505 4.32-4.019 2.82-1.515 6.31-1.515 3.88 0 6.94 1.806 3.11 1.747 4.85 4.805 1.8 3.058 1.8 6.932 0 3.902-1.8 6.99-1.74 3.058-4.85 4.863-3.06 1.748-6.94 1.748zm0-2.709q3.06 0 5.44-1.427 2.42-1.456 3.83-3.873 1.41-2.476 1.41-5.592 0-3.087-1.41-5.534-1.41-2.417-3.83-3.815-2.38-1.456-5.44-1.456-3.01 0-5.44 1.456-2.42 1.398-3.83 3.815-1.36 2.447-1.36 5.534 0 3.116 1.36 5.592 1.41 2.417 3.83 3.873 2.43 1.427 5.44 1.427zm-32.53 2.709q-3.88 0-6.99-1.748-3.06-1.805-4.85-4.863-1.75-3.088-1.75-6.932 0-3.873 1.75-6.931 1.79-3.117 4.85-4.864 3.11-1.806 6.99-1.806t6.94 1.806q3.06 1.747 4.8 4.864 1.8 3.058 1.8 6.931 0 3.844-1.8 6.932-1.74 3.058-4.8 4.863-3.06 1.748-6.94 1.748zm0-2.709q3.06 0 5.44-1.427 2.42-1.456 3.78-3.873 1.41-2.476 1.41-5.592 0-3.058-1.41-5.475-1.36-2.476-3.78-3.874-2.38-1.456-5.44-1.456-3.01 0-5.44 1.456-2.42 1.398-3.83 3.874-1.41 2.417-1.41 5.533 0 3.058 1.41 5.534 1.41 2.417 3.83 3.873 2.43 1.427 5.44 1.427z"),
            strokeWidth(".9101")
        )
    );
  }

  private Html.Instruction gitHubLogo() {
    return svg(
        css("""
        width:auto
        height:24rx

        fill:logo
        """),

        xmlns("http://www.w3.org/2000/svg"), width("98"), height("96"), viewBox("0 0 98 96"),

        path(
            fillRule("evenodd"), clipRule("evenodd"), d(
                "M48.854 0C21.839 0 0 22 0 49.217c0 21.756 13.993 40.172 33.405 46.69 2.427.49 3.316-1.059 3.316-2.362 0-1.141-.08-5.052-.08-9.127-13.59 2.934-16.42-5.867-16.42-5.867-2.184-5.704-5.42-7.17-5.42-7.17-4.448-3.015.324-3.015.324-3.015 4.934.326 7.523 5.052 7.523 5.052 4.367 7.496 11.404 5.378 14.235 4.074.404-3.178 1.699-5.378 3.074-6.6-10.839-1.141-22.243-5.378-22.243-24.283 0-5.378 1.94-9.778 5.014-13.2-.485-1.222-2.184-6.275.486-13.038 0 0 4.125-1.304 13.426 5.052a46.97 46.97 0 0 1 12.214-1.63c4.125 0 8.33.571 12.213 1.63 9.302-6.356 13.427-5.052 13.427-5.052 2.67 6.763.97 11.816.485 13.038 3.155 3.422 5.015 7.822 5.015 13.2 0 18.905-11.404 23.06-22.324 24.283 1.78 1.548 3.316 4.481 3.316 9.126 0 6.6-.08 11.897-.08 13.526 0 1.304.89 2.853 3.316 2.364 19.412-6.52 33.405-24.935 33.405-46.691C97.707 22 75.788 0 48.854 0z")
        )
    );
  }

  private void renderSourceMenu() {
    final List<SourceModel> items;
    items = builder.sources;

    final SourceModel first;
    first = items.getFirst();

    // the source file selector
    div(
        sourceFrame,

        css("""
        border-left:1px_solid_border
        border-right:1px_solid_border
        display:flex
        font-size:14rx
        gap:4rx_8rx
        grid-area:b
        padding:16rx
        overflow-x:auto

        lg:border-bottom:1px_solid_border

        xl:border-top:1px_solid_border
        xl:border-right-width:0px
        xl:flex-direction:column
        """),

        dataFrame("demo-source-menu", builder.sourceFrame),

        // stores the current selected button in the data-button attribute
        attr(dataButton, first.button().attrValue()),

        // stores the current selected panel in the data-panel attribute
        attr(dataPanel, first.panel().attrValue()),

        f(this::renderSourceMenuItems)
    );
  }

  private void renderSourceMenuItems() {
    final List<SourceModel> items;
    items = builder.sources;

    for (int idx = 0, size = items.size(); idx < size; idx++) {
      final SourceModel item;
      item = items.get(idx);

      button(
          item.button(),

          css("""
          border-radius:6rx
          cursor:pointer
          padding:4rx_8rx
          [data-selected=true]:background-color:btn-ghost-active

          active:background-color:btn-ghost-active
          hover:background-color:btn-ghost-hover
          """),

          attr(dataSelected, Boolean.toString(idx == 0)),

          dataOnClick(script -> {
            // 'deselects' current
            var frame = script.elementById(sourceFrame);

            var selectedButton = script.elementById(frame.attr(dataButton));

            selectedButton.attr(dataSelected, "false");

            var selectedPanel = script.elementById(frame.attr(dataPanel));

            selectedPanel.attr(dataSelected, "false");

            // 'selects' self
            var selfButton = script.elementById(item.button());

            selfButton.attr(dataSelected, "true");

            var selfPanel = script.elementById(item.panel());

            selfPanel.attr(dataSelected, "true");

            // stores selected
            frame.attr(dataButton, item.button().attrValue());

            frame.attr(dataPanel, item.panel().attrValue());
          }),

          text(item.name())
      );
    }
  }

  private void renderSourceCode() {
    // the Java source code display
    div(
        css("""
        border:1px_solid_border
        display:flex
        flex-direction:column
        grid-area:c
        """),

        css("""
        flex:1
        min-height:0
        overflow:auto
        """),

        dataFrame("demo-source-code", builder.sourceFrame),

        f(this::renderSourceCodeItems)
    );
  }

  private void renderSourceCodeItems() {
    final List<SourceModel> items;
    items = builder.sources;

    for (int idx = 0, size = items.size(); idx < size; idx++) {
      final SourceModel item;
      item = items.get(idx);

      final String source;
      source = item.value();

      pre(
          item.panel(),

          css("""
          display:none
          font-family:mono
          font-size:13rx
          line-height:18.6rx
          padding:16rx
          [data-selected=true]:display:flex

          span:[data-line]:display:block
          span:[data-line]:min-height:1lh

          span:[data-line]:nth-child(-n+15):display:none

          span:[data-high=annotation]:color:high-meta
          span:[data-high=comment]:color:high-comment
          span:[data-high=comment]:font-style:italic
          span:[data-high=keyword]:color:high-keyword
          span:[data-high=string]:color:high-string
          """),

          attr(dataSelected, Boolean.toString(idx == 0)),

          code(
              css("""
              flex-grow:1
              """),

              c(
                  Syntax.highlight(Syntax.JAVA, source)
              )
          )
      );
    }
  }

}