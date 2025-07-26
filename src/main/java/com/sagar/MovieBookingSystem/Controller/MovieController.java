package com.sagar.MovieBookingSystem.Controller;

import com.sagar.MovieBookingSystem.DTO.MovieRequestDTO;
import com.sagar.MovieBookingSystem.DTO.MovieResponseDTO;
import com.sagar.MovieBookingSystem.Service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
public class MovieController {
    @Autowired
    private MovieService movieService;



    @PostMapping("/addMovie")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<MovieResponseDTO> addMovie(@RequestBody MovieRequestDTO movieRequestDTO){
        return ResponseEntity.ok(movieService.addMovie(movieRequestDTO));
    }

    @GetMapping("/getAllMovies")
    public ResponseEntity<List<MovieResponseDTO>> getAllMovies(){
        return ResponseEntity.ok(movieService.getAllMovies());

    }
    @GetMapping("/getMoviesByGenre")
    public ResponseEntity<List<MovieResponseDTO>> getMoviesByGenre(@RequestParam String genre){
        return ResponseEntity.ok(movieService.getMoviesByGenre(genre));

    }
    @GetMapping("/getMoviesByLanguage")
    public ResponseEntity<List<MovieResponseDTO>> getMoviesByLanguage(@RequestParam String language){
        return ResponseEntity.ok(movieService.getMoviesByLanguage(language));

    }
    @GetMapping("/getMovieByTitle")
    public ResponseEntity<MovieResponseDTO> getMovieByTitle(@RequestParam String title){
        return ResponseEntity.ok(movieService.getMovieByTitle(title));

    }
    @PutMapping("/updateMovie/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<MovieResponseDTO> updateMovie(@PathVariable Long id, @RequestBody MovieRequestDTO movieRequestDTO){
        return ResponseEntity.ok(movieService.updateMovie(id,movieRequestDTO));

    }

    @DeleteMapping("/deleteMovie/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id){
        movieService.deleteMovie(id);
        return ResponseEntity.ok().build();

    }


}
