/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package caralibro;

import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author Rafa
 */
public class Analisis {

    String nombreFicheroPrincipal;
    String nombreFicheroNuevasConexiones;
    int numeroUsuarios;
    int numeroConexiones;
    float porcentajeDeseado; // Porcentaje expresado por el usuario
    int cantidadGrumosExistentes;

    ArrayList<Conexion> listadoConexiones = new ArrayList<>(); // Listado de las conexiones obtenido del fichero (futuro 'red')
    ArrayList listadoUsuarios = new ArrayList(); // Listado de todos los usuarios de la red social (futuro 'usr')
    ArrayList grumos = new ArrayList(); // Listado de grumos (futuro 'grus')
    ArrayList<Conexion> conexionesExtra = new ArrayList<>();

    Date tILecturaFichero;// tiempo inicial lectura fichero    
    Date tFLecturaFichero;// tiempo final lectura fichero

    // tiempo creacion lista usuarios
    Date tIListaUsuarios;
    Date tFListaUsuarios;

    // tiempo creacion lista grumos
    Date tIListaGrumos;
    Date tFListaGrumos;

    // tiempo ordenacion y seleccion grumos
    Date tIOrdenarYSeleccionar;
    Date tFOrdenarYSeleccionar;

    Analisis() {
        // Constructor vacio
    }
    
    public float tiempoLectura(){
        float tiempo = tFLecturaFichero.getTime() - tILecturaFichero.getTime();
        float resultado = tiempo / 100;
        return resultado;
    }
    
    public float tiempoListaUsuarios(){
        float tiempo = tFListaUsuarios.getTime() - tIListaUsuarios.getTime();
        float resultado = tiempo / 100;
        return resultado;
    }
    
    public float tiempoListaGrumos(){
        float tiempo = tFListaGrumos.getTime() - tIListaGrumos.getTime();
        float resultado = tiempo / 100;
        return resultado;
    }
    
    public float tiempoOrdenarYSeleccionar(){
        float tiempo = tFOrdenarYSeleccionar.getTime() - tIOrdenarYSeleccionar.getTime();
        float resultado = tiempo / 100;
        return resultado;
    }

    @Override
    public String toString() {
        String salida
                = "ANÁLISIS DE CARALIBRO\n"
                + "---------------------\n"
                + "Fichero principal: " + nombreFicheroPrincipal + ".txt\n"
                + "Lectura Fichero: "
                + "Fichero de nuevas conexiones (pulse enter si no existe): "
                + numeroUsuarios + " usuarios, " + numeroConexiones + " conexiones"
                + "Porcentaje tamaño mayor grumo: ";
        return "Analisis{" + "nombreFicheroPrincipal=" + nombreFicheroPrincipal + ", numeroUsuarios=" + numeroUsuarios + ", numeroConexiones=" + numeroConexiones + '}';
    }

}
