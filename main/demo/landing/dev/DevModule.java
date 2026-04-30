/*
 * Copyright (C) 2024-2026 Objectos Software LTDA.
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

import demo.landing.LandingDemo;
import module java.base;
import module objectos.way;

public final class DevModule implements Consumer<HttpRouting> {

  private final LandingDemo ctx;

  private final Html.Component head;

  private final Note.Sink noteSink;

  public DevModule(App.Injector injector) {
    ctx = injector.getInstance(LandingDemo.class);

    head = injector.getInstance(Html.Component.class);

    noteSink = injector.getInstance(Note.Sink.class);
  }

  @Override
  public final void accept(HttpRouting routing) {
    ctx.localRoutes(routing);

    ctx.publicRoutes(routing);

    routing.path("/", path -> path.GET(http -> http.movedPermanently("/index.html")));

    routing.path("/index.html", path -> path.GET(this::index));

    routing.path("/ui/script.js", path -> path.GET(this::script));

    routing.path("/ui/styles.css", path -> path.GET(this::styles));
  }

  private void index(HttpExchange http) {
    final DevView object;
    object = new DevView(head);

    http.ok(object);
  }

  private void script(HttpExchange http) {
    final JsLibrary library;
    library = JsLibrary.of();

    http.staticFile(library);
  }

  private void styles(HttpExchange http) {
    http.staticFile(StyleSheet.create(opts -> {
      opts.noteSink(noteSink);

      opts.include(LandingDemo.styles());

      opts.scanDirectory(Path.of("work", "main"));
    }));
  }

}