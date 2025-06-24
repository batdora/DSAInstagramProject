public class User {

    private int userID;
    private String originalUserID;
    public Post[] posts = new Post[5];
    private int postNum = 0;
    private boolean largeUser = false;
    FollowingTree followingTree = new FollowingTree();
    SeenPostAVLTree seenPostTree = new SeenPostAVLTree();

    private Post[] feed;

    public Post[] getFeed() {
        return feed;
    }

    public void setFeed(Post[] feed) {
        this.feed = feed;
    }

    public int getPostNum() {
        return postNum;
    }

    public String getOriginalUserID() {
        return originalUserID;
    }

    public int getUserID() {
        return userID;
    }

    // Constructor
    public User(int userID, String originalUserID) {
        this.userID = userID;
        this.originalUserID = originalUserID;
    }



    public void addPost(Post newPost){
        if (postNum == 5 && !largeUser) {
            Post[] tempPosts = new Post[100];
            System.arraycopy(posts, 0, tempPosts, 0, 5);
            posts = tempPosts;
            posts[5] = newPost;
            postNum++;
            largeUser = true;
        }else if (postNum == 100) {
            Post[] tempPosts = new Post[1000];
            System.arraycopy(posts, 0, tempPosts, 0, 100);
            posts = tempPosts;
            posts[100] = newPost;
            postNum ++;
        }else if (postNum == 1000) {
            Post[] tempPosts = new Post[100000];
            System.arraycopy(posts, 0, tempPosts, 0, 1000);
            posts = tempPosts;
            posts[1000] = newPost;
            postNum++;
        }else{
            posts[postNum] = newPost;
            postNum++;
        }
    }


    public class FollowingTree {

        private class Node {
            User user;
            Node left, right;
            int height;
            int totalPostsBelow; // To keep track of the total number of posts in subtrees

            Node(User user) {
                this.user = user;
                this.height = 1; // Initial height for new nodes is 1
                this.totalPostsBelow = 0;
            }
        }

        private Node root;

        public Node getRoot() {
            return root;
        }

        private boolean nodeInserted;

        // Method to get the height of a node
        private int height(Node node) {
            return (node == null) ? 0 : node.height;
        }

        // Get balance factor of a node
        private int getBalance(Node node) {
            return (node == null) ? 0 : height(node.left) - height(node.right);
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

            // Update totalPostsBelow
            y.totalPostsBelow = calculateTotalPosts(y);
            x.totalPostsBelow = calculateTotalPosts(x);

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

            // Update totalPostsBelow
            x.totalPostsBelow = calculateTotalPosts(x);
            y.totalPostsBelow = calculateTotalPosts(y);

            return y;
        }

        // Method to insert a new follower
        public int insert(User user) {
            nodeInserted = false; // Reset the flag before insertion
            root = insertNode(root, user);
            return nodeInserted ? 0 : -1;
        }

        private Node insertNode(Node current, User user) {
            if (current == null) {
                nodeInserted = true;
                return new Node(user);
            }

            if (user.getUserID() < current.user.getUserID()) {
                current.left = insertNode(current.left, user);
            } else if (user.getUserID() > current.user.getUserID()) {
                current.right = insertNode(current.right, user);
            } else {
                // User ID already exists; do not insert duplicates
                return current;
            }

            // Update height of the current node
            current.height = 1 + Math.max(height(current.left), height(current.right));

            // Update totalPostsBelow
            current.totalPostsBelow = calculateTotalPosts(current);

            // Balance the node if needed
            int balance = getBalance(current);

            // Left Left Case
            if (balance > 1 && user.getUserID() < current.left.user.getUserID()) {
                return rightRotate(current);
            }

            // Right Right Case
            if (balance < -1 && user.getUserID() > current.right.user.getUserID()) {
                return leftRotate(current);
            }

            // Left Right Case
            if (balance > 1 && user.getUserID() > current.left.user.getUserID()) {
                current.left = leftRotate(current.left);
                return rightRotate(current);
            }

            // Right Left Case
            if (balance < -1 && user.getUserID() < current.right.user.getUserID()) {
                current.right = rightRotate(current.right);
                return leftRotate(current);
            }

            return current;
        }

        // Search method
        public User findFollowing(int userID) {
            return findFollowing(root, userID);
        }

        private User findFollowing(Node current, int userID) {
            if (current == null) {
                return null;
            }
            if (userID == current.user.getUserID()) {
                return current.user;
            } else if (userID < current.user.getUserID()) {
                return findFollowing(current.left, userID);
            } else {
                return findFollowing(current.right, userID);
            }
        }

        // Delete method
        public void unfollow(int userID) {
            root = unfollow(root, userID);
        }

        private Node unfollow(Node current, int userID) {
            if (current == null) {
                return null;
            }

            if (userID < current.user.getUserID()) {
                current.left = unfollow(current.left, userID);
            } else if (userID > current.user.getUserID()) {
                current.right = unfollow(current.right, userID);
            } else {
                // Node to be deleted found
                if (current.left == null || current.right == null) {
                    Node temp = (current.left != null) ? current.left : current.right;
                    current = temp;
                } else {
                    // Node with two children: Get the inorder successor (smallest in the right subtree)
                    Node successor = getMinValueNode(current.right);
                    current.user = successor.user;
                    current.right = unfollow(current.right, successor.user.getUserID());
                }
            }

            if (current == null) {
                return current;
            }

            // Update height of the current node
            current.height = 1 + Math.max(height(current.left), height(current.right));

            // Update totalPostsBelow
            current.totalPostsBelow = calculateTotalPosts(current);

            // Balance the node if needed
            int balance = getBalance(current);

            // Left Left Case
            if (balance > 1 && getBalance(current.left) >= 0) {
                return rightRotate(current);
            }

            // Left Right Case
            if (balance > 1 && getBalance(current.left) < 0) {
                current.left = leftRotate(current.left);
                return rightRotate(current);
            }

            // Right Right Case
            if (balance < -1 && getBalance(current.right) <= 0) {
                return leftRotate(current);
            }

            // Right Left Case
            if (balance < -1 && getBalance(current.right) > 0) {
                current.right = rightRotate(current.right);
                return leftRotate(current);
            }

            return current;
        }

        // Helper method to find the minimum value node
        private Node getMinValueNode(Node node) {
            Node current = node;
            while (current.left != null) {
                current = current.left;
            }
            return current;
        }

        int index = 0;

        // Method to traverse
        public Post[] traverse() {
            index = 0;

            if (root == null)
                return null;

            Post[] posts = new Post[root.totalPostsBelow];

            return traverseInOrder(root, posts, this.index);
        }

        private Post[] traverseInOrder(Node node, Post[] posts, int index) {
            if (node != null) {

                int howManyPosts = node.user.postNum;

                System.arraycopy(node.user.posts, 0, posts, this.index, howManyPosts);

                this.index += howManyPosts;

                posts = traverseInOrder(node.left,posts,this.index);
                posts = traverseInOrder(node.right,posts, this.index);
            }

            return posts;
        }

        public int updateTotalPostsBelow(Node node) {
            if (node == null) {
                return 0;
            }

            // Update left and right subtrees recursively
            int leftPosts = updateTotalPostsBelow(node.left);
            int rightPosts = updateTotalPostsBelow(node.right);

            // Calculate total posts below the current node
            int currentNodePosts = getPostCount(node.user);
            node.totalPostsBelow = leftPosts + rightPosts + currentNodePosts;

            return node.totalPostsBelow;
        }

        // Calculate total posts in the subtree of a node
        private int calculateTotalPosts(Node node) {
            if (node == null) {
                return 0;
            }
            int leftPosts = node.left != null ? node.left.totalPostsBelow + getPostCount(node.left.user) : 0;
            int rightPosts = node.right != null ? node.right.totalPostsBelow + getPostCount(node.right.user) : 0;
            return leftPosts + rightPosts + node.user.postNum;
        }

        // Helper method to get the number of posts for a user
        private int getPostCount(User user) {
            return user != null ? user.postNum : 0;
        }
    }

    public class SeenPostAVLTree {

        private class Node {
            Post post;
            Node left, right;
            int height;
            boolean liked; // Liked flag

            Node(Post post) {
                this.post = post;
                this.height = 1; // Initial height for new nodes is 1
                this.liked = false; // Initialize liked flag to false
            }

            // Constructor for liked insert
            Node(Post post, boolean liked) {
                this.post = post;
                this.height = 1; // Initial height for new nodes is 1
                this.liked = liked;
            }
        }

        private Node root; // Root of the AVL tree

        // Helper method to get the height of a node
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

        // Method to insert a postID into the AVL tree
        public void insertSeenPost(Post post) {
            root = insertNode(root, post, false);
        }

        // Method to insert a liked postID into the AVL tree
        public void insertLikedPost(Post post) {
            root = insertNode(root, post, true);
        }

        private Node insertNode(Node node, Post post, boolean liked) {
            // Base case for recursion
            if (node == null) {
                if (liked)
                    post.like();
                return new Node(post, liked);
            }

            if (post.getPostID() < node.post.getPostID()) {
                node.left = insertNode(node.left, post, liked);
            } else if (post.getPostID() > node.post.getPostID()) {
                node.right = insertNode(node.right, post, liked);
            } else {
                // Duplicate postID, do not insert
                return node;
            }

            // Update height of the current node
            node.height = 1 + Math.max(height(node.left), height(node.right));

            // Get the balance factor to check if the node is unbalanced
            int balance = getBalance(node);

            // Balance the node if needed

            // Left Left Case
            if (balance > 1 && post.getPostID() < node.left.post.getPostID()) {
                return rightRotate(node);
            }

            // Right Right Case
            if (balance < -1 && post.getPostID() > node.right.post.getPostID()) {
                return leftRotate(node);
            }

            // Left Right Case
            if (balance > 1 && post.getPostID() > node.left.post.getPostID()) {
                node.left = leftRotate(node.left);
                return rightRotate(node);
            }

            // Right Left Case
            if (balance < -1 && post.getPostID() < node.right.post.getPostID()) {
                node.right = rightRotate(node.right);
                return leftRotate(node);
            }

            return node;
        }

        // Method to like a post
        public int toggleLikePost(Post post) {
            Node node = findNode(root, post.getPostID());

            // Post not seen before
            if (node == null) {
                insertLikedPost(post);
                System.out.println(originalUserID + " liked post " + post.getOriginalPostID());
                return 0;
            }

            // Post is seen before but not liked
            if (!node.liked){
                node.liked = true;
                post.like();
                System.out.println(originalUserID + " liked post " + post.getOriginalPostID());
                return 1;
            }

            // Unlike post
            else {
                node.liked = false;
                post.unlike();
                return 2;
            }
        }

        // Helper method to find a node by postID
        private Node findNode(Node node, int postID) {
            if (node == null) {
                return null;
            }
            if (postID == node.post.getPostID()) {
                return node;
            } else if (postID < node.post.getPostID()) {
                return findNode(node.left, postID);
            } else {
                return findNode(node.right, postID);
            }
        }

        public boolean findNode(int postID){
            Node result = findNode(root,postID);

            if (result == null){
                return false;

            }else return true;
        }
    }

}
