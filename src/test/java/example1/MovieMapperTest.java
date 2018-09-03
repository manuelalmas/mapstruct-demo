package example1;

import example1.domain.Movie;
import example1.dto.MovieDTO;
import example1.mapper.MovieMapper;
import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

public class MovieMapperTest {

    @Test
    public void shouldMapMovieToMovieDto() {
        Movie movie = new Movie();
        movie.setTitle("The Dark Knight");
        movie.setYear(2008);
        movie.setDirector("Christopher Nolan");

        MovieDTO movieDTO = MovieMapper.INSTANCE.movieToMovieDto(movie);

        assertThat(movieDTO).isNotNull();
        assertThat(movieDTO.getTitle()).isEqualTo(movie.getTitle());
        assertThat(movieDTO.getYear()).isEqualTo(String.valueOf(movie.getYear()));
        assertThat(movieDTO.getMaker()).isEqualTo(movie.getDirector());
    }

    @Test
    public void shouldMapMovieDtoToMovie() {
        MovieDTO movieDTO = new MovieDTO();
        movieDTO.setTitle("The Dark Knight");
        movieDTO.setYear("2008");
        movieDTO.setMaker("Christopher Nolan");

        Movie movie = MovieMapper.INSTANCE.movieDtoToMovie(movieDTO);

        assertThat(movie).isNotNull();
        assertThat(movie.getTitle()).isEqualTo(movieDTO.getTitle());
        assertThat(movie.getYear()).isEqualTo(Integer.parseInt(movieDTO.getYear()));
        assertThat(movie.getDirector()).isEqualTo(movieDTO.getMaker());
    }
}
