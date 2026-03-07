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

import java.time.LocalDateTime;
import objectos.way.Http;
import objectos.way.Sql;

record ConfirmData(AppReservation reservation) {

  static ConfirmData parse(Http.Exchange http) {
    final AppReservation reservation;
    reservation = AppReservation.parse(http);

    return new ConfirmData(reservation);
  }

  public final Sql.Update persistTicket(Sql.Transaction trx, LocalDateTime today) {
    trx.sql("""
    update
      RESERVATION
    set
      TICKET_TIME = ?
    where
      RESERVATION_ID = ?
      and TICKET_TIME is null
    """);

    trx.param(today);

    trx.param(reservation.id());

    return trx.updateWithResult();
  }

}