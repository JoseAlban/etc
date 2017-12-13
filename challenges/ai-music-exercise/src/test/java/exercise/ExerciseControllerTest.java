package exercise;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
public class ExerciseControllerTest {

    @Mock
    private ExampleApiService exampleApiService;

    @Mock
    private UserService userService;

    @InjectMocks
    private ExerciseController exerciseController;

    @Test
    public void simpleEndpointReturnsPostsWithoutTransformation() {
        //This unit-test class is the wrong place to test that behaviour, unless it becomes an integration test

        //Given
        ForumPosts posts = generateTestPosts();
        when(exampleApiService.getPosts()).thenReturn(posts);

        //When
        ForumPosts controllerOutput = exerciseController.posts();

        //Then
        assertEquals(controllerOutput.size(), 2);
        assertTrue(controllerOutput.get(0).getTitle().contains("Test"));
    }

    @Test
    public void transformedEndpointReturnsPostsWithCurrentDate() {
        //This unit-test class is the wrong place to test that behaviour, unless it becomes an integration test

        //Given
        TransformedForumPosts transformed = generateTransformedTestPosts();
        when(exampleApiService.getTransformedPosts()).thenReturn(transformed);

        //When
        TransformedForumPosts controllerOutput = exerciseController.transformed();

        //Then
        assertEquals(controllerOutput.size(), 2);
        assertTrue(controllerOutput.get(0).getTitle().contains("Test"));
        assertTrue(controllerOutput.get(0).getUppercaseTitle().contains("TEST"));
        assertTrue(controllerOutput.get(0).getPostDate().matches("\\d{4}-[01]\\d-[0-3]\\d"));
    }

    @Test
    public void transformedEndpointReturnsPostsWithFirstSentenceOfBodyAsTitleWhenTitleMissing() {
        // Please complete this test and the code described by the method name; namely,
        // if the REST endpoint called by the ExampleApiService contains a null title or an
        // empty string, but the body is provided, then the title should be created automatically
        // from the first sentence of the body.

        //This unit-test class is the wrong place to test that behaviour, unless it becomes an integration test

        //Given
        TransformedForumPosts transformed = new TransformedForumPosts(generateTestPosts());
        when(exampleApiService.getTransformedPosts()).thenReturn(transformed);

        //When
        TransformedForumPosts controllerOutput = exerciseController.transformed();

        //Then
        assertEquals(controllerOutput.get(1).getTitle(), "This is the first sentence");
    }

    @Test
    public void userIdIsTranslatedToUsernameUsingUsersServiceInTranslatedEndpoint() {
        // Please complete this test.  This requires you to create a minimal stub of a new service.  You can use
        // a simple map internally in the new class. The /translated endpoint now needs to return a new field for
        // username, and the ExampleApiService needs to delegate to the new service to provide the username for
        // a given userId.

        //This unit-test class is the wrong place to test that behaviour, unless it becomes an integration test

        //Given
        int userId = 1;
        String translatedUsername = "user1";
        when(userService.getUsername(userId)).thenReturn(translatedUsername);

        //When
        String username = exerciseController.translated(userId);

        //Then
        assertEquals(username, translatedUsername);
    }

    private TransformedForumPosts generateTransformedTestPosts() {
        TransformedForumPosts posts = new TransformedForumPosts();
        TransformedForumPost post1 = new TransformedForumPost();
        post1.setBody("Body 1");
        post1.setId(1);
        post1.setTitle("Test 1");
        post1.setUserId(1);
        post1.setPostDate("2017-10-01");
        TransformedForumPost post2 = new TransformedForumPost();
        post2.setBody("Body 2");
        post2.setId(2);
        post2.setTitle("Test 2");
        post2.setUserId(2);
        post2.setPostDate("2017-11-01");
        posts.add(post1);
        posts.add(post2);
        return posts;
    }

    private ForumPosts generateTestPosts() {
        ForumPosts posts = new ForumPosts();
        ForumPost post1 = new ForumPost();
        post1.setTitle("Test 1");

        ForumPost post2 = new ForumPost();
        post2.setBody("This is the first sentence.  This is the second sentence.");

        posts.add(post1);
        posts.add(post2);

        return posts;
    }

}
