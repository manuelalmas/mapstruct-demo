# Mapping with MapStruct

### What is MapStruct?

MapStruct is a Java annotation processor for the generation of type-safe and performant mappers for Java bean classes. It saves you from writing mapping code by hand, which is a tedious and error-prone task. The generator comes with sensible defaults and many built-in type conversions, but it steps out of your way when it comes to configuring or implementing special behavior.

Compared to other bean mapping tools working at runtime, MapStruct offers the following advantages:

* **Excellent performance** by using plain method invocations instead of reflection
* **Compile-time type safety**
* **No runtime dependencies**
* **Clear error reports** at build time if mappings are incorrect or incomplete
* **Easily understandable and debuggable mapping code**


### Configuration

For Maven-based projects, add the following to your POM file in order to use MapStruct:

```xml
...
<properties>
    <org.mapstruct.version>1.2.0.Final</org.mapstruct.version>
</properties>
...
<dependencies>
    <dependency>
        <groupId>org.mapstruct</groupId>
        <artifactId>mapstruct-jdk8</artifactId>
        <version>${org.mapstruct.version}</version>
    </dependency>
</dependencies>
...
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.7.0</version>
            <configuration>
                <source>1.8</source>
                <target>1.8</target>
                <annotationProcessorPaths>
                    <path>
                        <groupId>org.mapstruct</groupId>
                        <artifactId>mapstruct-processor</artifactId>
                        <version>${org.mapstruct.version}</version>
                    </path>
                </annotationProcessorPaths>
            </configuration>
        </plugin>
    </plugins>
</build>
...
```

This configuration comprises the following artifacts: `mapstruct-jdk8` and `mapstruct-processor`. The former contains all the annotations provided by MapStruct, whereas the latter is responsible for generating the mapper implementations.

### Example 1

Suppose we have a domain entity and a data transfer object (DTO) as follows: 

```java
public class Movie {
    private String title;
    private Integer year;
    private String director;
    
    // getters and setters
}
```

```java
public class MovieDTO {
    private String title;
    private String year;
    private String maker;
    
    // getters and setters    
}
```

To create a mapping between these types, we can declare a mapper like this:

```java
@Mapper
public interface MovieMapper {
    MovieMapper INSTANCE = Mappers.getMapper(MovieMapper.class);

    @Mapping(source = "director", target = "maker")
    MovieDTO movieToMovieDto(Movie movie);

    @InheritInverseConfiguration
    Movie movieDtoToMovie(MovieDTO movieDTO);
}
```

At compile time MapStruct will generate an implementation of this interface:

* The `@Mapper` annotation marks the MovieMapper as mapping interface and lets MapStruct processor find this mapper and thus generate its implementation during compilation.
* By convention, a member called `INSTANCE` is defined, providing access to the mapper implementation. 
* The `@Mapping` annotation is used to configure the mapping between `Movie#director` and `MovieDTO#maker`. The mapping of the remainder properties are not explicitly specified since, by default, properties are automatically mapped if they have the same name in both the source and target types.
* The `@InheritInverseConfiguration` annotation is used to apply the inverse mappings of the movieToMovieDto method.

After building the project, we can look at the generated mapper implementation under `/target/generated-sources/annotations/`. 

Here is the generated implementation of the mapper defined above:

```java
@Generated
public class MovieMapperImpl implements MovieMapper {

    @Override
    public MovieDTO movieToMovieDto(Movie movie) {
        if ( movie == null ) {
            return null;
        }

        MovieDTO movieDTO = new MovieDTO();

        movieDTO.setMaker( movie.getDirector() );
        movieDTO.setTitle( movie.getTitle() );
        if ( movie.getYear() != null ) {
            movieDTO.setYear( String.valueOf( movie.getYear() ) );
        }

        return movieDTO;
    }

    @Override
    public Movie movieDtoToMovie(MovieDTO movieDTO) {
        if ( movieDTO == null ) {
            return null;
        }

        Movie movie = new Movie();

        movie.setDirector( movieDTO.getMaker() );
        movie.setTitle( movieDTO.getTitle() );
        if ( movieDTO.getYear() != null ) {
            movie.setYear( Integer.parseInt( movieDTO.getYear() ) );
        }

        return movie;
    }
}
```

