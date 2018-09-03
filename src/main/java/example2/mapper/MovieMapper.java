package example2.mapper;

import example2.domain.Movie;
import example2.dto.MovieDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = AwardMapper.class)
public interface MovieMapper {

    MovieMapper INSTANCE = Mappers.getMapper(MovieMapper.class);

    @Mapping(target = "releaseDate", dateFormat = "dd.MM.yyyy")
    @Mapping(target = "director", expression = "java(movie.getDirector().toString())")
    @Mapping(source = "soundtrack.composer", target = "soundtrack")
    @Mapping(target = "runtime", ignore = true)
    @Mapping(target = "budget", numberFormat = "$###,###,###")
    @Mapping(source = "language", target = "lang", defaultValue = "English")
    MovieDTO movieToMovieDto(Movie movie);
}
