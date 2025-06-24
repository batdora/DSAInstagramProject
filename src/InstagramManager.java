import java.util.Arrays;

public class InstagramManager {

    public UserTree userTree;
    public PostTree postTree;

    public InstagramManager() {
        this.userTree = new UserTree();
        this.postTree = new PostTree();
    }

    public int createPost(User user, int postID, String originalPostID){

        if (user != null){

            Post post = new Post(user,postID,originalPostID);

            int result = postTree.insert(post);

            if (result == 0){
                user.addPost(post);
            }

            return result;
        }

        else return -1;

    }

    // Method to see a single post
    public void seePost(User user, Post post){
        boolean post_intree = user.seenPostTree.findNode(post.getPostID());

        if (!post_intree){
            user.seenPostTree.insertSeenPost(post);
        }
    }

    // Method to see all posts of a given user (user1 sees user2's posts)
    public void seeAllPosts(User user1, User user2){
        for (int i =0; i < user2.posts.length; i++){
         Post post = user2.posts[i];
         if (post == null){
             break;
         }
         user1.seenPostTree.insertSeenPost(post);
        }
    }


    public int followUser(User follower, User followed){

        return follower.followingTree.insert(followed);
    }

    public int unfollowUser(User follower, User followed){

        User tempUserFollowed = follower.followingTree.findFollowing(followed.getUserID());
        // Follower does not follow the followed already
        if (tempUserFollowed == null){
            return -1;
        }
        // Unfollow
        else if (tempUserFollowed.getUserID() == followed.getUserID()) {
            follower.followingTree.unfollow(followed.getUserID());
            return 0;
        } else{
            System.out.println("Unexpected error in unfollow user");
            return -1;
        }

    }

    public StringBuilder createFeed(User user, int feed_num){

        StringBuilder output = new StringBuilder("Feed for " + user.getOriginalUserID() + ":\n");

        user.followingTree.updateTotalPostsBelow(user.followingTree.getRoot());

        Post[] allPosts = user.followingTree.traverse();

        if (allPosts == null)
            return output.append("No more posts available for " + user.getOriginalUserID() + ".");

        MaxBinaryHeap heap = new MaxBinaryHeap(allPosts.length);

        heap.buildHeap(allPosts);

        Post[] feed = new Post[feed_num];

        for (int i = 0; i<feed_num;){
            Post post = heap.scroll();

            if (post != null){
                if (!user.seenPostTree.findNode(post.getPostID())){

                    output.append("Post ID: " + post.getOriginalPostID() + ", Author: " + post.getUser().getOriginalUserID() + ", Likes: " + post.getTotalLikes() +  "\n");

                    feed[i] = post;

                    i++;
                }

            } else{
                output.append("No more posts available for " + user.getOriginalUserID() + ".");
                break;
            }
        }

        user.setFeed(feed);

        System.out.println(output);

        return output;
    }

    public StringBuilder scrollFeed(User user, int[] scrolls){

        Post[] feed;

        StringBuilder output = new StringBuilder(user.getOriginalUserID() + " is scrolling through feed:\n");

        feed = user.getFeed();

        if (feed == null)
            return output.append("No more posts in feed.");


        for (int i = 0; i<scrolls.length; i++){

            Post post = feed[i];

            if (post != null){

                System.out.println(post.getOriginalPostID() + " is seen in feed of " + user.getOriginalUserID() + " and it has " + post.getTotalLikes() + " likes.");

                if (scrolls[i] == 1){
                    user.seenPostTree.insertLikedPost(post);
                    output.append(user.getOriginalUserID() + " saw " + post.getOriginalPostID() + " while scrolling and clicked the like button.\n");
                }else {
                    user.seenPostTree.insertSeenPost(post);

                    output.append(user.getOriginalUserID() + " saw " + post.getOriginalPostID() + " while scrolling.\n");
                }


            } else{
                output.append("No more posts in feed.");
                break;
            }
        }

        return output;
    }

    public StringBuilder sortPosts(User user){

        int postNum = user.getPostNum();

        if(postNum == 0){

            return new StringBuilder("No posts from " + user.getOriginalUserID() + ".");
        }

        Post[] posts = Arrays.copyOfRange(user.posts,0,postNum);

        StringBuilder output = new StringBuilder("Sorting " + user.getOriginalUserID() + "'s posts:\n");

        MaxBinaryHeap heap = new MaxBinaryHeap(postNum);

        heap.buildHeap(posts);

        for (int i = 0; i<postNum;i++){
            Post post = heap.scroll();

            if (post == null)
                break;

            output.append(post.getOriginalPostID() +  ", Likes: " + post.getTotalLikes() +  "\n");
        }

        return output;

    }

