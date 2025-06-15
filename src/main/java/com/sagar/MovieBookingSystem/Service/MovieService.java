package com.sagar.MovieBookingSystem.Service;

import com.sagar.MovieBookingSystem.DTO.MovieRequestDTO;
import com.sagar.MovieBookingSystem.DTO.MovieResponseDTO;
import com.sagar.MovieBookingSystem.Entity.Movie;
import com.sagar.MovieBookingSystem.Repository.MovieRepository;
import com.sagar.MovieBookingSystem.Util.MovieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MovieService {
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private MovieUtil movieUtil;


    public MovieResponseDTO addMovie(MovieRequestDTO movieRequestDTO){
           Movie movie = movieUtil.convertRequestDTOToEntity(movieRequestDTO,new Movie());
           return movieUtil.convertEntityToResponseDTO(movieRepository.save(movie));
    }

    public List<MovieResponseDTO> getAllMovies(){
          List<Movie> movieList = movieRepository.findAll();
          List<MovieResponseDTO> movieResponseDTOList = new ArrayList<>();
          movieList.forEach(movie -> {
              movieResponseDTOList.add(movieUtil.convertEntityToResponseDTO(movie));
          });
          return movieResponseDTOList;
    }


    public List<MovieResponseDTO> getMoviesByGenre(String genre){
        List<Movie> movieList = movieRepository.findAllByGenre(genre);
        if(!movieList.isEmpty()) {
            List<MovieResponseDTO> movieResponseDTOList = new ArrayList<>();
            movieList.forEach(movie -> {
                movieResponseDTOList.add(movieUtil.convertEntityToResponseDTO(movie));
            });
            return movieResponseDTOList;
        }else{
            throw new RuntimeException("No movies found for genre "+ genre);
        }
    }

    public List<MovieResponseDTO> getMoviesByLanguage(String language){
        List<Movie> movieList = movieRepository.findAllByLanguage(language);
        if(!movieList.isEmpty()) {
            List<MovieResponseDTO> movieResponseDTOList = new ArrayList<>();
            movieList.forEach(movie -> {
                movieResponseDTOList.add(movieUtil.convertEntityToResponseDTO(movie));
            });
            return movieResponseDTOList;
        }else{
            throw new RuntimeException("No movies found of language "+ language);
        }
    }

    public MovieResponseDTO getMovieByTitle(String title){
        Optional<Movie> movieOptional = movieRepository.findByTitle(title);
        if(movieOptional.isPresent()) {
            return movieUtil.convertEntityToResponseDTO(movieOptional.get());
        }else{
            throw new RuntimeException("No movies found of title "+ title);
        }
    }

    public MovieResponseDTO updateMovie(Long id, MovieRequestDTO movieRequestDTO){
        Movie movie = movieRepository.findById(id).orElseThrow(()-> new RuntimeException("No movie found for the id "+ id));
       movieUtil.convertRequestDTOToEntity(movieRequestDTO,movie);
        return movieUtil.convertEntityToResponseDTO(movieRepository.save(movie));

    }

    public void deleteMovie(Long id){
        movieRepository.deleteById(id);
    }



}
