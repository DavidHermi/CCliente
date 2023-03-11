import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Cliente   {






        private Socket socket;
        private DataInputStream EntradaDatos;
        private DataOutputStream SalidaDatos ;
        Scanner teclado = new Scanner(System.in);
        final String COMANDO_TERMINACION = "salir()";

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

        public void abrirFlujos() {
            try {
                EntradaDatos = new DataInputStream(socket.getInputStream());
               SalidaDatos = new DataOutputStream(socket.getOutputStream());
                SalidaDatos.flush();
            } catch (IOException e) {
                mostrarTexto("Error en la apertura de flujos");
            }
        }

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
            }finally{
                System.exit(0);
            }
        }

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

        public void recibirDatos() {
            String st = "";
            try {
                do {
                     EntradaDatos.readUTF();
                    mostrarTexto("\n[Servidor] => " + st);
                    System.out.print("\n[Usted] => ");
                } while (!st.equals(COMANDO_TERMINACION));
            } catch (IOException e) {}
        }

        public void escribirDatos() {
            String entrada = "";
            while (true) {
                System.out.print("[Usted] => ");
                entrada = teclado.nextLine();
                if(entrada.length() > 0)
                    enviar(entrada);
            }
        }

        public static void main(String[] argumentos) {

        }




}