    public class MaxBinaryHeap {

        private Post[] heap;
        private int size;

        public MaxBinaryHeap(int capacity) {
            this.heap = new Post[capacity + 1]; // Index 0 will be unused for simplicity
            this.size = 0;
        }

        // Method to build a max binary heap from an array of posts
        public void buildHeap(Post[] posts) {
            size = posts.length;
            // Copy posts array into the heap starting at index 1
            System.arraycopy(posts, 0, heap, 1, posts.length);

            // Heapify process: Start from the first non-leaf node and go up to the root
            for (int i = size / 2; i > 0; i--) {
                heapifyDown(i);
            }
        }

        // Method to delete the max element (scroll operation)
        public Post scroll() {
            if (size == 0) {
                return null;
            }

            Post max = heap[1]; // The root (max) element
            heap[1] = heap[size]; // Move the last element to the root
            size--;
            heapifyDown(1); // Rebalance the heap

            return max;
        }

        // Helper method to maintain max-heap property
        private void heapifyDown(int index) {
            int leftChild = 2 * index;
            int rightChild = 2 * index + 1;
            int largest = index;

            // Compare left child
            if (leftChild <= size && comparePosts(heap[leftChild], heap[largest]) > 0) {
                largest = leftChild;
            }

            // Compare right child
            if (rightChild <= size && comparePosts(heap[rightChild], heap[largest]) > 0) {
                largest = rightChild;
            }

            // Swap and continue heapifying if the current node is not the largest
            if (largest != index) {
                swap(index, largest);
                heapifyDown(largest);
            }
        }

        // Helper method to compare two posts based on likes and postID
        private int comparePosts(Post a, Post b) {
            if (a.getTotalLikes() != b.getTotalLikes()) {
                return Integer.compare(a.getTotalLikes(), b.getTotalLikes());
            } else {
                return Integer.compare(a.getPostID(), b.getPostID());
            }
        }

        // Helper method to swap two elements in the heap
        private void swap(int i, int j) {
            Post temp = heap[i];
            heap[i] = heap[j];
            heap[j] = temp;
        }

        // Method to print the heap for debugging purposes
        public void printHeap() {
            for (int i = 1; i <= size; i++) {
                System.out.println("PostID: " + heap[i].getPostID() + ", Likes: " + heap[i].getTotalLikes());
            }
        }
    }

    public class UserTree {

        private class Node {
            User user;
            Node left, right;
            int height;
            int userID;

            Node(int userID, String originalUserID) {
                User user = new User(userID, originalUserID);
                this.user = user;
                this.height = 1;
                this.userID = user.getUserID();
            }
        }

        private Node root;
        private boolean nodeInserted; // Flag to track if a node was inserted

        // Method to get the height of a node
        private int height(Node node) {
            return (node == null) ? 0 : node.height;
        }

        // Right rotation
        private Node rightRotate(Node y) {
            Node x = y.left;
            Node T2 = x.right;

            // Perform rotation
            x.right = y;
            y.left = T2;

            // Update heights
            y.height = Math.max(height(y.left), height(y.right)) + 1;
            x.height = Math.max(height(x.left), height(x.right)) + 1;

            return x;
        }

        // Left rotation
        private Node leftRotate(Node x) {
            Node y = x.right;
            Node T2 = y.left;

            // Perform rotation
            y.left = x;
            x.right = T2;

            // Update heights
            x.height = Math.max(height(x.left), height(x.right)) + 1;
            y.height = Math.max(height(y.left), height(y.right)) + 1;

            return y;
        }

        // Get balance factor of a node
        private int getBalance(Node node) {
            return (node == null) ? 0 : height(node.left) - height(node.right);
        }

        // Method to insert a new user
        public String  insert(int userID, String originalUserID) {
            nodeInserted = false; // Reset the flag before insertion
            root = insertNode(root, userID, originalUserID);
            return nodeInserted ? "Created user with Id " : "Some error occurred in create_user.";
        }

