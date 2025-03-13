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

import objectos.way.Html;

record SourceModel(String name, String value, Html.Id button, Html.Id panel) {

  private static int INDEX = 0;

  public static SourceModel create(String name, String value) {
    final int index;
    index = INDEX++;

    return new SourceModel(
        name,

        value,

        Html.Id.of("src-btn-" + index),

        Html.Id.of("src-panel-" + index)
    );
  }

}