As you can see, the previous code is very easy to understand and resembles the code we would have to write manually if we didn't have MapStruct at our disposal.

Let's write a test case that shows how we can use the previous mapper methods:

```java
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
```

### What if I use Spring?

When working with the Spring Framework, we don't need the `Mappers#getMapper` to obtain an instance of the mapper — we can use the `@Autowired` annotation instead.

In order to do so, we need to either annotate the mapper with `@Mapper(componentModel = "spring")` or add the following processor configuration option:

 ```xml
<compilerArgs>
	<compilerArg>
		-Amapstruct.defaultComponentModel=spring
	</compilerArg>
</compilerArgs>
 ```

I prefer the latter approach since the configuration is centralized in a single place and therefore we don't have to annotate every mapper, thus leaving the Java code cleaner.

### Example 2

Consider the following domain entity and its DTO counterpart:

```java
public class Award {
    private String category;
    private LocalDate date;

    // getters and setters
}
```

```java
public class AwardDTO {
    private String category;
    private Integer year;

    // getters and setters
}
```

There may be scenarios where some specific mapping from one type into another cannot be generated by MapStruct. For such cases, we can manually implement custom mapping logic like this:

```java
@Mapper
public interface AwardMapper {
    AwardMapper INSTANCE = Mappers.getMapper(AwardMapper.class);

    default AwardDTO awardToAwardDto(Award award) {
        if (award == null) {
            return null;
        }

        AwardDTO awardDTO = new AwardDTO();

        awardDTO.setCategory(award.getCategory());

        Optional.ofNullable(award.getDate())
                .ifPresent(date -> awardDTO.setYear(date.getYear()));

        return awardDTO;
    }
}
```

Now consider the following Movie entity, which has a relationship with the previous Award entity, and its corresponding DTO:

```java
public class Movie {
    private String title;
    private LocalDate releaseDate;
    private Director director;
    private Soundtrack soundtrack;
    private Stream<Award> awards;
    private String runtime;
    private Integer budget;
    private String language;

    // getters and setters
}
```

```java
public class MovieDTO {
    private String title;
    private String releaseDate;
    private String director;
    private String soundtrack;
    private Set<AwardDTO> awards;
    private String runtime;
    private String budget;
    private String lang;
    
    // getters and setters
}
```

Assume that the mapper between these types is as follows:

```java
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
```

* The `Mapper#uses` is intended to invoke other mappers. This is useful to structure the mapping code, for instance with one mapper per domain entity.
* The `Movie#releaseDate` property will be converted into a string formatted with the pattern `dd.MM.yyyy`.
* The `MovieDTO#director` property will hold the value returned by the Java expression `movie.getDirector().toString()`.
* The `Movie#soundtrack#composer` property will be mapped to `MovieDTO#soundtrack`. The `.` notation can be used to control nested bean mappings.
* The mapping between `Movie#runtime`  and `MovieDTO#runtime` will be ignored.
* The `Movie#budget` property will be converted to a string with the specified number format.
* If the `Movie#language` property is `null`, then `MovieDTO#lang` will be set to the predefined value `English`.

### Conclusion

This article was meant to walk you through the very basics of MapStruct.

There are other interesting features that were not covered here, including mapping customization with decorators, mapping customization with `@BeforeMapping` and `@AfterMapping` methods, shared mapping configurations with `@MapperConfig`, and much more!

Do you want to dive deeper into this amazing tool? Check out the links below.

### References

* http://mapstruct.org/documentation/stable/reference/html/
* https://github.com/mapstruct/mapstruct
* https://github.com/mapstruct/mapstruct-examples
  