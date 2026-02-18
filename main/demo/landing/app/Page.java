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

import demo.landing.app.Kino.Query;
import java.util.Map;
import objectos.way.Http;

/**
 * The pages of this application.
 *
 * <p>
 * As a reminder, as this application will be embedded in another one, it does
 * not have actual pages.
 */
enum Page {

  NOW_SHOWING,

  MOVIE,

  SEATS,

  CONFIRM,

  TICKET,

  BAD_REQUEST;

  private static final Map<String, Page> Q = Map.of(
      "N", NOW_SHOWING,
      "M", MOVIE,
      "S", SEATS,
      "C", CONFIRM,
      "T", TICKET,
      "B", BAD_REQUEST
  );

  final String key = name().substring(0, 1);

  static Page parse(Http.Exchange http) {
    Page res;
    res = NOW_SHOWING;

    final String q;
    q = http.queryParam("page");

    if (q != null) {
      res = Q.getOrDefault(q, BAD_REQUEST);
    }

    return res;
  }

  final String href() {
    return "/index.html?page=" + key;
  }

  final String hrefId(int value) {
    return href() + "&id=" + value;
  }

  final Query query() {
    return new Query(this, 0L, 0);
  }

  final Query query(long id) {
    return new Query(this, id, 0);
  }

  final Query query(long id, int aux) {
    return new Query(this, id, aux);
  }

}