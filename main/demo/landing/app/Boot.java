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
package demo.landing.app;

import objectos.http.Handler;
import objectos.http.Redirection;
import objectos.http.Request;
import objectos.http.Result;

/// The `/home` controller.
final class Boot implements Handler {

  private final AppCtx ctx;

  Boot(AppCtx ctx) {
    this.ctx = ctx;
  }

  @Override
  public final Result handle(Request req) {
    final String hashValue;
    hashValue = req.header(AppCtx.DEMO_LOCATION_HASH);

    final String hashRedirect;
    hashRedirect = ctx.decodeHash(hashValue);

    return Redirection.found(hashRedirect);
  }

}