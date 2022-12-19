package it.loreb;

public class InvalidMessageException extends Exception
{
    public InvalidMessageException(String msg)
    {
        super("The message wasn't valid for the following reasons: " + msg);
    }    
}
