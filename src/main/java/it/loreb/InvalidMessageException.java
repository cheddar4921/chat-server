package it.loreb;

/**
 * Exception that is thrown when an inputted message is not a valid message type.
 */
public class InvalidMessageException extends Exception
{
    /**
     * Default constructor.
     * @param msg Reason why the message isn't valid.
     */
    public InvalidMessageException(String msg)
    {
        super("The message wasn't valid for the following reasons: " + msg);
    }    
}
