package it.loreb;

import java.io.File;
import java.io.IOException;

public class Main 
{
    public static void main( String[] args ) throws IOException
    {
        int port = 25575;
        if (args.length > 0)
        {
            port = Integer.parseInt(args[0]);
        }
        try
        {
            File dir = new File("logs");
            if (!dir.exists())
            {
                dir.mkdir();
            }
            Server mainServer = new Server(port);
            mainServer.run();
            System.out.println("Done");
        }
        catch (Exception e)
        {
            System.out.println("Something went wrong in the server. " + e.getMessage());
        }
        
    }
}
