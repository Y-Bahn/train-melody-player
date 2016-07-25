package io.ybahn.trainmelody;

import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class MusicDirectory {

    public String[] getAllTitles() {
        List<File> f =  getAllFileList();
        List<String> ls = new ArrayList<String>();
        for (File f2:f) {
            ls.add(f2.getName());
        }
        return ls.toArray(new String[0]);
    }

    public String[] getAllPaths() {
        List<File> f =  getAllFileList();
        List<String> lp = new ArrayList<String>();
        for (File f2:f) {
            lp.add(f2.getAbsolutePath());
        }
        return lp.toArray(new String[0]);
    }

    private List<File> getAllFileList() {
        File[] f = getStorageDir().listFiles();
        List<File> lf = Arrays.asList(f);
        List<File> fullFileList = getFileObjectList(lf);
        return fullFileList;
    }

    private List<File> getFileObjectList(List<File> l) {
        //回す
        List<File> ret = new ArrayList<File>();
        Iterator<File> it = l.iterator();
        while (it.hasNext()) {
            File f = it.next();
            if (f.isDirectory()) {
                //再帰
                List<File> lx = getFileObjectList(Arrays.asList(f.listFiles()));
                ret.addAll(lx);
            } else {
                ret.add(f);
            }
        }
        return ret;
    }

    private File getStorageDir () {
        File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
        return directory;
    }
}
