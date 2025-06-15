package com.sagar.MovieBookingSystem.Util;

import com.sagar.MovieBookingSystem.DTO.ShowDTO;
import com.sagar.MovieBookingSystem.Entity.Movie;
import com.sagar.MovieBookingSystem.Entity.Show;
import com.sagar.MovieBookingSystem.Entity.Theater;
import org.springframework.stereotype.Component;

@Component
public class ShowUtil {
    public ShowDTO convertEntityToDTO(Show show){
        ShowDTO showDTO = new ShowDTO();
        showDTO.setShowTime(show.getShowTime());
        showDTO.setPrice(showDTO.getPrice());
        showDTO.setMovieId(show.getMovie().getId());
        showDTO.setTheaterId(show.getTheater().getId());
        return showDTO;
    }
    public Show convertDTOToEntity(ShowDTO showDTO, Show show, Movie movie, Theater theater){
        show.setMovie(movie);
        show.setTheater(theater);
        show.setShowTime(showDTO.getShowTime());
        show.setPrice(showDTO.getPrice());
        return show;
    }

}
