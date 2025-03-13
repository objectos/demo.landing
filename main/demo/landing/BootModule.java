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

import demo.landing.dev.DevModule;
import demo.landing.local.LocalModule;
import objectos.way.App;
import objectos.way.Http;

public final class BootModule extends Http.Module {

  private final App.Injector injector;

  public BootModule(App.Injector injector) {
    this.injector = injector;
  }

  @Override
  protected final void configure() {
    install(
        new DevModule(injector)
    );

    final LandingDemoConfig config;
    config = injector.getInstance(LandingDemoConfig.class);

    install(
        new LocalModule(config)
    );
  }

}