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
package demo.landing.home;

import module objectos.way;

/// Renders the "Now Showing" header.
final class Header extends Html.Template {

  @Override
  protected final void render() {
    div(
        css("""
        margin-bottom:32rx
        """),

        h2(
            text("Now Showing")
        ),

        p(
            text("Please choose a movie")
        )
    );
  }

}