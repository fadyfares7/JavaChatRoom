package server;

import java.util.LinkedList;
import java.util.List;

public class Group {
    private String name;
    public String creatorName;
    public List<String> members;
    private String socket;
    private groupThread gthread;

    public Group(String name, String creatorName) {
        this.name = name;
        this.creatorName = creatorName;
        members=new LinkedList<String>();
    }
    
    public void addMember(String name)
    {
        members.add(name);
    }

    public void setSocket(String socket) {
        this.socket = socket;
    }

    public void setGthread(groupThread gthread) {
        this.gthread = gthread;
    }

    public String getSocket() {
        return socket;
    }

    public groupThread getGthread() {
        return gthread;
    }

    public String getName() {
        return name;
    }
    
}
