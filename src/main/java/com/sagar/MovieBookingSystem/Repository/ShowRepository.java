package com.sagar.MovieBookingSystem.Repository;

import com.sagar.MovieBookingSystem.Entity.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShowRepository extends JpaRepository<Show,Long> {
    List<Show> findAllByMovieId(String movieId);
    List<Show> findAllByTheaterId(String theaterId);


}
