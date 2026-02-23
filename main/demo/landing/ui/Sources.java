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

import java.util.ArrayList;
import java.util.List;
import module objectos.way;

/// UI component for selecting and displaying the source code of a Java file.
public final class Sources extends Html.Template {

  private final SourceCode code;

  private final SourceSelector selector;

  private Sources(SourceCode code, SourceSelector selector) {
    this.code = code;

    this.selector = selector;
  }

  public static Sources of(SourceModel... more) {
    final List<SourceModel> sources;
    sources = new ArrayList<>();

    for (SourceModel item : more) {
      sources.add(item);
    }

    sources.add(Source.BackLinkUi);
    sources.add(Source.IconUi);
    sources.add(Source.MainUi);
    sources.add(Source.ShellUi);
    sources.add(Source.SourceCode);
    sources.add(Source.SourceModel_);
    sources.add(Source.SourceSelector);

    return new Sources(
        new SourceCode(sources),

        new SourceSelector(sources)
    );
  }

  @Override
  protected final void render() {
    c(code);

    c(selector);
  }

}
