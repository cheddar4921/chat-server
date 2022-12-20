package it.loreb;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.fasterxml.jackson.core.JsonProcessingException;

public class Server implements Runnable
{
    private static int              _id = 0; //ids of the workers

    private int                     port = 25575; //port - defaults to 25575
    private boolean                 running = false; //running state of server
    private ServerSocket            sSocket; //socket responsable for accepts
    private Socket                  cSocket; //individual client socket - to be passed to server workers

    //stuff for logging
    private static Logger           logger;
    private static FileHandler      fh;
    private static SimpleFormatter  sf;
    private static File             logFile;

    private ArrayList<Worker>       workers; //array to reference workers
    private ArrayList<Thread>       threads; //array to reference the threads workers are on

    /**
     * @param port Server's port.
     * @throws IOException Logger and Socket can throw IOException.
     */
    public Server(int port) throws IOException
    {
        //initializing logger
        logger = Logger.getLogger(Server.class.getName());
        logFile = new File("logs/Server.log");
        if (!logFile.exists())
        {
            logFile.createNewFile();
        }
        fh = new FileHandler(logFile.getPath());
        sf = new SimpleFormatter();
        fh.setFormatter(sf);
        logger.addHandler(fh);
        logger.info("Hello world!");

        this.port = validatePort(port);
        sSocket = new ServerSocket(this.port);

        threads = new ArrayList<Thread>();
        workers = new ArrayList<Worker>();
    }

    @Override
    public void run() 
    {
        running = true;
        while (running)
        {
            try 
            {
                cSocket = sSocket.accept();
                Worker newWorker = new Worker(this, cSocket, _id);
                Thread newThread = new Thread(newWorker);
                newThread.setName(String.valueOf(_id));

                newThread.start();
                workers.add(newWorker);
                threads.add(newThread);
                logger.info("A client has connected on worker ID " + _id);
                _id++;
            } 
            catch (IOException e) 
            {
                logger.severe("Something went wrong in the stream of the socket. " + e.getMessage());
            }
        }
    }

    public void sendToClient(Message m)
    {
        for (int i = 0; i < workers.size(); i++)
        {
            try
            {
                if ((workers.get(i).getClientName().equals(m.getTo())) || (m.getTo().equals("-")))
                {
                    workers.get(i).sendToClient(m);
                }
            }
            catch (JsonProcessingException jsone)
            {
                logger.severe("Erroring in processing JSON for message. Error occurred on worker " + workers.get(i).getID() + ". Exception: " + jsone.getMessage());
            }
            catch (IOException ioe)
            {
                logger.severe("Erroring in message stream. Error occurred on worker " + workers.get(i).getID() + ". Exception: " + ioe.getMessage());
            }
        }
    }

    public boolean isNameFree(String name)
    {
        if ((name.equals("SERVER")) || (name.equals("Guest")) || (name.equals("-")))
        {
            return false;
        }
        for (int i = 0; i < workers.size(); i++)
        {
            if (workers.get(i).getClientName().equals(name))
            {
                return false;
            }
        }
        return true;
    }

    public ArrayList<String> clientsConnected()
    {
        ArrayList<String> str = new ArrayList<String>();
        for (int i = 0; i < workers.size(); i++)
        {
            str.add(workers.get(i).getClientName());
        }
        return str;
    }

    public void removeWorker(int id)
    {
        for (int i = 0; i < workers.size(); i++)
        {
            if (workers.get(i).getID() == id)
            {
                workers.remove(i);
            }
        }
        for (int i = 0; i < threads.size(); i++)
        {
            if (threads.get(i).getName().equals(String.valueOf(id)))
            {
                try
                {
                    threads.get(i).join();
                }
                catch (InterruptedException ie)
                {
                    logger.warning("Interrupted exception from thread ID " + id + ". Exception: " + ie.getMessage());
                }
                threads.remove(i);
            }
        }
    }  

    /**
     * Validates the port number inserted when executing the program.
     * @param port The port number to validate.
     * @return Returns the port number if valid. If not valid, returns 25575 to use as default port
     */
    public int validatePort(int port)
    {
        if ((port > 0) && (port < 65536))
        {
            return port;
        }
        else
        {
            logger.warning("PORT NUMBER INVALID. Using 25575 instead.");
            return 25575;
        }
    }
    
}
