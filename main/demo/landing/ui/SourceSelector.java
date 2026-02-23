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

/// Renders the source file selector.
final class SourceSelector extends Html.Template {

  private final List<SourceModel> sources;

  SourceSelector(List<SourceModel> sources) {
    this.sources = sources;
  }

  @Override
  protected final void render() {
    final SourceModel first;
    first = sources.getFirst();

    // the source file selector
    div(
        Shell.SRC,

        css("""
        border-left:1px_solid_var(--color-border)
        border-right:1px_solid_var(--color-border)
        display:flex
        font-size:14rx
        gap:4rx_8rx
        grid-area:b
        padding:16rx
        overflow-x:auto

        lg/border-bottom:1px_solid_var(--color-border)

        xl/border-top:1px_solid_var(--color-border)
        xl/border-right-width:0px
        xl/flex-direction:column
        """),

        // stores the current selected button in the data-button attribute
        attr(Shell.BTN, first.button().attrValue()),

        // stores the current selected panel in the data-panel attribute
        attr(Shell.PNL, first.panel().attrValue()),

        f(this::renderSourceMenuItems)
    );
  }

  private void renderSourceMenuItems() {
    for (int idx = 0, size = sources.size(); idx < size; idx++) {
      final SourceModel item;
      item = sources.get(idx);

      button(
          item.button(),

          css("""
          border-radius:6rx
          cursor:pointer
          padding:4rx_8rx
          &[data-selected=true]/background-color:var(--color-btn-ghost-active)

          active/background-color:var(--color-btn-ghost-active)
          hover/background-color:var(--color-btn-ghost-hover)
          """),

          attr(Shell.SEL, Boolean.toString(idx == 0)),

          onclick(Js.of(
              // 'stores' the Shell.SRC element at the 'frame' variable
              Js.var("frame", Js.byId(Shell.SRC)),
              // 'deselects' current
              Js.byId(Js.var("frame").as(JsElement.type).attr(Shell.BTN)).attr(Shell.SEL, "false"),
              Js.byId(Js.var("frame").as(JsElement.type).attr(Shell.PNL)).attr(Shell.SEL, "false"),
              // 'selects' self
              Js.byId(item.button()).attr(Shell.SEL, "true"),
              Js.byId(item.panel()).attr(Shell.SEL, "true"),
              // stores selected,
              Js.var("frame").as(JsElement.type).attr(Shell.BTN, item.button().attrValue()),
              Js.var("frame").as(JsElement.type).attr(Shell.PNL, item.panel().attrValue())
          )),

          text(item.name())
      );
    }
  }

}
