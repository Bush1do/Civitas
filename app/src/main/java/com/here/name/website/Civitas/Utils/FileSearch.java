package com.here.name.website.Civitas.Utils;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Charles on 12/17/2017.
 */

public class FileSearch {
    //Search directory and return all directories inside
    public static ArrayList<String> getDirectoryPaths(String directory){
        ArrayList<String> pathArray=new ArrayList<>();
        File file=new File(directory);
        File[] listFiles=file.listFiles();
        for(int i=0;i<listFiles.length;i++){
            if(listFiles[i].isDirectory()){
                pathArray.add(listFiles[i].getAbsolutePath());
            }
        }

        return pathArray;
    }


    //Search directory and return all files inside
    public static ArrayList<String> getFilePath(String directory){
        ArrayList<String> pathArray=new ArrayList<>();
        File file=new File(directory);
        File[] listFiles=file.listFiles();
        for(int i=0;i<listFiles.length;i++){
            if(listFiles[i].isFile()){
                pathArray.add(listFiles[i].getAbsolutePath());
            }
        }
        return pathArray;
    }
}
