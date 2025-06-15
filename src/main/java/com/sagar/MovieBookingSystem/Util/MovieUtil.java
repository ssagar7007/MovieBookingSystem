package com.sagar.MovieBookingSystem.Util;

import com.sagar.MovieBookingSystem.DTO.MovieRequestDTO;
import com.sagar.MovieBookingSystem.DTO.MovieResponseDTO;
import com.sagar.MovieBookingSystem.Entity.Movie;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class MovieUtil {
    public MovieResponseDTO convertEntityToResponseDTO(Movie movie){
        MovieResponseDTO movieResponseDTO = new MovieResponseDTO();
        movieResponseDTO.setId(movie.getId());
        movieResponseDTO.setTitle(movie.getTitle());
        movieResponseDTO.setDescription(movie.getDescription());
        movieResponseDTO.setGenre(movie.getGenre());
        movieResponseDTO.setLanguage(movie.getLanguage());
        movieResponseDTO.setDuration(movie.getDuration());
        movieResponseDTO.setReleaseDate(movie.getReleaseDate());
        return movieResponseDTO;
    }

    public Movie convertRequestDTOToEntity(MovieRequestDTO movieRequestDTO,Movie movie){
        if(StringUtils.isNotEmpty(movieRequestDTO.getTitle())) {
            movie.setTitle(movieRequestDTO.getTitle());
        }
        if(StringUtils.isNotEmpty(movieRequestDTO.getDescription())) {
            movie.setDescription(movieRequestDTO.getDescription());
        }
        if(StringUtils.isNotEmpty(movieRequestDTO.getGenre())) {
            movie.setGenre(movieRequestDTO.getGenre());
        }
        if(StringUtils.isNotEmpty(movieRequestDTO.getLanguage())){
            movie.setLanguage(movieRequestDTO.getLanguage());
        }
        if(movieRequestDTO.getReleaseDate() != null) {
            movie.setReleaseDate(movieRequestDTO.getReleaseDate());
        }
        if(StringUtils.isNotEmpty(movieRequestDTO.getDuration())) {
            movie.setDuration(movieRequestDTO.getDuration());
        }

        return movie;
    }
}
