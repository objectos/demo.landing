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
package demo.landing.dev;

import demo.landing.app.Kino;
import objectos.way.Css;
import objectos.way.Html;

@Css.Source
final class DevView extends Html.Template {

  private final Html.Component head;

  @SuppressWarnings("unused")
  private final Html.Component demo;

  public DevView(Html.Component head, Html.Component demo) {
    this.head = head;

    this.demo = demo;
  }

  @Override
  protected final void render() {
    doctype();

    html(
        css("""
        background-color:var(--color-html)
        color:var(--color-text)
        min-height:100vh
        width:100%
        """),

        lang("en"),

        head(
            meta(charset("utf-8")),
            meta(httpEquiv("content-type"), content("text/html; charset=utf-8")),
            meta(name("viewport"), content("width=device-width, initial-scale=1")),
            link(rel("shortcut icon"), type("image/vnd.microsoft.icon"), href("/images/favicon.ico")),
            link(rel("preload"), href("/ui/fonts.css"), as("style")),
            link(rel("stylesheet"), type("text/css"), href("/ui/fonts.css")),
            c(head)
        ),

        renderBody()
    );
  }

  private Html.Instruction.OfElement renderBody() {
    return body(
        css("""
        background-color:var(--color-body)
        min-height:100vh
        width:100%
        """),

        onload(Kino.ONLOAD),

        hero(),

        demo()
    );
  }

  private static final Html.ClassName SECTION = Html.ClassName.ofText("""
  border-bottom:1px_solid_var(--color-border)
  """);

  private static final Html.ClassName CONTAINER = Html.ClassName.ofText("""
  max-width:calc(var(--breakpoint-x2)_-_66rx)

  sm/border-left:1px_solid_var(--color-border)
  sm/border-right:1px_solid_var(--color-border)
  sm/margin:0_32rx

  x2/margin:0_auto
  """);

  private Html.Instruction hero() {
    return section(
        SECTION,

        div(
            CONTAINER,

            css("""
            padding:92rx_32rx
            """),

            h1(
                css("""
                font-size:48rx
                font-weight:200
                line-height:1

                xl/font-size:48rx

                x2/font-size:60rx
                """),

                text("This website is built entirely using Java")
            ),

            p(
                css("""
                margin-top:28rx

                lg/margin-top:48rx
                """),

                text("""
                Objectos Way allows you to create complete web applications
                using nothing but the Java programming language.
                """)
            )
        )
    );
  }

  private Html.Instruction demo() {
    return section(
        SECTION,

        div(
            CONTAINER,

            css("""
            padding:56rx_32rx
            """),

            h2(
                css("""
                font-size:20rx
                font-weight:600
                """),

                text("Live Objectos Way Demo")
            ),

            p(
                css("""
                font-size:14rx
                padding:20rx_0_32rx
                """),

                text("""
                This demo is written entirely in Java
                using Objectos Way, JDK 23 and the H2 database engine.
                """),

                br(css("display:none md/display:inline")),

                text("""
                The main panel displays the application itself,
                while the secondary panel shows the source code used to generate the view.
                """)
            ),

            div(
                Kino.SHELL,

                text("Loading")
            )
        )
    );
  }

}