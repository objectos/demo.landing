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
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchService;
import java.util.HexFormat;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import objectos.way.App;
import objectos.way.App.Reloader;
import objectos.way.Http;
import objectos.way.Http.Routing;
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
    return App.NoteSink.OfConsole.create();
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
  final App.Injector injector(App.Injector.Builder ctx) {
    // styles scan directory
    ctx.putInstance(Path.class, classOutput);

    return ctx.build();
  }

  @Override
  final Http.Handler serverHandler(App.Injector injector) {
    // WatchService
    final FileSystem fileSystem;
    fileSystem = FileSystems.getDefault();

    final WatchService watchService;

    try {
      watchService = fileSystem.newWatchService();
    } catch (IOException e) {
      throw App.serviceFailed("WatchService", e);
    }

    final App.ShutdownHook shutdownHook;
    shutdownHook = injector.getInstance(App.ShutdownHook.class);

    shutdownHook.register(watchService);

    // App.Reloader
    App.Reloader reloader;

    try {
      reloader = App.Reloader.create(config -> {
        config.binaryName("demo.landing.BootModule");

        config.watchService(watchService);

        final Note.Sink noteSink;
        noteSink = injector.getInstance(Note.Sink.class);

        config.noteSink(noteSink);

        config.directory(classOutput);
      });

      shutdownHook.register(reloader);
    } catch (IOException e) {
      throw App.serviceFailed("App.Reloader", e);
    }

    return new ReloadingHandlerFactory1(reloader, App.Injector.class, injector);
  }

  @Override
  final int serverPort() {
    return DEVELOPMENT_HTTP_PORT;
  }

  private static class ReloadingHandlerFactory1 implements Http.Handler {

    private final Lock lock = new ReentrantLock();

    private final App.Reloader reloader;

    private final Class<?> type1;

    private final Object value1;

    private Class<?> previousClass;

    private Http.Handler handler;

    public ReloadingHandlerFactory1(Reloader reloader, Class<?> type1, Object value1) {
      this.reloader = reloader;
      this.type1 = type1;
      this.value1 = value1;
    }

    @Override
    public final void handle(Http.Exchange http) {
      lock.lock();
      try {
        Class<?> handlerClass;
        handlerClass = reloader.get();

        if (!handlerClass.equals(previousClass)) {
          previousClass = handlerClass;

          recreate();
        }

        handler.handle(http);
      } catch (Error | RuntimeException e) {
        throw e;
      } catch (Exception e) {
        throw new RuntimeException(e);
      } finally {
        lock.unlock();
      }
    }

    @SuppressWarnings("unchecked")
    private void recreate() throws Exception {
      Constructor<?> constructor;
      constructor = previousClass.getConstructor(type1);

      Object instance;
      instance = constructor.newInstance(value1);

      Consumer<Http.Routing> module;
      module = (Consumer<Routing>) instance;

      handler = Http.Handler.create(module);
    }

  }

}
