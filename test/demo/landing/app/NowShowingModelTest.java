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

import static org.testng.Assert.assertEquals;

import java.util.List;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(Testing.class)
public class NowShowingModelTest {

  private final String data = """
  insert into MOVIE (MOVIE_ID, TITLE, SYNOPSYS, RUNTIME, RELEASE_DATE)
  values (11, 'Title 1', 'Synopsys 1', 120, '2025-01-10')
  ,      (12, 'Title 2', 'Synopsys 2', 150, '2025-01-20');
  """;

  @Test
  public void testCase01() {
    Testing.rollback(trx -> {
      Testing.load(trx, data);

      final List<NowShowingModel> items;
      items = NowShowingModel.query(trx);

      assertEquals(items.size(), 2);

      final NowShowingModel item0;
      item0 = items.get(0);

      assertEquals(item0.id(), 11);
      assertEquals(item0.title(), "Title 1");

      final NowShowingModel item1;
      item1 = items.get(1);

      assertEquals(item1.id(), 12);
      assertEquals(item1.title(), "Title 2");
    });
  }

}