/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package caralibro;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;

/**
 *
 * @author Rafa
 */
public class CaraLibro {

    String nombreFicheroInicio;
    int numeroUsuarios, numeroConexiones;
    ArrayList listadoUsuarios = new ArrayList();
    ArrayList<Conexion> listadoConexiones = new ArrayList<Conexion>();
    ArrayList grumos = new ArrayList();

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        CaraLibro claseInicial = new CaraLibro();
        claseInicial.pedirNombreArchivo();
        claseInicial.procesarGrumos();

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
    private void pedirNombreArchivo() {
        boolean lecturaCorrecta = false;

        Scanner miEscanner = new Scanner(System.in);
        System.out.println("Por favor, introduzca el nombre del fichero (el fichero deberá ser de tipo .txt)");
        nombreFicheroInicio = miEscanner.nextLine();
        nombreFicheroInicio += ".txt";
        miEscanner.close();

        leerArchivo();
    }

    /**
     * Lee el fichero de inicio del programa. Procesa cada línea (nUsuarios,
     * nConexiones, conexiones) Comprueba si el fichero existe y se puede leer
     *
     * @return TRUE si el fichero existe y se ha podido procesar, FALSE en caso
     * contrario
     */
    public boolean leerArchivo() {
        Scanner miEscaner;
        int contadorLinea = 0; //Cuenta la linea del archivo en la que me llego

        try {
            miEscaner = new Scanner(new File("DOCS/" + nombreFicheroInicio));
            while (miEscaner.hasNextLine()) {
                switch (contadorLinea) {
                    case 0:
                        numeroUsuarios = Integer.parseInt(miEscaner.nextLine());
                        break;
                    case 1:
                        numeroConexiones = Integer.parseInt(miEscaner.nextLine());
                        break;
                }
                if (contadorLinea > 1) {
                    procesarConexiones(miEscaner.nextLine());
                }
                contadorLinea++;
            }
            //System.out.println("Numero usuarios: " + numeroUsuarios);
            //System.out.println("Numero conexiones: " + numeroConexiones);
            //System.out.println("");
            miEscaner.close();
        } catch (FileNotFoundException fnfEx) {
            System.err.println("No se ha encontrado el archivo " + nombreFicheroInicio);
            return false;
        } catch (Exception ex) {
            System.err.println("Error no controlado.");
            return false;
        }

        return true;
    }

    private void procesarConexiones(String conexion) {
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

    private void procesarGrumos() {
        ArrayList usuariosProcesados = new ArrayList();
        ArrayList usuariosGrumo = new ArrayList();
        int contador = 0;

        //ordenarConexiones();
        // Primera agrupacion de grumos
        for (Object usuario : listadoUsuarios) {
            if (!usuariosProcesados.contains(usuario)) {
                usuariosProcesados.add(usuario);
                usuariosGrumo.add(usuario);

                for (Conexion laConexion : listadoConexiones) {
                    if ((int) laConexion.usuario1 == (int) usuario || laConexion.usuario2 == (int) usuario) {
                        if (!usuariosProcesados.contains(laConexion.usuario1)) {
                            usuariosProcesados.add(laConexion.usuario1);
                        }
                        if (!usuariosProcesados.contains(laConexion.usuario2)) {
                            usuariosProcesados.add(laConexion.usuario2);
                        }
                        if (!usuariosGrumo.contains(laConexion.usuario1)) {
                            usuariosGrumo.add(laConexion.usuario1);
                        }
                        if (!usuariosGrumo.contains(laConexion.usuario2)) {
                            usuariosGrumo.add(laConexion.usuario2);
                        }
                    }
                }
                grumos.add(usuariosGrumo);
                //System.out.println(usuariosGrumo.toString());
            }
            usuariosGrumo = new ArrayList();
        }
        System.out.println(grumos.toString());

        boolean cambiosRealizados = true;

        //Segunda agrupacion de grumos
        int tamanioGrumos = grumos.size();
        int siguiente;
        for (int i = 0; i < tamanioGrumos; i++) {
            
            do {
                System.out.println(grumos.size());
                tamanioGrumos = grumos.size();
                cambiosRealizados = false;

                siguiente = i + 1;
                
                if(siguiente>=tamanioGrumos){
                    break;
                }

                ArrayList grumo1 = (ArrayList) grumos.get(i);
                ArrayList grumo2 = (ArrayList) grumos.get(siguiente); // Empiezo a mirar a partir del segundo grumo

                for (int j = 0; j < grumo2.size(); j++) {
                    if (grumo1.contains(grumo2.get(j))) {
                        cambiosRealizados = true;
                        for (int k = 0; k < grumo2.size(); k++) {
                            if (!grumo1.contains(grumo2.get(k))) {
                                grumo1.add(grumo2.get(k));
                            }
                        }
                    }
                }
                if (cambiosRealizados) {
                    //cambiosRealizados = false;
                    grumos.remove(siguiente);
                }
                System.out.println(grumos.toString());
            }while (cambiosRealizados);
            if(siguiente>=tamanioGrumos){
                    break;
                }
        }
        System.out.println(grumos.toString());
    }

    private void ordenarConexiones() {
        for (Conexion laConexion : listadoConexiones) {
            System.out.println("\t[" + laConexion.usuario1 + "," + laConexion.usuario2 + "]");
        }

        Conexion[] lista = listadoConexiones.toArray(new Conexion[listadoConexiones.size()]);

        boolean done = false;

        while (!done) {
            done = true;

            for (int i = 1; i < lista.length; i++) {
                if (lista[i - 1].usuario1 < lista[i].usuario1) {
                    done = false;
                    Conexion temporal = lista[i];
                    lista[i] = lista[i - 1];
                    lista[i - 1] = temporal;
                }
            }
        }

        System.out.println("ORDENADA");
        for (int i = 0; i < lista.length; i++) {
            System.out.println("\t[" + lista[i].usuario1 + "," + lista[i].usuario2 + "]");
        }

        listadoConexiones.clear();
        for (int i = 0; i < lista.length; i++) {
            listadoConexiones.add(lista[i]);
        }
    }

}
