package moba.cds;

/**
 * Created by gutte on 4/19/2017.
 */

public class CommandBlock {

    private String head = "";
    private String arg = "" ;

    private String cmd = "";

    public CommandBlock(String message){
        this.cmd = message;
        String[] str = message.split(" ");

        this.head = str[0] ;
        this.arg = str[1];

    }

    public String getFull(){
        return this.cmd ;
    }

    public String getHead(){
        return  this.head ;
    }

    public String getArg(){
        return  this.arg;
    }
}
