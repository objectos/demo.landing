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

import demo.landing.app.AppCtx;
import module java.base;
import module objectos.way;

/// Exposes the demo application to the host application.
@App.DoNotReload
public interface LandingDemo {

  /// Configures the creation of a demo application.
  interface Options {

    void clock(Clock value);

    void codecKey(byte[] value);

    void database(Sql.Database value);

    void noteSink(Note.Sink value);

    void reservationEpoch(Instant value);

    void reservationRandom(RandomGenerator value);

    void testing();

  }

  /// Creates a new demo instance with the specified options.
  ///
  /// @param opts allows for setting the options
  ///
  /// @return a newly created demo instance
  static LandingDemo create(Consumer<? super Options> opts) {
    return AppCtx.create(opts);
  }

  Http.Routing.Module localRoutes();

  Http.Routing.Module publicRoutes();

  Css.Library styles();

}
