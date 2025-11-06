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

import demo.landing.app.Kino;
import java.util.Objects;
import objectos.way.App;
import objectos.way.Css;
import objectos.way.Html;
import objectos.way.Http;

/**
 * Exposes the demo application to the host application.
 */
@App.DoNotReload
public interface LandingDemo {

  /**
   * Represents the stage this demo is running on.
   */
  @App.DoNotReload
  public enum Stage {

    DEFAULT,

    TESTING;

  }

  /**
   * The result of a POST request in the context of the demo application.
   */
  @App.DoNotReload
  sealed interface PostResult {}

  /**
   * Indicates that the host application should embed this result.
   */
  @App.DoNotReload
  static final class Embed implements PostResult {

    private final Http.Status status;

    private final Html.Component component;

    private Embed(Http.Status status, Html.Component component) {
      this.status = status;

      this.component = component;
    }

    public final Http.Status status() { return status; }

    public final Html.Component get() { return component; }

  }

  static Embed embedOk(Html.Component component) {
    return embed(Http.Status.OK, component);
  }

  static Embed embedBadRequest(Html.Component component) {
    return embed(Http.Status.BAD_REQUEST, component);
  }

  private static Embed embed(Http.Status status, Html.Component component) {
    Objects.requireNonNull(status, "status == null");
    Objects.requireNonNull(component, "component == null");

    return new Embed(status, component);
  }

  /**
   * Indicates that the host application should perform a redirect to the
   * specified location.
   */
  @App.DoNotReload
  static final class Redirect implements PostResult {

    private final String location;

    private Redirect(String location) {
      this.location = location;
    }

    public final String get() {
      return location;
    }

  }

  static Redirect redirect(String location) {
    Objects.requireNonNull(location, "location == null");

    return new Redirect(location);
  }

  static LandingDemo create(LandingDemoConfig config) {
    return Kino.create(config);
  }

  static Css.Library styles() {
    return Kino.styles();
  }

  Html.Component get(Http.Exchange http);

  PostResult post(Http.Exchange http);

}
