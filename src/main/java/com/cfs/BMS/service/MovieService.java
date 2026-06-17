package com.cfs.BMS.service;

import com.cfs.BMS.dto.MovieRequest;
import com.cfs.BMS.entity.Movie;
import com.cfs.BMS.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor

public class MovieService {

    //update,delete functionality add

    private final MovieRepository movieRepository;


    public Movie addMovies(MovieRequest request)
    {
        Movie movie = Movie.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .durationMinutes(request.getDurationMinutes())
                .genre(request.getGenre())
                .rating(request.getRating())
                .language(request.getLanguage())
                .releaseDate(request.getReleaseDate())
                .posterUrl(request.getPosterUrl())
                .build();

        return movieRepository.save(movie);

    }

    public List<Movie> getAllMovies()
    {
        return movieRepository.findAll();
    }

    public Movie getMovieById(Long id)
    {
        return movieRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Movie not found with id: "+id));
    }

    public List<Movie> searchByTitle(String title)
    {
        return movieRepository.findByTitleContainingIgnoreCase(title);
    }

    public List<Movie> searchByGenre(String genre)
    {
        return movieRepository.findByGenre(genre);
    }

    public List<Movie> searchByLanguage(String language)
    {
        return movieRepository.findByLanguage(language);
    }


}
