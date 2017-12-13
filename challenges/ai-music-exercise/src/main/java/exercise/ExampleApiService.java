package exercise;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ExampleApiService {

    private RestTemplate restTemplate;

    @Autowired
    public ExampleApiService(RestTemplate restTemplate) {

        this.restTemplate = restTemplate;
    }

    ForumPosts getPosts() {
        ResponseEntity<ForumPosts> entity = restTemplate.getForEntity("https://jsonplaceholder.typicode.com/posts", ForumPosts.class);
        if (entity.getStatusCode().is2xxSuccessful()) {
            return entity.getBody();
        } else {
            throw new RuntimeException("Can't get posts");
        }
    }

    TransformedForumPosts getTransformedPosts() {
        return new TransformedForumPosts(this.getPosts());
    }
}
