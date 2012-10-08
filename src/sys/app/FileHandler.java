package sys.app;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import javax.swing.JFileChooser;
import sys.io.Device;
import sys.process.Process;

/**
 *
 * @author Ana Luiza Cunha, Guilherme Kelling, Mauricio Zaquia
 */
public class FileHandler {

    public static String filePath;

    //Transforms the array got from getFile() into various Processes, then put them into the list and return it.
    public static LinkedList<Process> readFileA() throws Exception {
        LinkedList<Process> ps = new LinkedList<Process>();
        String[] temp = new String[3];

        File f = getFile();
        BufferedReader br;

        br = new BufferedReader(new FileReader(f));

        while (br.ready()) {
            temp = br.readLine().split(",");
            int a = Integer.parseInt(temp[0]);
            int b = Integer.parseInt(temp[1]); 
            int c = Integer.parseInt(temp[2]);
            Process p = new Process(Math.abs(a), Math.abs(b),Math.abs(c));
            ps.add(p);
        }
        return ps;

    }
    
    public static LinkedList<Device> readFileB() throws Exception {
        LinkedList<Device> ps = new LinkedList<Device>();
        String[] temp = new String[2];

        File f = getFile();
        BufferedReader br;

        br = new BufferedReader(new FileReader(f));

        while (br.ready()) {
            temp = br.readLine().split(",");
            String a = temp[0];
            int b = Integer.parseInt(temp[1]);
            Device d = new Device(a, Math.abs(b));
            ps.add(d);
        }
        return ps;

    }

    //Open File Chooser and returns the selected file.
    private static File getFile() {
        JFileChooser fc = new JFileChooser();
        fc.addChoosableFileFilter(new TextFileFilter());
        int var = fc.showOpenDialog(fc);
        File f = fc.getSelectedFile();
        filePath = f.getAbsolutePath();
        return f;
    }
    
    //Filter only .txt files
    private static class TextFileFilter extends javax.swing.filechooser.FileFilter {

        @Override
        public boolean accept(File file) {
            return (file.getName().toLowerCase().endsWith(".txt")
                    || file.isDirectory());
        }

        @Override
        public String getDescription() {
            return "Text File (*.txt)";
        }
    }

    //Open file chooser to save the file into the specified place.
    public static boolean saveFile(String t) {
        JFileChooser fc = new JFileChooser();
        fc.addChoosableFileFilter(new TextFileFilter());
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int var = fc.showSaveDialog(fc);
        File f = fc.getSelectedFile();
        if (writeFile(f, t)) {
            return true;
        } else {
            return false;
        }

    }

    //Transforms the string into the selected txt file.
    private static boolean writeFile(File f, String t) {
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(f.getAbsolutePath()+".txt"));
            bw.write(t);
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException e) {
            }
        }
    }
}
