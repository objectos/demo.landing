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

/// UI component for selecting and displaying the source code of a Java file.
public final class UiSources extends Html.Template {

  private final UiSourceCode code;

  private final UiSourceSelector selector;

  private UiSources(UiSourceCode code, UiSourceSelector selector) {
    this.code = code;

    this.selector = selector;
  }

  public static UiSources of(SourceModel... more) {
    final List<SourceModel> sources;
    sources = new ArrayList<>();

    for (SourceModel item : more) {
      sources.add(item);
    }

    sources.add(Source.UiBackLink);
    sources.add(Source.UiContent);
    sources.add(Source.UiIcon);
    sources.add(Source.UiShell);
    sources.add(Source.UiSourceCode);
    sources.add(Source.UiSources);
    sources.add(Source.UiSourceSelector);

    return new UiSources(
        new UiSourceCode(sources),

        new UiSourceSelector(sources)
    );
  }

  @Override
  protected final void render() {
    c(code);

    c(selector);
  }

}
