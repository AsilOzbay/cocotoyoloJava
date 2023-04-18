import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

public class Main {
    private static final DecimalFormat df = new DecimalFormat("0.000000",new DecimalFormatSymbols(Locale.US));


    public static void main(String[] args) {
        //reading file
        double[] headresults=new double[4];
        double[] bodyresults = new double[4];
        String maininput=new String();
        try {

            File myObj = new File("C:\\Users\\sikeratar\\Desktop\\odgttoyolo\\annotation_train.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                maininput+=data;
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        //spiting
        String[] lines = maininput.split("ID");
        for (int s=1;s< lines.length;s++) {
            ArrayList<String> head = new ArrayList<>();
            ArrayList<String> body = new ArrayList<>();
            String[] inlines = lines[s].split("tag");
            //taking id

            String id = new String();
            for (int i = 4; i < inlines[0].length(); i++) {
                if (inlines[0].charAt(i + 4) == 'g') {
                    break;
                }
                id += inlines[0].charAt(i);
            }
            System.out.println("id " + id);
            //taking humans and heads

            for (int k = 1; k < inlines.length; k++) {

                String headdata = new String();
                boolean hashead=true;
                if (inlines[k].lastIndexOf("hbox")==-1){hashead=false;}
                if (hashead) {
                    for (int j = inlines[k].lastIndexOf("hbox")+8;; j++) {
                        if (inlines[k].charAt(j) == ']') {
                            break;
                        }
                        headdata += inlines[k].charAt(j);
                    }
                }
                String bodydata = new String();
                boolean hasbody=true;
                if (inlines[k].lastIndexOf("vbox")==-1){hasbody=false;}
                if (hasbody) {
                    for (int j = inlines[k].lastIndexOf("vbox")+8; ; j++) {
                        if (inlines[k].charAt(j) == ']') {
                            break;
                        }
                        bodydata += inlines[k].charAt(j);
                    }
                }
                //taking height and width
                BufferedImage bimg = null;
                try {
                    bimg = ImageIO.read(new File("C:\\Users\\sikeratar\\Desktop\\odgttoyolo\\dataset\\images\\" + id + ".jpg"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                int width = bimg.getWidth();
                int height = bimg.getHeight();
                //formating datas
                String[] headcord = headdata.split(", ");
                String[] bodycord = bodydata.split(", ");
                double[] headxy = new double[4];
                double[] bodyxy = new double[4];

                for (int g = 0; g < 4; g++) {
                    if (hashead) {
                        headxy[g] = Double.parseDouble(headcord[g]);
                    }if (hasbody) {
                    bodyxy[g] = Double.parseDouble(bodycord[g]);}

                }
                if (hashead){
                headresults[0]=(headxy[0] + headxy[2] / 2) / width;
                headresults[1]=(headxy[1] + headxy[3] / 2) / height;
                headresults[2]=headxy[2] / width;
                headresults[3]=headxy[3] / height;
                for (int c=0;c<4;c++){
                    if (headresults[c]>1){headresults[c]=1;}
                    if (headresults[c]<0){headresults[c]=0;}
                }
                }
                if(hasbody) {
                    bodyresults[0]=(bodyxy[0] + bodyxy[2] / 2) / width;
                    bodyresults[1]=(bodyxy[1] + bodyxy[3] / 2) / height;
                    bodyresults[2]=bodyxy[2] / width;
                    bodyresults[3]=bodyxy[3] / height;
                    for (int c=0;c<4;c++){
                        if (bodyresults[c]>1){bodyresults[c]=1;}
                        if (bodyresults[c]<0){bodyresults[c]=0;}
                    }
                }
                if (headresults[3]==bodyresults[3]){hashead=false;hasbody=false;}
                if (hashead) {
                    head.add("0 " + df.format( headresults[0])+ " " + df.format(headresults[1]) + " " + df.format(headresults[2]) + " " + df.format(headresults[3]));
                }
                if (hasbody) {
                    body.add("1 " + df.format(bodyresults[0]) + " " + df.format((bodyresults[1])) + " " + df.format(bodyresults[2]) + " " + df.format(bodyresults[3]));
                }
            }
            //writing file
            File fout = new File("C:\\Users\\sikeratar\\Desktop\\odgttoyolo\\dataset\\labels\\" + id + ".txt");
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(fout);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

            for (int i = 0; i < body.size(); i++) {
                try {

                    bw.write(head.get(i));
                    bw.newLine();
                    bw.write(body.get(i));
                    bw.newLine();
                    if (head.get(i)==body.get(i)){break;}
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            try {
                bw.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            body.clear();
            head.clear();
        }
    }
}