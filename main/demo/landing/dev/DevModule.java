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
import java.nio.file.Path;
import java.util.function.Consumer;
import objectos.css.StyleSheet;
import objectos.http.Redirection;
import objectos.http.RequestMethod;
import objectos.http.Routing;
import objectos.http.StaticFile;
import objectos.script.JsLibrary;
import objectos.way.App;
import objectos.way.Html;
import objectos.way.Note;

public final class DevModule implements Consumer<Routing> {

  private final LandingDemo ctx;

  private final Html.Component head;

  private final Note.Sink noteSink;

  public DevModule(App.Injector injector) {
    ctx = injector.getInstance(LandingDemo.class);

    head = injector.getInstance(Html.Component.class);

    noteSink = injector.getInstance(Note.Sink.class);
  }

  @Override
  public final void accept(Routing r) {
    ctx.localRoutes(r);

    ctx.publicRoutes(r);

    r.at("/",
        RequestMethod.GET, Redirection.movedPermanently("/index.html"));

    r.at("/index.html",
        RequestMethod.GET, StaticFile.of(new DevView(head)));

    r.at("/ui/script.js",
        RequestMethod.GET, StaticFile.of(JsLibrary.of()));

    r.at("/ui/styles.css",
        RequestMethod.GET, StaticFile.of(StyleSheet.create(opts -> {
          opts.noteSink(noteSink);

          opts.include(LandingDemo.styles());

          opts.scanDirectory(Path.of("work", "main"));
        })));
  }

}