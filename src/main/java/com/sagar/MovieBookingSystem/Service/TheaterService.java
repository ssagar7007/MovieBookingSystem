package com.sagar.MovieBookingSystem.Service;

import com.sagar.MovieBookingSystem.DTO.TheaterDTO;
import com.sagar.MovieBookingSystem.Entity.Theater;
import com.sagar.MovieBookingSystem.Repository.TheaterRepository;
import com.sagar.MovieBookingSystem.Util.TheaterUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TheaterService {
    @Autowired
    private TheaterRepository theaterRepository;
    @Autowired
    private TheaterUtil theaterUtil;

    public TheaterDTO addTheater(TheaterDTO theaterDTO){
        Theater theater = theaterUtil.convertDTOToEntity(theaterDTO,new Theater());
        return theaterUtil.convertEntityToDTO(theaterRepository.save(theater));
    }

    public List<TheaterDTO> getTheaterByLocation(String location){
        List<Theater> theaterList = theaterRepository.findByLocation(location);
        List<TheaterDTO> theaterDTOList = new ArrayList<>();
        if(!theaterList.isEmpty()){
            theaterList.forEach(theater -> {
                theaterDTOList.add(theaterUtil.convertEntityToDTO(theater));
            });
            return  theaterDTOList;
        } else{
            throw  new RuntimeException("No theater found at location "+location);
        }
    }

    public TheaterDTO updateTheater(Long id, TheaterDTO theaterDTO){
        Theater theater = theaterRepository.findById(id).orElseThrow(()->new RuntimeException("No theater exists with the given id "+ id));
        theaterUtil.convertDTOToEntity(theaterDTO,theater);
        return theaterUtil.convertEntityToDTO(theaterRepository.save(theater));
    }

    public void deleteTheater(Long id){
        theaterRepository.deleteById(id);
    }



}
