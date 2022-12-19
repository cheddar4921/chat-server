package it.loreb;

import java.io.File;
import java.io.IOException;

public class Main 
{
    public static void main( String[] args ) throws IOException
    {
        try
        {
            File dir = new File("logs");
            if (!dir.exists())
            {
                dir.mkdir();
            }
            Server mainServer = new Server(25575);
            mainServer.run();
            System.out.println("Done");
        }
        catch (Exception e)
        {
            System.out.println("Something went wrong in the server. " + e.getMessage());
        }
        
    }
}
