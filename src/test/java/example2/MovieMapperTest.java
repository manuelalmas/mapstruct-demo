package example2;

import example2.domain.Award;
import example2.domain.Director;
import example2.domain.Movie;
import example2.domain.Soundtrack;
import example2.dto.AwardDTO;
import example2.dto.MovieDTO;
import example2.mapper.MovieMapper;
import org.junit.Test;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

public class MovieMapperTest {

    @Test
    public void shouldMapMovieToMovieDto() {
        Movie movie = new Movie();
        movie.setTitle("The Dark Knight");
        movie.setReleaseDate(LocalDate.of(2008, 7, 18));

        Director director = new Director();
        director.setFirstName("Christopher");
        director.setLastName("Nolan");
        movie.setDirector(director);

        Soundtrack soundtrack = new Soundtrack();
        soundtrack.setComposer("Hans Zimmer");
        movie.setSoundtrack(soundtrack);

        Award award1 = new Award();
        award1.setCategory("Best Performance by an Actor in a Supporting Role");
        award1.setDate(LocalDate.of(2009, 2, 22));

        Award award2 = new Award();
        award2.setCategory("Best Achievement in Sound Editing");
        award2.setDate(LocalDate.of(2009, 2, 22));

        movie.setAwards(Stream.of(award1, award2));

        movie.setRuntime("152 min");
        movie.setBudget(185000000);

        MovieDTO movieDTO = MovieMapper.INSTANCE.movieToMovieDto(movie);

        assertThat(movieDTO).isNotNull();
        assertThat(movieDTO.getTitle()).isEqualTo(movie.getTitle());
        assertThat(movieDTO.getReleaseDate()).isEqualTo("18.07.2008");
        assertThat(movieDTO.getDirector()).isEqualTo("Christopher Nolan");
        assertThat(movieDTO.getSoundtrack()).isEqualTo("Hans Zimmer");
        assertThat(movieDTO.getAwards()).hasSize(2);
        assertThat(movieDTO.getAwards()).extracting(AwardDTO::getYear).containsOnly(2009);
        assertThat(movieDTO.getAwards()).extracting(AwardDTO::getCategory)
                .contains("Best Performance by an Actor in a Supporting Role", "Best Achievement in Sound Editing");
        assertThat(movieDTO.getRuntime()).isNull();
        assertThat(movieDTO.getBudget()).isEqualTo("$185,000,000");
        assertThat(movieDTO.getLang()).isEqualTo("English");
    }
}
