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

import module java.base;

/// The views of this application.
enum AppView {

  HOME,

  MOVIE,

  SEATS,

  CONFIRM,

  TICKET,

  NOT_FOUND;

  final String slug = name().toLowerCase(Locale.US);

  final String href(int id, AppReservation reservation) {
    return switch (this) {
      case HOME, NOT_FOUND -> "/demo.landing/" + slug + reservation;

      default -> "/demo.landing/" + slug + "/" + id + reservation;
    };
  }

}