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

/// Alerts the user that invalid data was submitted.
enum SeatsAlert {

  EMPTY("""
  Kindly choose at least 1 seat."""),

  LIMIT("""
  We regret to inform you that we limit purchases to 6 tickets per person. \
  Kindly choose at most 6 seats."""),

  BOOKED("""
  We regret to inform you that another customer has already reserved one or more of the selected seats. \
  Kindly choose alternative seats.""");

  final String msg;

  private SeatsAlert(String msg) {
    this.msg = msg;
  }

  private static final SeatsAlert[] VALUES = SeatsAlert.values();

  static SeatsAlert of(int id) {
    final int ordinal;
    ordinal = id - 1;

    if (0 <= ordinal && ordinal < VALUES.length) {
      return VALUES[ordinal];
    } else {
      return null;
    }
  }

  public final int id() {
    return ordinal() + 1;
  }

}