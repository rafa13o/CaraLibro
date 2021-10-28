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

        ArrayList usuariosGrumo; // Listado de los usuarios que pertenecen a un grumo

        // Llamadas necesarias a funciones
        pedirNombreArchivo();

        if (leerArchivo()) { // Se ha leído y procesado correctamente
            for (Object usuario : elAnalisis.listadoUsuarios) {
                usuariosGrumo = new ArrayList();
                uber_amigos((int) usuario, elAnalisis.listadoConexiones, usuariosGrumo);
                elAnalisis.grumos.add(usuariosGrumo);
            }
            quitarVacios(elAnalisis.grumos);
            //System.out.println(elAnalisis.grumos.toString());
        } else {

        }
        
        //pedirPorcentaje();
        ordenarGrumos();
        seleccionarGrumos();

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
        
        // Pedir nombre fichero inicial
        System.out.print("Fichero principal: ");
        elAnalisis.nombreFicheroPrincipal = miEscanner.nextLine();
        elAnalisis.nombreFicheroPrincipal += ".txt";
        
        // Pedir porcentaje
        System.out.print("Indicque porcentaje: ");
        elAnalisis.porcentajeDeseado = miEscanner.nextFloat();
        miEscanner.close();
    }

    private static void pedirNombreNuevasConexiones() {
        Scanner miEscanner = new Scanner(System.in);
        System.out.print("Fichero de nuevas conexiones (pulse enter si no existe): ");
        elAnalisis.nombreFicheroPrincipal = miEscanner.nextLine();
        miEscanner.close();

    }

    /*private static void pedirPorcentaje() {
        miEscanner.reset();
        elAnalisis.porcentajeDeseado = miEscanner.nextFloat();
        miEscanner.close();
    }*/

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
    public static boolean leerArchivo() {
        FileInputStream ficheroStream;
        Scanner miEscaner;
        int contadorLinea = 0; //Cuenta la linea del archivo en la que me llego

        try {
            ficheroStream = new FileInputStream("DOCS/" + elAnalisis.nombreFicheroPrincipal);
            miEscaner = new Scanner(ficheroStream, "UTF-8");
            elAnalisis.tILecturaFichero = hora();
            while (miEscaner.hasNextLine()) {
                //System.out.println(contadorLinea);
                if (contadorLinea > 1) {
                    procesarConexiones(miEscaner.nextLine(), elAnalisis.listadoConexiones, elAnalisis.listadoUsuarios);
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
            elAnalisis.tFLecturaFichero = hora();
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

    /**
     * Elimina las posiciones vacías de cualquier ArrayList que se le pase por
     * parámetro
     *
     * @param listado
     */
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

    private static void ordenarGrumos() {
        for (int i = 0; i < elAnalisis.grumos.size(); i++) {
            int siguiente = i + 1;
            if (siguiente >= elAnalisis.grumos.size()) {
                break;
            }
            ArrayList lista1 = (ArrayList) elAnalisis.grumos.get(i);
            ArrayList lista2 = (ArrayList) elAnalisis.grumos.get(siguiente);
            if (lista2.size() > lista1.size()) {
                ArrayList listaTemporal = lista2;
                elAnalisis.grumos.remove(siguiente);
                elAnalisis.grumos.add(i, listaTemporal);
            }
        }
    }

    private static void seleccionarGrumos() {
        // TODO seleccionar los grumos a juntar
        // TODO juntar los mayores grumos
        // TODO imprimir número de usuarios y porcentaje con respecto al total de usuarios
        int cantidadUsuarios = 0;
        float porcentaje = 0.0f;
        ArrayList grumosSeleccionados = new ArrayList();
        for (int i = 0; i < elAnalisis.grumos.size(); i++) {
            ArrayList grumo1 = (ArrayList) elAnalisis.grumos.get(i);
            grumosSeleccionados.add(grumo1);

            cantidadUsuarios += grumo1.size();
            porcentaje = (float) (cantidadUsuarios*100/elAnalisis.numeroUsuarios);

            if (porcentaje >= elAnalisis.porcentajeDeseado) {
                break;
            }
        }
        
        if(grumosSeleccionados.size() > 1){
            System.out.println("Se deben unir los "+grumosSeleccionados.size()+" mayores");
            for(int i = 0; i<grumosSeleccionados.size(); i++){
                int numeroGrumo = i+1;
                ArrayList grumo = (ArrayList) grumosSeleccionados.get(i);
                int cantidadUsuariosEnGrumo = grumo.size();
                float porcentajeGrumo = cantidadUsuariosEnGrumo*100/elAnalisis.numeroUsuarios;
                System.out.println("#"+numeroGrumo+": "+cantidadUsuariosEnGrumo+" usuarios "+porcentajeGrumo+" %");
            }
            System.out.println(grumosSeleccionados.toString());
            System.out.println("Nuevas relaciones de amistad (salvadas en extra.txt");
            for(int i = 0; i<grumosSeleccionados.size(); i++){
                int usuario1, usuario2;
                int siguiente = i+1;
                if(siguiente >= grumosSeleccionados.size()){
                    break;
                }
                ArrayList grumo1 = (ArrayList) grumosSeleccionados.get(i);
                ArrayList grumo2 = (ArrayList) grumosSeleccionados.get(siguiente);
                
                if(grumo1.size() <= siguiente){
                    usuario1 = (int) grumo1.get(i);
                }else{
                    usuario1 = (int) grumo1.get(i);
                }
                
                if(grumo2.size()<=siguiente){
                    usuario2 = (int) grumo2.get(0);
                }else{
                    usuario2 = (int) grumo2.get(siguiente);
                }
                System.out.println(usuario1+" <-> "+usuario2);
                
            }
        }else{
            System.out.println("El mayor grumo contiene "+cantidadUsuarios+" usuarios ("+porcentaje+")%");
            System.out.println("No son necesarias nuevas relaciones de amistad");
        }
        
    }

    /**
     * Función que imprime la hora cuando se la invoca
     */
    private static Date hora() {
        Date date = new Date();
        return date;
    }

}
