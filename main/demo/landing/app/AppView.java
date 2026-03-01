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
import module objectos.way;

/// The views of this application.
enum AppView {

  HOME,

  MOVIE,

  SEATS,

  CONFIRM,

  TICKET,

  NOT_FOUND;

  private static final Map<String, AppView> Q = Map.of(
      "N", HOME,
      "M", MOVIE,
      "S", SEATS,
      "C", CONFIRM,
      "T", TICKET,
      "B", NOT_FOUND
  );

  final String key = name().substring(0, 1);

  static AppView parse(Http.Exchange http) {
    AppView res;
    res = HOME;

    final String q;
    q = http.queryParam("page");

    if (q != null) {
      res = Q.getOrDefault(q, NOT_FOUND);
    }

    return res;
  }

  final String href() {
    return this == HOME
        ? "/index.html"
        : "/index.html?page=" + key;
  }

  final String hrefId(int value) {
    return href() + "&id=" + value;
  }

  final AppUrl query() {
    return new AppUrl(this, 0L, 0);
  }

  final AppUrl query(long id) {
    return new AppUrl(this, id, 0);
  }

  final AppUrl query(long id, int aux) {
    return new AppUrl(this, id, aux);
  }

}