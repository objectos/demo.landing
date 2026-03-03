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

record AppHash(AppView view, AppReservation reservation, int id) {

  public static AppHash of(AppView view) {
    return new AppHash(view, AppReservation.EMPTY, 0);
  }

  public static AppHash of(AppView view, long rid) {
    return of(view, rid, 0);
  }

  public static AppHash of(AppView view, long rid, int id) {
    return new AppHash(view, new AppReservation(rid), id);
  }

}
