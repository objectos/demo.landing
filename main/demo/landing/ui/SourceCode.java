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
package demo.landing.ui;

import module java.base;
import module objectos.way;

/// Renders the source code of the selected file.
final class SourceCode extends Html.Template {

  private final List<SourceModel> sources;

  SourceCode(List<SourceModel> sources) {
    this.sources = sources;
  }

  @Override
  protected final void render() {
    div(
        css("""
        border:1px_solid_var(--color-border)
        display:flex
        flex-direction:column
        grid-area:c
        """),

        css("""
        flex:1
        min-height:0
        overflow:auto
        """),

        f(this::renderSourceCodeItems)
    );
  }

  private void renderSourceCodeItems() {
    for (int idx = 0, size = sources.size(); idx < size; idx++) {
      final SourceModel item;
      item = sources.get(idx);

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
          &[data-selected=true]/display:flex

          &_span[data-line]/display:block
          &_span[data-line]/min-height:1lh

          &_span[data-line]:nth-child(-n+15)/display:none

          &_span[data-high=annotation]/color:var(--color-high-meta)
          &_span[data-high=comment]/color:var(--color-high-comment)
          &_span[data-high=comment]/font-style:italic
          &_span[data-high=keyword]/color:var(--color-high-keyword)
          &_span[data-high=string]/color:var(--color-high-string)
          """),

          attr(Shell.SEL, Boolean.toString(idx == 0)),

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
