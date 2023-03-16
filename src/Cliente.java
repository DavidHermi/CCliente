import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Cliente {


    private Socket socket;
    private DataInputStream EntradaDatos;
    private DataOutputStream SalidaDatos;
    Scanner teclado = new Scanner(System.in);

    //Mismo metodo para levantar conexion
    public void levantarConexion(String ip, int puerto) {
        try {
            socket = new Socket(ip, puerto);
            mostrarTexto("Conectado a :" + socket.getInetAddress().getHostName());
        } catch (Exception e) {
            mostrarTexto("Excepción al levantar conexión: " + e.getMessage());
            System.exit(0);
        }
    }

    public static void mostrarTexto(String s) {
        System.out.println(s);
    }

    //Metodo abrir flujos
    public void abrirFlujos() {
        try {
            EntradaDatos = new DataInputStream(socket.getInputStream());
            SalidaDatos = new DataOutputStream(socket.getOutputStream());
            SalidaDatos.flush();
        } catch (IOException e) {
            mostrarTexto("Error en la apertura de flujos");
        }
    }

    //Metodo de enviar mensaje
    public void enviar(String s) {
        try {
            SalidaDatos.writeUTF(s);
            SalidaDatos.flush();
        } catch (IOException e) {
            mostrarTexto("IOException on enviar");
        }
    }

    public void cerrarConexion() {
        try {
            EntradaDatos.close();
            SalidaDatos.close();
            socket.close();
            mostrarTexto("Conexión terminada");
        } catch (IOException e) {
            mostrarTexto("IOException on cerrarConexion()");
        } finally {
            System.exit(0);
        }
    }

    //Metodo de ejecutar la conexion
    public void ejecutarConexion(String ip, int puerto) {
        Thread hilo = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    levantarConexion(ip, puerto);
                    abrirFlujos();
                    recibirDatos();
                } finally {
                    cerrarConexion();
                }
            }
        });
        hilo.start();
    }

    //Metodo recibirDatos
    public void recibirDatos() {
        String st = "";
        try {
            do {
                st = EntradaDatos.readUTF();
                mostrarTexto("\n[Servidor] => " + st);
                System.out.print("\n[Usted] => ");
            } while (true);
        } catch (IOException e) {
            cerrarConexion();
        }
    }

    //Metodo escribir datos
    public void escribirDatos() {
        String entrada = "";
        while (true) {
            System.out.print("[Usted] => ");
            entrada = teclado.nextLine();
            if (entrada.length() > 0)
                enviar(entrada);
        }
    }

    public static void main(String[] argumentos) {
        Cliente cliente = new Cliente();
        Scanner escaner = new Scanner(System.in);

        mostrarTexto("Ingresa la IP: [localhost por defecto] ");
        String ip = escaner.nextLine();
        if (ip.length() <= 0) ip = "localhost";

        mostrarTexto("Puerto: [5050 por defecto] ");
        String puerto = escaner.nextLine();
        if (puerto.length() <= 0) puerto = "5050";
        cliente.ejecutarConexion(ip, Integer.parseInt(puerto));
        cliente.escribirDatos();
    }

}




