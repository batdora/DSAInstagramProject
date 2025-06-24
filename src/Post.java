public class Post {

    private User user;
    private int postID;
    private int total_likes;
    private String originalPostID;

    public String getOriginalPostID() {
        return originalPostID;
    }

    public Post(User user, int postID, String originalPostID) {
        this.user = user;
        this.postID = postID;
        this.total_likes = 0;
        this.originalPostID = originalPostID;

    }

    public User getUser() {
        return user;
    }

    public int getPostID() {
        return postID;
    }

    public int getTotalLikes(){
        return total_likes;
    }

    public void like(){
        total_likes ++;
        System.out.println(originalPostID + " has received a like.");
    }

    public void unlike(){
        total_likes --;
    }

}
