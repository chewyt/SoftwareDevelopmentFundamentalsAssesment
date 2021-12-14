package chewyt;

public class Main {
    public static int port;
    public static String docRoot;
    public static String[] docRoots;

    public static void main(String[] args) {
        if (args.length == 0) {
            port = 3000;
            docRoot = "./target";
        } else if (args.length == 2 && args[0].equals("--port")) {
            port = Integer.parseInt(args[1]);
            // System.out.println("TEST ON SYS ARGS\nPort: " + args[0] + " " + args[1]);
            docRoot = "./target";

        } else if (args.length == 4 && args[0].equals("--port") && args[2].equals("--docRoot")) {
            port = Integer.parseInt(args[1]);
            docRoot = args[3];
            // System.out.println("TEST ON SYS ARGS\nPort: " + args[0] + " " + args[1] + " "
            // + args[2] + " " + args[3]);

        } else {
            System.out.println("Wrong command");
            System.exit(1);
        }

        // System.out.println(args[0].equals("--port"));
        // System.out.println(args[0]);

        docRoots = docRoot.split(":");

        //For checking sysArgs display and defaults
        // System.out.println("Port: " + port);
        // System.out.println("DocRoot: ");
        // for (String string : docRoots) {
        //     System.out.println(string);

        }
    }
}
