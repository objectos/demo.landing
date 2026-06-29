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
package demo.landing;

import module java.base;
import module objectos.way;
import objectos.http.Handler;

/**
 * Starts the application in development mode.
 */
public final class StartDev extends Start {

  private StartDev() {}

  public static void main(String[] args) {
    new StartDev().start(args);
  }

  @Override
  final App.NoteSink noteSink() {
    return App.NoteSink.sysout();
  }

  @Override
  final LandingDemo demo(App.Injector injector) {
    return LandingDemo.create(opts -> {
      final HexFormat hexFormat;
      hexFormat = HexFormat.of();

      final byte[] codecKey;
      codecKey = hexFormat.parseHex("7b9e2a4f6c8d1e3b5a0f7d9c4e2b6a8f1d3c5e7b9a0f2d4c6e8b1a3f5c7d9e0b");

      opts.codecKey(codecKey);

      final Sql.Database database;
      database = injector.getInstance(Sql.Database.class);

      opts.database(database);

      final Note.Sink noteSink;
      noteSink = injector.getInstance(Note.Sink.class);

      opts.noteSink(noteSink);
    });
  }

  private record Reloader(App.Injector injector) implements ReloadingFunction {
    @SuppressWarnings("unchecked")
    @Override
    public final Handler reload(ClassLoader loader) throws Exception {
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

      final Consumer<Routing> module;
      module = (Consumer<Routing>) instance;

      return Handler.create(module);
    }
  }

  @Override
  final Handler serverHandler(App.Injector injector) {
    try {
      return ReloadingHandler.create(opts -> {
        opts.moduleOf(StartDev.class);

        final Note.Sink noteSink;
        noteSink = injector.getInstance(Note.Sink.class);

        opts.noteSink(noteSink);

        opts.reloadingFunction(new Reloader(injector));
      });
    } catch (IOException e) {
      throw App.serviceFailed("ReloadingHandler", e);
    }
  }

  @Override
  final int serverPort() {
    return DEVELOPMENT_HTTP_PORT;
  }

}
