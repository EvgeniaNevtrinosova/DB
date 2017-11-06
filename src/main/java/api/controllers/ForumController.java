package api.controllers;

import api.models.Forum;
import api.models.User;
import api.services.ForumService;
import api.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/forum")
public class ForumController {
    final ForumService forumService;
    final UserService userService;


    @Autowired
    public ForumController(ForumService forumService, UserService userService) {
        this.forumService = forumService;
        this.userService = userService;
    }


    @PostMapping("/create")
    public ResponseEntity<?> createForum(@RequestBody Forum forum) {
        try {
            User author = userService.getInf(forum.getUser());
            forumService.create(forum, author.getNickname());
            forum.setUser(author.getNickname());
            return ResponseEntity.status(HttpStatus.CREATED).body(forum);
        } catch (DuplicateKeyException err) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(forumService.getInf(forum.getSlug()));
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Throwable("This user not found"));
        }
    }

    @GetMapping("/{slug}/details")
    public ResponseEntity<?> getForumDetails(@PathVariable String slug) {
        try {
            Forum foundForum = this.forumService.getInf(slug);
            return ResponseEntity.ok(foundForum);
        } catch (EmptyResultDataAccessException err) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Throwable("Not found such forum"));
        }
    }


}
