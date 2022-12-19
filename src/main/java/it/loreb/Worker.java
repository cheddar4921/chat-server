package it.loreb;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.fasterxml.jackson.core.JsonProcessingException;

import it.loreb.Message.Tag;

public class Worker implements Runnable
{
    private int                 id; //worker id
    private BufferedReader      input; //input stream of socket
    private DataOutputStream    output; //output stream of socket
    private boolean             running; //running state of the worker
    private String              clientName; //the name of the client this worker is connected to

    private Server              parent; //reference to parent object
    private Socket              cSocket; //client's socket

    //stuff for logging
    private static Logger           logger;
    private static FileHandler      fh;
    private static SimpleFormatter  sf;
    private static File             logFile;

    /**
     * @param parent The parent of the worker, for reference.
     * @param cSocket The socket to which the client is connected to.
     * @param id The ID of this worker.
     * @throws IOException Exception caused by errors in the streams.
     */
    public Worker(Server parent, Socket cSocket, int id) throws IOException
    {
        //initializing logger
        logger = Logger.getLogger(Worker.class.getName());
        logFile = new File("logs/Worker-" + id + ".log");
        if (!logFile.exists())
        {
            logFile.createNewFile();
        }
        fh = new FileHandler(logFile.getPath());
        sf = new SimpleFormatter();
        fh.setFormatter(sf);
        logger.addHandler(fh);
        logger.info("Hello world!");

        this.parent = parent;
        this.cSocket = cSocket;
        this.id = id;
        this.clientName = "Guest";

        input = new BufferedReader(new InputStreamReader(cSocket.getInputStream()));
        output = new DataOutputStream(this.cSocket.getOutputStream());
    }

    @Override
    public void run() 
    {
        logger.info("Sending first time message.");
        try
        {
            sendToClient(new Message(Tag.MSG,"-", "SERVER", "Input your name with /name [username] to start chatting!"));
            running = true;
        }
        catch (IOException e)
        {
            //TODO handle this error better
            logger.severe("ERROR. The client's stream doesn't seem to be working. Closing the worker. Exception: " + e.getMessage());
            running = false;
        }
        while (running)
        {
            try 
            {
                String str = input.readLine();

                if (str.isEmpty())
                {
                    break;
                }

                Message m = Message.fromJSON(str);

                logger.info("Message recieved: " + m.toString());
                
                if ((clientName == "Guest"))
                {
                    switch (m.getTag())
                    {
                        case NAME:
                            if (parent.isNameFree(m.getContents()))
                            {
                                clientName = m.getContents();
                                sendToClient(new Message(Tag.YES,"-", "SERVER", "Name accepted."));
                            }
                            else
                            {
                                sendToClient(new Message(Tag.NO,"-", "SERVER", "Name unavailable."));
                            }
                            break;
                        default:
                            sendToClient(new Message(Tag.NO,"-", "SERVER", "You don't have a name yet! Choose a name with /name [username] to start chatting."));
                            break;
                    }
                }
                else
                {
                    switch (m.getTag())
                    {
                        case NAME:
                            if (parent.isNameFree(m.getContents()))
                            {
                                clientName = m.getContents();
                                sendToClient(new Message(Tag.YES,"-", "SERVER", "NAME"));
                            }
                            else
                            {
                                sendToClient(new Message(Tag.NO,"-", "SERVER", "NAME"));
                            }
                            break;
                        case MSG:
                            parent.sendToClient(m);
                            break;
                        case LIST:
                            sendToClient(new Message(Tag.MSG, "-", "SERVER", "Currently connected: " + String.join(", ", parent.clientsConnected())));
                            break;
                        case DISCONNECT:
                            logger.info("Started disconnect protocol.");
                            sendToClient(new Message(Tag.DISCONNECT, "-", "SERVER", "Disconnect requested."));
                            running = false;
                            break;
                        default:
                            sendToClient(new Message(Tag.NO,"-", "SERVER", "NOT FOUND"));
                            break;
                    }
                }
            } 
            catch (IOException ioe) 
            {
                logger.severe("ERROR. Something is wrong in the client's stream. Exception: " + ioe.getMessage());
            }
        }
        logger.info("Worker " + this.id + " shutting down.");
        parent.removeWorker(this.id);
    }

    public void sendToClient(Message m) throws JsonProcessingException, IOException
    {
        String str = Message.toJSON(m);
        output.writeBytes(str + "\n");
        logger.info("Sent MESSAGE: " + m.toString());
    }
    
    public String getClientName()
    {
        return this.clientName;
    }

    public int getID()
    {
        return this.id;
    }
}
