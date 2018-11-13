package es.ediaz.temporales;

import java.util.Random;

public class GoogleDrive {
    private int uniqueID;
    private static GoogleDrive self;
    
    private GoogleDrive(){
        uniqueID = new Random().nextInt(8);
    }
    
    public static GoogleDrive getGoogleDrive(){
        if(self == null){
            self = new GoogleDrive();
        }
        return self;
    }
    
    public int getUniqueID(){
        return uniqueID;
    }
}
