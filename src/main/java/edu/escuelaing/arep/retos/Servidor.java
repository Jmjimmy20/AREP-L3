package edu.escuelaing.arep.retos;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;

import javax.imageio.ImageIO;

public class Servidor {

    private OutputStream out;
    private BufferedReader in;
    private ServerSocket SS;
    private Socket SC;
    String inputLine;
    String outputLine;

    private static ArrayList<String> Lformatos = new ArrayList<>(Arrays.asList("jpg","png","img"));
    

    public static void main(String[] args) throws IOException {
        new Servidor();
    }


    public Servidor() throws IOException{

        StartM();
        
        while(true){
            try {
                SC = SS.accept();
            } catch (IOException e) {
                System.err.println("No acepta el puerto del cliente.");
                System.exit(1);
            }
            out = SC.getOutputStream();
            in = new BufferedReader(new InputStreamReader(SC.getInputStream()));
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Se Recibe: " + inputLine);
                if (!in.ready()) break;
                outputLine = Llamado();
            }
            if(outputLine != null) {
                String formato = null;
                if (outputLine.length() > 3){
                    formato = outputLine.substring(outputLine.length() - 3);
                }
                if(Lformatos.contains(formato)){
                    try {
                        ShowImage(formato);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    ShowHTML(out);
                }
            }
            SC.close();
        }
    }

    public void ShowHTML(OutputStream out) {
        Scanner scanner = null;
        try {
            scanner = new Scanner( new File("src/main/resources/" + outputLine));
            String htmlString = scanner.useDelimiter("\\Z").next();
            scanner.close();
            byte htmlBytes[] = htmlString.getBytes("UTF-8");
            PrintStream ps = new PrintStream(out);
            DateFormat df = new SimpleDateFormat("EEE, MMM d, yyyy HH:mm:ss z");
            ps.println("HTTP/1.1 200 OK");
            ps.println("Content-Type: text/html; charset=UTF-8");
            ps.println("Date: " + df.format(new Date()));
            ps.println("Connection: close");
            ps.println();
            ps.println(htmlString);

        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            try {
                scanner = new Scanner( new File("src/main/resources/notfound.html"));
                String htmlString = scanner.useDelimiter("\\Z").next();
                scanner.close();
                byte htmlBytes[] = htmlString.getBytes("UTF-8");
                PrintStream ps = new PrintStream(out);
                DateFormat df = new SimpleDateFormat("EEE, MMM d, yyyy HH:mm:ss z");
                ps.println("HTTP/1.1 200 OK");
                ps.println("Content-Type: text/html; charset=UTF-8");
                ps.println("Date: " + df.format(new Date()));
                ps.println("Connection: close");
                ps.println();
                ps.println(htmlString);
            } catch (FileNotFoundException | UnsupportedEncodingException ex) {
                ex.printStackTrace();
            }

        }
    }
    

    public int getPort(){
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 4567;
    }
   
    public void ShowImage(String formato) throws IOException {
        try{
            PrintWriter printW = new PrintWriter(out, true);
            printW.println("HTTP/1.1 200 OK");
            printW.println("Content-Type: image/png\r\n");
            System.out.println(outputLine);
            BufferedImage image= ImageIO.read(new File("src/main/resources/img/" + outputLine));
            ImageIO.write(image, formato, out);
        } catch (IOException e) {
                BufferedImage image= ImageIO.read(new File("src/main/resources/img/trifuerza.png"));
                ImageIO.write(image, formato, out);
        }
    }

    public String Llamado(){
        if (inputLine.contains("GET")) {
            String [] splitedLine = inputLine.split(" ");
            outputLine = splitedLine[1] ;
        }
        return outputLine;
    }

    public void StartM(){
        int port = getPort();
        try {
            SS = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println("No se escucha el puerto: " + port);
            System.exit(1);
        }
        SC = null;
        out = null;
        in = null;
    }

}