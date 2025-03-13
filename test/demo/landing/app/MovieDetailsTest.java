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

import demo.landing.AbstractTest;
import java.util.Optional;
import org.testng.annotations.Test;

public class MovieDetailsTest extends AbstractTest {

  @Test
  public void testCase01() {
    final Optional<MovieDetails> maybe;
    maybe = MovieDetails.queryOptional(trx, 11);

    assertEquals(maybe.isPresent(), true);

    final MovieDetails result;
    result = maybe.get();

    assertEquals(result.movieId(), 11);
    assertEquals(result.title(), "Title 1");
    assertEquals(result.runtime(), "2h 11m");
    assertEquals(result.releaseDate(), "Jan 10, 2025");
    assertEquals(result.genres(), "Adventure, Drama");
    assertEquals(result.synopsys(), "Synopsys 1");
  }

  @Test
  public void testCase02() {
    final Optional<MovieDetails> maybe;
    maybe = MovieDetails.queryOptional(trx, 33);

    assertEquals(maybe.isPresent(), false);
  }

  @Override
  protected final String testData() {
    return """
    insert into MOVIE (MOVIE_ID, TITLE, SYNOPSYS, RUNTIME, RELEASE_DATE)
    values (11, 'Title 1', 'Synopsys 1', 131, '2025-01-10')
    ,      (12, 'Title 2', 'Synopsys 2', 150, '2025-01-20');

    insert into GENRE (GENRE_ID, NAME)
    values (21, 'Action')
    ,      (22, 'Adventure')
    ,      (23, 'Comedy')
    ,      (24, 'Drama');

    insert into MOVIE_GENRE (MOVIE_ID, GENRE_ID)
    values (11, 24) /* Drama */
    ,      (11, 22) /* Adventure */
    ,      (12, 23) /* Comedy */;
    """;
  }

}