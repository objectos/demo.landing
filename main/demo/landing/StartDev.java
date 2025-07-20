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
package demo.landing;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.file.Path;
import java.util.HexFormat;
import objectos.way.App;
import objectos.way.Css;
import objectos.way.Http;
import objectos.way.Note;
import objectos.way.Sql;

/**
 * Starts the application in development mode.
 */
public final class StartDev extends Start {

  private final Path classOutput = Path.of("work", "main");

  private StartDev() {}

  public static void main(String[] args) {
    new StartDev().start(args);
  }

  @Override
  final App.NoteSink noteSink() {
    return App.NoteSink.sysout();
  }

  @Override
  final LandingDemoConfig config(App.Injector injector) {
    return LandingDemoConfig.create(config -> {
      final HexFormat hexFormat;
      hexFormat = HexFormat.of();

      final byte[] codecKey;
      codecKey = hexFormat.parseHex("7b9e2a4f6c8d1e3b5a0f7d9c4e2b6a8f1d3c5e7b9a0f2d4c6e8b1a3f5c7d9e0b");

      config.codecKey(codecKey);

      final Sql.Database database;
      database = injector.getInstance(Sql.Database.class);

      config.database(database);

      final Note.Sink noteSink;
      noteSink = injector.getInstance(Note.Sink.class);

      config.noteSink(noteSink);
    });
  }

  @Override
  final void injector(App.Injector.Options opts) {
    super.injector(opts);

    final Note.Sink noteSink;
    noteSink = opts.getInstance(Note.Sink.class);

    final Css.StyleSheet css;
    css = css(noteSink, classOutput);

    opts.putInstance(Css.StyleSheet.class, css);
  }

  private record Reloader(App.Injector injector) implements App.Reloader.HandlerFactory {
    @Override
    public final Http.Handler reload(ClassLoader loader) throws Exception {
      final Class<?> bootClass;
      bootClass = loader.loadClass("demo.landing.BootModule");

      final Constructor<?> constructor;
      constructor = bootClass.getConstructor(App.Injector.class, Module.class);

      final Class<? extends Reloader> self;
      self = getClass();

      final Module original;
      original = self.getModule();

      final Object instance;
      instance = constructor.newInstance(injector, original);

      final Http.Routing.Module module;
      module = (Http.Routing.Module) instance;

      return Http.Handler.of(module);
    }
  }

  @Override
  final Http.Handler serverHandler(App.Injector injector) {
    try {
      return App.Reloader.create(opts -> {
        opts.handlerFactory(new Reloader(injector));

        opts.moduleOf(StartDev.class);

        final Note.Sink noteSink;
        noteSink = injector.getInstance(Note.Sink.class);

        opts.noteSink(noteSink);
      });
    } catch (IOException e) {
      throw App.serviceFailed("App.Reloader", e);
    }
  }

  @Override
  final int serverPort() {
    return DEVELOPMENT_HTTP_PORT;
  }

}
