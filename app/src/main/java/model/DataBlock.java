package model;

/**
 * Created by gutte on 2/1/2017.
 */

public class DataBlock {

    String head ;
    String value;

    public DataBlock(){
        this.head = "";
        this.value = "";
    }

    public DataBlock(String head, int value) {
        this.head = String.valueOf(head) ;
        this.value = String.valueOf(value) ;
    }

    public void setData(String head, String value){
        this.head = head ;
        this.value = value ;
    }

    public void setData(String head, int value){
        this.head = head ;
        this.value = String.valueOf(value);
    }


    void setValue(String value){
        this.value = value;
    }

    void setHead(){
        this.head = head ;
    }

    public String getDataToString(){
        return String.valueOf(head+value);
    }

}
