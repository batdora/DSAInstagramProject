import java.io.*;

public class Main {
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        InstagramManager manager = new InstagramManager();

        String inputFile = args[0];
        String outputFile = args[1];

        // Use try-with-resources to ensure files are closed
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {

            String line;
            // Read each line (command) from the input file
            while ((line = reader.readLine()) != null) {
                // Trim and ignore empty lines or comments
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                // Split the command into parts
                String[] parts = line.split("\\s+");
                String command = parts[0].toLowerCase();

                String output = ""; // To collect the output for this command

                switch (command) {
                    case "create_user":
                        output = handleCreateUser(manager, parts);
                        break;

                    case "follow_user":
                        output = handleFollowUser(manager, parts);
                        break;

                    case "unfollow_user":
                        output = handleUnfollowUser(manager,parts);
                        break;

                    case "create_post":
                        output = handleCreatePost(manager, parts);
                        break;

                    case "see_post":
                        output = handleSeePost(manager,parts);
                        break;

                    case "see_all_posts_from_user":
                        output = handleSeeAllPosts(manager,parts);
                        break;

                    case "toggle_like":
                        output = handleToggleLike(manager,parts);
                        break;

                    case "generate_feed":
                        output = handleGenerateFeed(manager,parts);
                        break;

                    case "scroll_through_feed":
                        output = handleScrollFeed(manager,parts);
                        break;

                    case "sort_posts":
                        output = handleSortPosts(manager,parts);
                        break;

                    default:
                        output = "Unknown command: " + command;
                        break;
                }

                if (!output.isEmpty()) {
                    writer.write(output);
                    writer.newLine(); // Add a newline for readability
                }
            }

            System.out.println("Simulation completed. Outputs are written to " + outputFile);
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            System.out.println("Operation completed in " + duration + " milliseconds.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String handleCreateUser(InstagramManager manager, String[] parts) {

        if (parts[1].startsWith("user")) {
            try {
                int userId = Integer.parseInt(parts[1].substring(4));

                String result = manager.userTree.insert(userId, parts[1]);

                if (result.startsWith("S"))
                    return result;

                return result + parts[1] + ".";


            } catch (NumberFormatException e) {
                System.out.println("Error: The part after 'user' is not a valid integer for the input create user: " + parts[1]);
                return "Some error occurred in create_user.";
            }
        } else return "Some error occurred in create_user.";


    }

    private static String handleFollowUser(InstagramManager manager, String[] parts) {

        if (parts[1].startsWith("user")) {

            // Check if user1 is formatted correctly
            try {
                int user1Id = Integer.parseInt(parts[1].substring(4));

                User user1 = manager.userTree.findUser(user1Id);

                if (user1 != null) {

                    if (parts[2].startsWith("user")) {

                        // Check if user1 is formatted correctly
                        try {
                            int user2Id = Integer.parseInt(parts[2].substring(4));

                            if (user1Id == user2Id)
                                return "Some error occurred in follow_user.";

                            User user2 = manager.userTree.findUser(user2Id);

                            if (user2 != null) {

                                int result = manager.followUser(user1, user2);

                                if (result == 0){
                                    return parts[1] + " followed " + parts[2] + ".";

                                }else return "Some error occurred in follow_user.";

                            }else return "Some error occurred in follow_user.";

                        } catch (NumberFormatException e) {
                            System.out.println("Error: The part after 'user' is not a valid integer for the input follow user: " + parts[1]);
                            return "Some error occurred in follow_user.";
                        }

                    } else return "Some error occurred in follow_user.";

                }else return "Some error occurred in follow_user.";

            } catch (NumberFormatException e) {
                System.out.println("Error: The part after 'user' is not a valid integer for the input follow user: " + parts[1]);
                return "Some error occurred in follow_user.";
            }

        } else return "Some error occurred in follow_user.";
    }

    private static String handleUnfollowUser(InstagramManager manager, String[] parts) {

        if (parts[1].startsWith("user")) {

            // Check if user1 is formatted correctly
            try {
                int user1Id = Integer.parseInt(parts[1].substring(4));

                User user1 = manager.userTree.findUser(user1Id);

                if (user1 != null) {

                    if (parts[2].startsWith("user")) {

                        // Check if user1 is formatted correctly
                        try {
                            int user2Id = Integer.parseInt(parts[2].substring(4));

                            if (user1Id == user2Id)
                                return "Some error occurred in unfollow_user.";

                            User user2 = manager.userTree.findUser(user2Id);

                            if (user2 != null) {

                                int result = manager.unfollowUser(user1,user2);

                                if (result == 0){
                                    return parts[1] + " unfollowed " + parts[2] + ".";

                                } else return "Some error occurred in unfollow_user.";

                            }else return "Some error occurred in unfollow_user.";

                        } catch (NumberFormatException e) {
                            System.out.println("Error: The part after 'user' is not a valid integer for the input unfollow user: " + parts[1]);
                            return "Some error occurred in unfollow_user.";
                        }

                    } else return "Some error occurred in unfollow_user.";

                }else return "Some error occurred in unfollow_user.";

            } catch (NumberFormatException e) {
                System.out.println("Error: The part after 'user' is not a valid integer for the input follow unuser: " + parts[1]);
                return "Some error occurred in unfollow_user.";
            }

        } else return "Some error occurred in unfollow_user.";
    }

    private static String handleCreatePost(InstagramManager manager, String[] parts) {

        if (parts[1].startsWith("user")) {
            if (parts[2].startsWith("post")) {

                // Check if user is correctly formatted
                try {
                    int userId = Integer.parseInt(parts[1].substring(4));

                    User user = manager.userTree.findUser(userId);

                    if (user == null){
                        return "Some error occurred in create_post.";
                    }

                    // Check if post is correctly formatted
                    try {
                        int postId = Integer.parseInt(parts[2].substring(4));

                        Post post = manager.postTree.findPost(postId);

                        if (post != null){
                            return "Some error occurred in create_post.";
                        }

                        int result = manager.createPost(user,postId, parts[2]);

                        if (result == 0){
                            return parts[1] + " created a post with Id " + parts[2] + ".";
                        } else return "Some error occurred in create_post.";


                    } catch (NumberFormatException e) {
                        System.out.println("Error: The part after 'post' is not a valid integer for the input create post: " + parts[2]);
                        return "Some error occurred in create_post.";
                    }

                } catch (NumberFormatException e) {
                    System.out.println("Error: The part after 'user' is not a valid integer for the input create post: " + parts[1]);
                    return "Some error occurred in create_post.";
                }
            }else return "Some error occurred in create_post.";
        } else return "Some error occurred in create_post.";
    }

    private static String handleSeePost(InstagramManager manager, String[] parts) {

        if (parts[1].startsWith("user")) {
            if (parts[2].startsWith("post")) {

                // Check if user is correctly formatted
                try {
                    int userId = Integer.parseInt(parts[1].substring(4));

                    User user = manager.userTree.findUser(userId);

                    if (user == null){
                        return "Some error occurred in see_post.";
                    }

                    // Check if post is correctly formatted
                    try {
                        int postId = Integer.parseInt(parts[2].substring(4));

                        Post post = manager.postTree.findPost(postId);

                        if (post == null){
                            return "Some error occurred in see_post.";
                        }

                        manager.seePost(user,post);


                        return parts[1] + " saw " + parts[2] + ".";


                    } catch (NumberFormatException e) {
                        System.out.println("Error: The part after 'post' is not a valid integer for the input see_post: " + parts[2]);
                        return "Some error occurred in see_post.";
                    }

                } catch (NumberFormatException e) {
                    System.out.println("Error: The part after 'user' is not a valid integer for the input see_post: " + parts[1]);
                    return "Some error occurred in see_post.";
                }
            }else return "Some error occurred in see_post.";
        } else return "Some error occurred in see_post.";
    }

    private static String handleSeeAllPosts(InstagramManager manager, String[] parts) {

        if (parts[1].startsWith("user")) {
            if (parts[2].startsWith("user")) {

                // Check if user1 is correctly formatted
                try {
                    int user1Id = Integer.parseInt(parts[1].substring(4));

                    User user1 = manager.userTree.findUser(user1Id);

                    if (user1 == null){
                        return "Some error occurred in see_all_posts_from_user.";
                    }

                    // Check if user2 is correctly formatted
                    try {

                        int user2Id = Integer.parseInt(parts[2].substring(4));

                        User user2 = manager.userTree.findUser(user2Id);

                        if (user2 == null){
                            return "Some error occurred in see_all_posts_from_user.";
                        }

                        manager.seeAllPosts(user1,user2);

                        return parts[1] + " saw all posts of " + parts[2] + ".";


                    } catch (NumberFormatException e) {
                        System.out.println("Error: The part after 'user' is not a valid integer for the input see_all_posts_from_user " + parts[2]);
                        return "Some error occurred in see_all_posts_from_user.";
                    }

                } catch (NumberFormatException e) {
                    System.out.println("Error: The part after 'user' is not a valid integer for the input see_all_posts_from_user " + parts[1]);
                    return "Some error occurred in see_all_posts_from_user.";
                }
            }else return "Some error occurred in see_all_posts_from_user.";
        } else return "Some error occurred in see_all_posts_from_user.";
    }

    private static String handleToggleLike(InstagramManager manager, String[] parts) {

        if (parts[1].startsWith("user")) {
            if (parts[2].startsWith("post")) {

                // Check if user is correctly formatted
                try {
                    int userId = Integer.parseInt(parts[1].substring(4));

                    User user = manager.userTree.findUser(userId);

                    if (user == null){
                        return "Some error occurred in toggle_like.";
                    }

                    // Check if post is correctly formatted
                    try {
                        int postId = Integer.parseInt(parts[2].substring(4));

                        Post post = manager.postTree.findPost(postId);

                        if (post == null){
                            return "Some error occurred in toggle_like.";
                        }

                        int result = user.seenPostTree.toggleLikePost(post);

                        return switch (result) {
                            case 0, 1 -> parts[1] + " liked " + parts[2] + ".";
                            case 2 -> parts[1] + " unliked " + parts[2] + ".";
                            default -> {
                                System.out.println("Error: Some error occurred when implementing toggle_like.");
                                yield "Some error occurred in toggle_like.";
                            }
                        };

                    } catch (NumberFormatException e) {
                        System.out.println("Error: The part after 'post' is not a valid integer for the input toggle_like: " + parts[2]);
                        return "Some error occurred in toggle_like.";
                    }

                } catch (NumberFormatException e) {
                    System.out.println("Error: The part after 'user' is not a valid integer for the input toggle_like: " + parts[1]);
                    return "Some error occurred in toggle_like.";
                }
            }else return "Some error occurred in toggle_like.";
        } else return "Some error occurred in toggle_like.";
    }

    private static String handleGenerateFeed(InstagramManager manager, String[] parts) {

        if (parts[1].startsWith("user")) {
            try {
                int userId = Integer.parseInt(parts[1].substring(4));
                User user = manager.userTree.findUser(userId);

                if (user == null)
                    return "Some error occurred in generate_feed.";

                int feedNum = Integer.parseInt(parts[2]);

                String output = manager.createFeed(user,feedNum).toString();

                if (output.endsWith("\n"))
                    output = output.substring(0,output.length()-1);

                return output;

            } catch (NumberFormatException e) {
                System.out.println("Error: The part after 'user' is not a valid integer for the input generate_feed: " + parts[1]);
                return "Some error occurred in generate_feed.";
            }
        } else return "Some error occurred in generate_feed.";

    }

    private static String handleScrollFeed(InstagramManager manager, String[] parts) {

        if (parts[1].startsWith("user")) {
            try {
                int userId = Integer.parseInt(parts[1].substring(4));
                User user = manager.userTree.findUser(userId);

                if (user == null)
                    return "Some error occurred in scroll_through_feed.";

                int feedNum = Integer.parseInt(parts[2]);

                int[] likes = new int[feedNum];

                for (int i = 0; i<feedNum; i++){
                    likes[i] = Integer.parseInt(parts[i+3]);
                }

                manager.createFeed(user,feedNum);

                String output = manager.scrollFeed(user,likes).toString();

                if (output.endsWith("\n"))
                    output = output.substring(0,output.length()-1);

                return output;

            } catch (NumberFormatException e) {
                System.out.println("Error: The part after 'user' is not a valid integer for the input scroll_feed: " + parts[1]);
                return "Some error occurred in scroll_through_feed.";
            }
        } else return "Some error occurred in scroll_through_feed.";

    }

    private static String handleSortPosts(InstagramManager manager, String[] parts) {

        if (parts[1].startsWith("user")) {
            try {
                int userId = Integer.parseInt(parts[1].substring(4));
                User user = manager.userTree.findUser(userId);

                if (user == null)
                    return "Some error occurred in sort_posts.";

                String output = manager.sortPosts(user).toString();

                if (output.endsWith("\n"))
                    output = output.substring(0,output.length()-1);

                return output;

            } catch (NumberFormatException e) {
                System.out.println("Error: The part after 'user' is not a valid integer for the input sort posts: " + parts[1]);
                return "Some error occurred in sort_posts.";
            }
        } else return "Some error occurred in sort_posts.";


    }

}