        private Node insertNode(Node current, int userID, String originalUserID) {
            // Base case for recursion
            if (current == null) {
                nodeInserted = true; // Mark that a node was successfully inserted
                return new Node(userID, originalUserID);
            }

            // Traverse the tree to find the correct position
            if (userID < current.user.getUserID()) {
                current.left = insertNode(current.left, userID, originalUserID);
            } else if (userID > current.user.getUserID()) {
                current.right = insertNode(current.right, userID, originalUserID);
            } else {
                // A user with the same ID already exists
                return current; // Return current without marking insertion
            }

            // Update the height of the current node
            current.height = 1 + Math.max(height(current.left), height(current.right));

            // Get the balance factor to check if the node is unbalanced
            int balance = getBalance(current);

            // Perform the necessary rotations to balance the tree

            // Left Left Case
            if (balance > 1 && userID < current.left.user.getUserID()) {
                return rightRotate(current);
            }

            // Right Right Case
            if (balance < -1 && userID > current.right.user.getUserID()) {
                return leftRotate(current);
            }

            // Left Right Case
            if (balance > 1 && userID > current.left.user.getUserID()) {
                current.left = leftRotate(current.left);
                return rightRotate(current);
            }

            // Right Left Case
            if (balance < -1 && userID < current.right.user.getUserID()) {
                current.right = rightRotate(current.right);
                return leftRotate(current);
            }

            // Return the (unchanged) node pointer if no rotation is needed
            return current;
        }

        // Method to find a user by ID
        public User findUser(int userId) {
            return findUserNode(root, userId);
        }

        private User findUserNode(Node node, int userId) {
            if (node == null) {
                return null;
            }
            if (userId == node.user.getUserID()) {
                return node.user;
            } else if (userId < node.user.getUserID()) {
                return findUserNode(node.left, userId);
            } else {
                return findUserNode(node.right, userId);
            }
        }
    }


    public class PostTree {

        private class Node {
            Post post;
            Node left, right;
            int height;
            int postID;

            Node(Post post) {
                this.post = post;
                this.height = 1;
                this.postID = post.getPostID();
            }

            public void setPost(Post post) {
                this.post = post;
            }
        }

        private Node root;
        private boolean nodeInserted; // Flag to track if a node was inserted

        // Method to get the height of a node
        private int height(Node node) {
            return (node == null) ? 0 : node.height;
        }

        // Right rotation
        private Node rightRotate(Node y) {
            Node x = y.left;
            Node T2 = x.right;

            // Perform rotation
            x.right = y;
            y.left = T2;

            // Update heights
            y.height = Math.max(height(y.left), height(y.right)) + 1;
            x.height = Math.max(height(x.left), height(x.right)) + 1;

            return x;
        }

        // Left rotation
        private Node leftRotate(Node x) {
            Node y = x.right;
            Node T2 = y.left;

            // Perform rotation
            y.left = x;
            x.right = T2;

            // Update heights
            x.height = Math.max(height(x.left), height(x.right)) + 1;
            y.height = Math.max(height(y.left), height(y.right)) + 1;

            return y;
        }

        // Get balance factor of a node
        private int getBalance(Node node) {
            return (node == null) ? 0 : height(node.left) - height(node.right);
        }

        // Method to insert a new post
        public int insert(Post post) {
            nodeInserted = false; // Reset the flag before insertion
            root = insertNode(root, post);
            return nodeInserted ? 0 : -1;
        }

        private Node insertNode(Node current, Post post) {
            // Base case for recursion
            if (current == null) {
                nodeInserted = true; // Mark that a node was successfully inserted
                return new Node(post);
            }

            // Traverse the tree to find the correct position
            if (post.getPostID() < current.post.getPostID()) {
                current.left = insertNode(current.left, post);
            } else if (post.getPostID() > current.post.getPostID()) {
                current.right = insertNode(current.right, post);
            } else {
                // A post with the same ID already exists
                return current; // Return current without marking insertion
            }

            // Update the height of the current node
            current.height = 1 + Math.max(height(current.left), height(current.right));

            // Get the balance factor to check if the node is unbalanced
            int balance = getBalance(current);

            // Perform the necessary rotations to balance the tree

            // Left Left Case
            if (balance > 1 && post.getPostID() < current.left.post.getPostID()) {
                return rightRotate(current);
            }

            // Right Right Case
            if (balance < -1 && post.getPostID() > current.right.post.getPostID()) {
                return leftRotate(current);
            }

            // Left Right Case
            if (balance > 1 && post.getPostID() > current.left.post.getPostID()) {
                current.left = leftRotate(current.left);
                return rightRotate(current);
            }

            // Right Left Case
            if (balance < -1 && post.getPostID() < current.right.post.getPostID()) {
                current.right = rightRotate(current.right);
                return leftRotate(current);
            }

            // Return the (unchanged) node pointer if no rotation is needed
            return current;
        }

        // Method to find a post by ID
        public Post findPost(int postID) {
            return findPostNode(root, postID);
        }

        private Post findPostNode(Node node, int postID) {
            if (node == null) {
                return null;
            }
            if (postID == node.post.getPostID()) {
                return node.post;
            } else if (postID < node.post.getPostID()) {
                return findPostNode(node.left, postID);
            } else {
                return findPostNode(node.right, postID);
            }
        }

    }
}
