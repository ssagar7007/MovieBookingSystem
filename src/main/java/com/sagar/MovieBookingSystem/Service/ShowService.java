package com.sagar.MovieBookingSystem.Service;

import com.sagar.MovieBookingSystem.DTO.ShowDTO;
import com.sagar.MovieBookingSystem.Entity.Movie;
import com.sagar.MovieBookingSystem.Entity.Show;
import com.sagar.MovieBookingSystem.Entity.Theater;
import com.sagar.MovieBookingSystem.Repository.MovieRepository;
import com.sagar.MovieBookingSystem.Repository.ShowRepository;
import com.sagar.MovieBookingSystem.Repository.TheaterRepository;
import com.sagar.MovieBookingSystem.Util.ShowUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ShowService {
    @Autowired
    private ShowRepository showRepository;
    @Autowired
    private ShowUtil showUtil;
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private TheaterRepository theaterRepository;

    public ShowDTO createShow(ShowDTO showDTO){
        Movie movie = movieRepository.findById(showDTO.getMovieId()).orElseThrow(
                ()-> new RuntimeException("No movie found for id "+ showDTO.getMovieId()));
        Theater theater = theaterRepository.findById(showDTO.getTheaterId()).orElseThrow(
                ()-> new RuntimeException("No theater found for id "+showDTO.getTheaterId()));
        Show show = showUtil.convertDTOToEntity(showDTO,new Show(),movie,theater);
        return showUtil.convertEntityToDTO(showRepository.save(show));
    }

    public List<ShowDTO> getAllShows(){
        List<Show> showList = showRepository.findAll();
        List<ShowDTO> showDTOList = new ArrayList<>();
        showList.forEach(show -> {
            showDTOList.add(showUtil.convertEntityToDTO(show));
        });
        return showDTOList;
    }

    public List<ShowDTO> getAllShowsByMovie(String movieId){
        List<Show> showList = showRepository.findAllByMovieId(movieId);
        if(!showList.isEmpty()){
            List<ShowDTO> showDTOList = new ArrayList<>();
            showList.forEach(show -> {
                showDTOList.add(showUtil.convertEntityToDTO(show));
            });
            return showDTOList;
        }else{
            throw new RuntimeException("Shows not found with movieId "+ movieId);
        }
    }

    public List<ShowDTO> getAllShowsByTheater(String theaterId){
        List<Show> showList = showRepository.findAllByTheaterId(theaterId);
        if(!showList.isEmpty()){
            List<ShowDTO> showDTOList = new ArrayList<>();
            showList.forEach(show -> {
                showDTOList.add(showUtil.convertEntityToDTO(show));
            });
            return showDTOList;
        }else{
            throw new RuntimeException("Shows not found with theaterId "+ theaterId);
        }
    }

    public ShowDTO updateShow(Long id, ShowDTO showDTO){
        Show show = showRepository.findById(id).orElseThrow(()->new RuntimeException("No Show found with id "+ id));
        Movie movie = movieRepository.findById(showDTO.getMovieId()).orElseThrow(
                ()-> new RuntimeException("No movie found for id "+ showDTO.getMovieId()));
        Theater theater = theaterRepository.findById(showDTO.getTheaterId()).orElseThrow(
                ()-> new RuntimeException("No theater found for id "+showDTO.getTheaterId()));
        show = showUtil.convertDTOToEntity(showDTO,show,movie,theater);
        return showUtil.convertEntityToDTO(showRepository.save(show));
    }


    public void deleteShow(Long id){
        Show show = showRepository.findById(id).orElseThrow(()->new RuntimeException("No show exists for id "+id));
        if(!show.getBooking().isEmpty()){
            throw  new RuntimeException("Can't delete show with existing bookings");
        }
        showRepository.deleteById(id);
    }




}
