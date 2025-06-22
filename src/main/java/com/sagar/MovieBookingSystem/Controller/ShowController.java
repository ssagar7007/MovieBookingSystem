package com.sagar.MovieBookingSystem.Controller;

import com.sagar.MovieBookingSystem.DTO.ShowDTO;
import com.sagar.MovieBookingSystem.Service.ShowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/show")
public class ShowController {
    @Autowired
    private ShowService showService;


    @PostMapping("/createShow")
    @PreAuthorize(("hasRole('ADMIN')"))
    public ResponseEntity<ShowDTO> createShow(@RequestBody ShowDTO showDTO){
        return ResponseEntity.ok(showService.createShow(showDTO));
    }

    @GetMapping("/allShows")
    public ResponseEntity<List<ShowDTO>> getAllShows(){
        return ResponseEntity.ok(showService.getAllShows());
    }
    @GetMapping("/allShowsByMovie/{movieid}")
    public ResponseEntity<List<ShowDTO>> getAllShowsByMovie(@PathVariable Long movieId){
        return ResponseEntity.ok(showService.getAllShowsByMovie(movieId));
    }

    @GetMapping("/allShowsByTheater/{theaterId}")
    public ResponseEntity<List<ShowDTO>> getAllShowsByTheater(@PathVariable Long theaterId){
        return ResponseEntity.ok(showService.getAllShowsByTheater(theaterId));
    }

    @PutMapping("/updateShow/{id}")
    @PreAuthorize(("hasRole('ADMIN')"))
    public ResponseEntity<ShowDTO> updateShow(@PathVariable Long id, @RequestBody ShowDTO showDTO){
        return ResponseEntity.ok(showService.updateShow(id, showDTO));
    }

    @DeleteMapping("/deleteShow/{id}")
    @PreAuthorize(("hasRole('ADMIN')"))
    public ResponseEntity<Void> deleteShow(@PathVariable Long id){
        showService.deleteShow(id);
        return ResponseEntity.ok().build();
    }


}
