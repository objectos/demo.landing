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

import module objectos.way;

final class NotFound implements Kino.GET, Kino.POST, Http.Handler {

  public static Html.Component create() {
    return Shell.create(shell -> {
      shell.app = new NotFoundView();

      shell.sources(
        //          Source.NotFound,
      //          Source.NotFoundView
      );
    });
  }

  @Override
  public final void handle(Http.Exchange http) {
    http.notFound(
        new Shell(
            new NotFoundView()

        //            Source.NotFound,
        //            Source.NotFoundView
        )
    );
  }

  @Override
  public final Html.Component get(Http.Exchange http) {
    return create();
  }

  @Override
  public final Kino.PostResult post(Http.Exchange http) {
    throw new UnsupportedOperationException("Implement me");
  }

}