package it.loreb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Message class used for the exchange of messages between server and host.
 */
public class Message 
{
    /**
     * Tag type of a message. Can be MSG, YES, NO, NAME, LIST or DISCONNECT.
     */
    enum Tag
    {
        MSG,
        YES,
        NO,
        NAME,
        LIST,
        DISCONNECT
    }

    /**
     *Contents of the message.
     */
    private String          contents;
    /**
     *Tag of the message. Indicates what command has been used. If no command has been used, it defaults to MSG.
     */
    private Tag             tag;
    /**
     *Reciever of the message.
     */
    private String          to;
    /**
     *Sender of the message.
     */
    private String          from;

    /**
     * Empty constructor.
     */
    public Message()
    {

    }

    /**
     * Default constructor.
     * @param tag The type of message. Tagging is done to reduce stress on the server's end.
     * @param to Who the message is for.
     * @param from Who sent the message.
     * @param contents The contents of the message. These don't have to be necessarly something the user sees.
     */
    public Message(@JsonProperty("tag")Tag tag, @JsonProperty("to")String to, @JsonProperty("from")String from, @JsonProperty("contents")String contents)
    {
        this.contents = contents;
        this.tag = tag;
        this.to = to;
        this.from = from;
    }
    
    @Override
    public String toString()
    {
        return "tag: " + this.tag + ", from: " + this.from + ", to: " + this.to + ", contents:'" + this.contents + "'";
    }

    /**
     * Method used to get a message from a JSON string.
     * @param str The JSON contents.
     * @return The message.
     * @throws JsonMappingException Thrown when can't properly map to class.
     * @throws JsonProcessingException Thrown when can't process JSON.
     * @throws IOException Thrown when there's a IO error.
     */
    public static Message fromJSON(String str) throws JsonMappingException, JsonProcessingException, IOException
    {
        return new ObjectMapper().readValue(str, Message.class);
    }

    /**
     * Method used to turn the message into a JSON string to be sent through the stream socket.
     * @param m The message.
     * @return The string to be sent.
     * @throws JsonProcessingException Thrown when can't process JSON.
     */
    public static String toJSON(Message m) throws JsonProcessingException
    {
        return new ObjectMapper().writeValueAsString(m);
    }

    /**
     * Method to format a message from a string or command line.
     * @param str The string to be formatted.
     * @param from The name of the client sending this.
     * @return The formatted message.
     * @throws InvalidMessageException Thrown when the message can't be formatted into a valid message type.
     */
    public static Message formatMessage(String str, String from) throws InvalidMessageException
    {
        Message m = new Message();
        m.setFrom(from);
        if (str.startsWith("/"))
        {
            ArrayList<String> str_array = new ArrayList<String>(Arrays.asList(str.split(" ")));
            switch (str_array.get(0).toLowerCase())
            {
                case "/name":
                m.setTag(Tag.NAME);
                if (str_array.size() == 2)
                {
                    str_array.remove(0);
                    m.setContents(String.join(" ", str_array));
                }
                else
                {   
                    throw new InvalidMessageException("Amount of arguments invalid.");
                }
                break;
                case "/msg":
                m.setTag(Tag.MSG);
                if (str_array.size() > 2)
                {
                    str_array.remove(0);
                    m.setTo(str_array.get(0));
                    str_array.remove(0);
                    m.setContents(String.join(" ", str_array));
                }
                else
                {
                    throw new InvalidMessageException("Amount of arguments invalid.");
                }
                break;
                case "/list":
                m.setTag(Tag.LIST);
                m.setTo(from);
                if (str_array.size() != 1)
                {
                    throw new InvalidMessageException("Amount of arguments invalid.");
                }
                break;
                case "/disconnect":
                m.setTag(Tag.DISCONNECT);
                m.setTo(from);
                if (str_array.size() != 1)
                {
                    throw new InvalidMessageException("Amount of arguments invalid.");
                }
                break;
                default:
                throw new InvalidMessageException("No such command exists.");
            }
        }
        else
        {
            m.setTag(Tag.MSG);
            m.setTo("-");
            m.setContents(str);
        }
        return m;
    }

    /**
     * Returns the message's tag.
     * @return The message's tag.
     */
    public Tag getTag() {
        return tag;
    }

    /**
     * Sets the tag.
     * @param tag The tag to be set to.
     */
    public void setTag(Tag tag) {
        this.tag = tag;
    }
    
    /**
     * Returns the message's contents.
     * @return The message's contents.
     */
    public String getContents() {
        return contents;
    }
    
    /**
     * Sets the contents.
     * @param tag The contents to be set to.
    */
    public void setContents(String contents) {
        this.contents = contents;
    }

    /**
     * Returns the reciever.
     * @return The reciever's clientName.
     */
    public String getTo() {
        return to;
    }

    /**
     * Sets the reciever.
     * @param to The reciever's clientName.
     */
    public void setTo(String to) {
        this.to = to;
    }

    /**
     * Returns the sender.
     * @return The sender's clientName.
     */
    public String getFrom() {
        return from;
    }

    /**
     * Sets the sender.
     * @param from The sender's clientName.
     */
    public void setFrom(String from) {
        this.from = from;
    }
    
}
