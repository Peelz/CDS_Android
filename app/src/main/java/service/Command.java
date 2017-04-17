package service;

import model.Constant;

/**
 * Created by gutte on 4/8/2017.
 */

public class Command {

    public Command(){

    }

    public Command(String message){

    }

    public String setChangeGearRequest(String gear){

        return Constant.CMD_CHANGE_GEAR +" "+gear ;
    }

    public String setChangeModeRequest(String mode){

        return Constant.CMD_CHANGE_MODE +" "+mode ;
    }

}
