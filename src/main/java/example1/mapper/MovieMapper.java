package example1.mapper;

import example1.domain.Movie;
import example1.dto.MovieDTO;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MovieMapper {

    MovieMapper INSTANCE = Mappers.getMapper(MovieMapper.class);

    @Mapping(source = "director", target = "maker")
    MovieDTO movieToMovieDto(Movie movie);

    @InheritInverseConfiguration
    Movie movieDtoToMovie(MovieDTO movieDTO);
}
