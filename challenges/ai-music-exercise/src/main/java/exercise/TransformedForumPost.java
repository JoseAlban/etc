package exercise;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TransformedForumPost extends ForumPost {

    private String postDate;

    public TransformedForumPost() {

    }

    public TransformedForumPost(ForumPost forumPost, String postDate) {
        this.setUserId(forumPost.getUserId());
        this.setId(forumPost.getId());

        String title = forumPost.getTitle();
        String body = forumPost.getBody();
        if (title == null && body != null) {
            title = body.substring(0, body.indexOf('.'));
        }
        this.setTitle(title);
        this.setBody(body);

        if (postDate == null) {
            postDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        }
        this.setPostDate(postDate);
    }

    public void setPostDate(String postDate) {
        this.postDate = postDate;
    }

    public String getPostDate() {
        return postDate;
    }

    public String getUppercaseTitle() {
        return this.getTitle().toUpperCase();
    }
}
