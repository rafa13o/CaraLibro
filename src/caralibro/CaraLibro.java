/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package caralibro;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

/**
 *
 * @author Rafa
 */
public class CaraLibro {

    static ArrayList usuariosProcesados = new ArrayList(); // Usuarios que ya han sido procesados en un grumo
    static Analisis elAnalisis;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        elAnalisis = new Analisis();
        
        ArrayList<Conexion> listadoConexiones = new ArrayList<>(); // Listado de las conexiones obtenido del fichero (futuro 'red')
        ArrayList listadoUsuarios = new ArrayList(); // Listado de todos los usuarios de la red social (futuro 'usr')
        ArrayList usuariosGrumo; // Listado de los usuarios que pertenecen a un grumo
        ArrayList grumos = new ArrayList(); // Listado de grumos (futuro 'grus')
        pedirNombreArchivo();

        if (leerArchivo( listadoConexiones, listadoUsuarios)) { // Se ha leído y procesado correctamente
            for (Object usuario : listadoUsuarios) {
                usuariosGrumo = new ArrayList();
                uber_amigos((int) usuario, listadoConexiones, usuariosGrumo);
                grumos.add(usuariosGrumo);
            }
            quitarVacios(grumos);
            ordenarGrumos(grumos);
            System.out.println(grumos.toString());
        }else{
            
        }

        /*
        System.out.println("Nº Usuarios: " + claseInicial.numeroUsuarios);
        System.out.println("Nº Conexiones: " + claseInicial.numeroConexiones);
        System.out.println("Listado Usuarios: " + claseInicial.listadoUsuarios);
        System.out.println("ListadoConexiones: {");
        for (Conexion laConexion : claseInicial.listadoConexiones) {
            System.out.println("\t[" + laConexion.usuario1 + "," + laConexion.usuario2 + "]");
        }
        System.out.println("}");
        System.out.println("Nº Grumos: "+claseInicial.grumos.size());*/
    }

    /**
     * Pide el nombre del archivo al usuario y agrega el .txt para que se pueda
     * localizar
     */
    private static void pedirNombreArchivo() {
        Scanner miEscanner = new Scanner(System.in);
        System.out.print("Fichero principal: ");
        elAnalisis.nombreFicheroPrincipal = miEscanner.nextLine();
        elAnalisis.nombreFicheroPrincipal += ".txt";
        miEscanner.close();
    }
    
    private static void pedirNombreNuevasConexiones(){
        Scanner miEscanner = new Scanner(System.in);
        System.out.print("Fichero de nuevas conexiones (pulse enter si no existe): ");
        elAnalisis.nombreFicheroPrincipal = miEscanner.nextLine();
    }

    /**
     * Lee el fichero de inicio del programa. Procesa cada línea (nUsuarios,
     * nConexiones, conexiones) Comprueba si el fichero existe y se puede leer
     *
     * @param nombreFicheroInicio Nombre del fichero de lectura que ha indicado
     * el usuario
     * @param listadoConexiones
     * @param listadoUsuarios
     * @return TRUE si el fichero existe y se ha podido procesar, FALSE en caso
     * contrario
     */
    public static boolean leerArchivo(ArrayList listadoConexiones, ArrayList listadoUsuarios) {
        FileInputStream ficheroStream;
        Scanner miEscaner;
        int contadorLinea = 0; //Cuenta la linea del archivo en la que me llego

        try {
            ficheroStream = new FileInputStream("DOCS/" + elAnalisis.nombreFicheroPrincipal);
            miEscaner = new Scanner(ficheroStream, "UTF-8");
            hora();
            while (miEscaner.hasNextLine()) {
                //System.out.println(contadorLinea);
                if (contadorLinea > 1) {
                    procesarConexiones(miEscaner.nextLine(), listadoConexiones, listadoUsuarios);
                } else {
                    switch (contadorLinea) {
                        case 0:
                            elAnalisis.numeroUsuarios = Integer.parseInt(miEscaner.nextLine());
                            break;
                        case 1:
                            elAnalisis.numeroConexiones = Integer.parseInt(miEscaner.nextLine());
                            break;
                    }
                }
                contadorLinea++;
            }
            hora();
            //System.out.println("Numero usuarios: " + numeroUsuarios);
            //System.out.println("Numero conexiones: " + numeroConexiones);
            //System.out.println("");
            ficheroStream.close();
            miEscaner.close();
        } catch (FileNotFoundException fnfEx) {
            System.err.println("No se ha encontrado el archivo " + elAnalisis.nombreFicheroPrincipal);
            return false;
        } catch (Exception ex) {
            System.err.println("Error no controlado.");
            return false;
        }

        return true;
    }

    /**
     *
     * @param conexion
     * @param listadoConexiones
     * @param listadoUsuarios
     */
    private static void procesarConexiones(String conexion, ArrayList listadoConexiones, ArrayList listadoUsuarios) {
        Conexion laConexion;

        String[] listaConexion = conexion.split(" ");
        int usuario1 = Integer.parseInt(listaConexion[0]);
        int usuario2 = Integer.parseInt(listaConexion[1]);
        laConexion = new Conexion(usuario1, usuario2);
        //System.err.println("Conexión: \n\tu1: " + usuario1 + "\n\tu2: " + usuario2);
        listadoConexiones.add(laConexion);

        if (!listadoUsuarios.contains(usuario1)) { //Si el usuario no existe
            listadoUsuarios.add(usuario1);
        }

        if (!listadoUsuarios.contains(usuario2)) {
            listadoUsuarios.add(usuario2);
        }
    }

    /**
     *
     * @param usuarioInicial
     * @param conexiones
     * @param grumo
     */
    private static void uber_amigos(int usuarioInicial, ArrayList<Conexion> conexiones, ArrayList grumo) {
        if (!usuariosProcesados.contains(usuarioInicial)) { // Si ese usuario no se ha procesado todavia
            usuariosProcesados.add(usuarioInicial);
            for (Conexion laConexion : conexiones) {
                if (laConexion.usuario1 == usuarioInicial) {
                    if (!grumo.contains(laConexion.usuario2)) {
                        grumo.add(laConexion.usuario2);
                        uber_amigos(laConexion.usuario2, conexiones, grumo);
                    }
                } else if (laConexion.usuario2 == usuarioInicial) {
                    if (!grumo.contains(laConexion.usuario1)) {
                        grumo.add(laConexion.usuario1);
                        uber_amigos(laConexion.usuario1, conexiones, grumo);
                    }
                }
            }
        }
    }

    private static void quitarVacios(ArrayList listado) {
        boolean unaVacia;
        do {
            unaVacia = false;
            for (int i = 0; i < listado.size(); i++) {
                ArrayList sublista = (ArrayList) listado.get(i);
                if (sublista.isEmpty()) {
                    unaVacia = true;
                    listado.remove(i);
                }
            }
        } while (unaVacia);
    }
    
    private static void ordenarGrumos(ArrayList grumos){
        for (int i = 0; i<grumos.size(); i++){
            int siguiente = i+1;
            if(siguiente>=grumos.size()){
                break;
            }
            ArrayList lista1 = (ArrayList) grumos.get(i);           
            ArrayList lista2 = (ArrayList) grumos.get(siguiente);
            if(lista2.size()>lista1.size()){
                ArrayList listaTemporal = lista2;
                grumos.remove(siguiente);
                grumos.add(i, listaTemporal);
            }
        }
    }

    /**
     * Función que imprime la hora cuando se la invoca
     */
    private static long hora() {
        Date date = new Date();
        return date.getTime();
    }

}
