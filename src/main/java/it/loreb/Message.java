package it.loreb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Message 
{
    enum Tag
    {
        MSG,
        YES,
        NO,
        NAME,
        LOG,
        LIST,
        DISCONNECT
    }

    private String          contents;
    private Tag             tag;
    private String          to;
    private String          from;

    public Message()
    {

    }

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

    public static Message fromJSON(String str) throws JsonMappingException, JsonProcessingException, IOException
    {
        return new ObjectMapper().readValue(str, Message.class);
    }

    public static String toJSON(Message m) throws JsonProcessingException
    {
        return new ObjectMapper().writeValueAsString(m);
    }

    public static Message empty()
    {
        return new Message();
    }

    public static boolean isEmpty(Message e)
    {
        return e.contents.isEmpty();
    }

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
                case "/log":
                m.setTag(Tag.LOG);
                if (str_array.size() == 2)
                {
                    m.setContents(str_array.get(1));
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

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }
    
    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
    
}