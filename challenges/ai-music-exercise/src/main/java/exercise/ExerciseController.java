package exercise;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExerciseController {

    private ExampleApiService exampleApiService;
    private UserService userService;

    @Autowired
    public ExerciseController(ExampleApiService exampleApiService,
                              UserService userService) {
        this.exampleApiService = exampleApiService;
        this.userService = userService;
    }

    @RequestMapping("/simple")
    ForumPosts posts() {
        return exampleApiService.getPosts();
    }

    @RequestMapping("/transformed")
    TransformedForumPosts transformed() {
        return exampleApiService.getTransformedPosts();
    }

    @RequestMapping(path="/translated", params={"userId"})
    String translated(@RequestParam("userId") int userId) {
        return userService.getUsername(userId);
    }

}
