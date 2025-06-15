package com.sagar.MovieBookingSystem.Repository;

import com.sagar.MovieBookingSystem.Entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie,Long> {

    List<Movie> findAllByGenre(String genre);
    List<Movie> findAllByLanguage(String language);
    Optional<Movie> findByTitle(String title);


}
