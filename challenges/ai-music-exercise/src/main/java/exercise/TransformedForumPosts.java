package exercise;

import java.util.ArrayList;

public class TransformedForumPosts extends ArrayList<TransformedForumPost> {

    public TransformedForumPosts() {
    }

    public TransformedForumPosts(ForumPosts forumPosts) {
        for (ForumPost forumPost: forumPosts) {
            this.add(new TransformedForumPost(forumPost, null));
        }
    }

}
