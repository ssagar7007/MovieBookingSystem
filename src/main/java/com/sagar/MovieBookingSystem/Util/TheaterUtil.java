package com.sagar.MovieBookingSystem.Util;

import com.sagar.MovieBookingSystem.DTO.TheaterDTO;
import com.sagar.MovieBookingSystem.Entity.Theater;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class TheaterUtil {

    public TheaterDTO convertEntityToDTO(Theater theater){
        TheaterDTO theaterDTO = new TheaterDTO();
        theaterDTO.setId(theater.getId());
        theaterDTO.setName(theater.getName());
        theaterDTO.setTheaterLocation(theater.getTheaterLocation());
        theaterDTO.setTheaterCapacity(theater.getTheaterCapacity());
        theaterDTO.setTheaterScreenType(theater.getTheaterScreenType());
        return theaterDTO;
    }

    public Theater convertDTOToEntity(TheaterDTO theaterDTO,Theater theater){
        if(StringUtils.isNotEmpty(theaterDTO.getName())) {
            theater.setName(theaterDTO.getName());
        }
        if(StringUtils.isNotEmpty(theaterDTO.getTheaterLocation())) {
            theater.setTheaterLocation(theaterDTO.getTheaterLocation());
        }
        if(theaterDTO.getTheaterCapacity() != null) {
            theater.setTheaterCapacity(theaterDTO.getTheaterCapacity());
        }
        if(StringUtils.isNotEmpty(theaterDTO.getTheaterScreenType())) {
            theater.setTheaterScreenType(theaterDTO.getTheaterScreenType());
        }

        return theater;
    }
}
