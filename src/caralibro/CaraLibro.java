/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package caralibro;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 *
 * @author Paula Alcalaya Álvarez
 */
public class CaraLibro {

    static ArrayList usuariosProcesados = new ArrayList(); // Usuarios que ya han sido procesados en un grumo
    static Analisis elAnalisis; // Objeto Analisis

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        elAnalisis = new Analisis(); // Creo una nueva instancia del objeto Analisis.

        System.out.println(
                "ANÁLISIS DE CARALIBRO\n"
                + "---------------------");

        // Llamadas necesarias a funciones
        if (!pedirDatos()) {
            System.err.println("El programa se cerrará.");
            return; // Finalizo la ejecución si pedirDatos devolvió FALSO
        };
        crearGrumos();
        System.out.printf("Creación lista grumos: %.5f seg.\n", elAnalisis.tiempoListaGrumos());
        System.out.println("Existen " + elAnalisis.grus.size() + " grumos.");
        ordenarGrumos();
        seleccionarGrumos();
        System.out.printf("Ordenación y selección de grumos: %.5f seg.\n", elAnalisis.tiempoOrdenarYSeleccionar());
    }

    /**
     * Pide el nombre del archivo al usuario y agrega el .txt para que se pueda
     * localizar
     *
     * @return TRUE si ha podido leer los archivos y los datos que ha dado el
     * usuario, FALSE en caso contrario.
     */
    private static boolean pedirDatos() {
        Scanner miEscanner = new Scanner(System.in);
        try {
            // Pedir nombre fichero inicial
            System.out.print("Fichero principal: ");
            elAnalisis.nombreFicheroPrincipal = miEscanner.nextLine(); // Lo que leo, lo guardo en la propiedad nombreFicheroPrincipal del objeto Analisis.
            if (!leerArchivo(true)) {
                return false; // El fichero principal no existe y, por tanto, no se puede leer. Finalizo la ejecución.
            }
            System.out.printf("Lectura fichero: %.5f seg.\n", elAnalisis.tiempoLectura());

            // Pedir fichero de nuevas conexiones
            System.out.print("Fichero de nuevas conexiones (pulse enter si no existe): ");
            elAnalisis.nombreFicheroNuevasConexiones = miEscanner.nextLine();
            if (elAnalisis.nombreFicheroNuevasConexiones != null && !elAnalisis.nombreFicheroNuevasConexiones.equals("")) {
                if (!leerArchivo(false)) {
                    return false;
                }
            }
            procesarConexiones();
            System.out.println(elAnalisis.numeroUsuarios + " usuarios, " + elAnalisis.red.size() + " conexiones");

            // Pedir porcentaje
            System.out.print("Porcentaje tamaño mayor grumo: ");
            elAnalisis.porcentajeDeseado = miEscanner.nextFloat();
            miEscanner.close();

            System.out.printf("Creación lista usuarios: %.5f seg.\n", elAnalisis.tiempoListaUsuarios());
            return true;
        } catch (InputMismatchException imEx) {
            System.err.println("Algún dato no es correcto.");
            return false;
        } catch (Exception ex) {
            System.err.println("Error no controlado.");
            return false;
        }
    }

    /**
     * Lee el fichero de inicio del programa. Procesa cada línea. Comprueba si
     * el fichero existe y se puede leer.
     *
     * @param tipoFichero TRUE si es el inicial, FALSE si es el extra
     * @return TRUE si el fichero existe y se ha podido procesar, FALSE en caso
     * contrario
     */
    public static boolean leerArchivo(boolean tipoFichero) {
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        String archivoALeer; // Archivo que se lee en la ejecucion actual

        if (tipoFichero) {
            archivoALeer = elAnalisis.nombreFicheroPrincipal;
        } else {
            archivoALeer = elAnalisis.nombreFicheroNuevasConexiones;
        }

        try {
            // Para la lectura del fichero
            fileReader = new FileReader("DOCS/" + archivoALeer);
            bufferedReader = new BufferedReader(fileReader);

            elAnalisis.tILecturaFichero = hora(); // Guardo el tiempo de inicio de lectura del fichero

            String lineaLeida; // Linea leida
            while ((lineaLeida = bufferedReader.readLine()) != null) {
                elAnalisis.datosArchivo.add(lineaLeida); // Lo agregro al arrayList datosArchivo del objeto Analisis para su posterior procesamiento
            }

            // Intento cerrar el archivo y su lectura
            fileReader.close();
            bufferedReader.close();

            elAnalisis.tFLecturaFichero = hora(); // Guardo el tiempo final de lectura del fichero
        } catch (FileNotFoundException fnfEx) {
            if (tipoFichero) { // El fichero que ha dado la excepción es el principal
                System.err.println("No se ha encontrado el archivo " + elAnalisis.nombreFicheroPrincipal);
            } else { // El fichero que ha dado la excepción es el de nuevas conexiones
                System.err.println("No se ha encontrado el archivo " + elAnalisis.nombreFicheroNuevasConexiones);
            }
            return false;
        } catch (IOException ioEx) {
            System.err.println("Un archivo, o los dos, no se pudo cerrar bien.");
            return false;
        } catch (Exception ex) { // Cualquier otra excepción no controlada
            System.err.println("Error no controlado.");
            return false;
        }

        return true;
    }

    /**
     * Procesa llas conexiones que haya almacenadas en el archivo y que,
     * anteriormente, han sido guardadas en el ArrayList datosArchivo del objeto
     * Analisis
     */
    private static void procesarConexiones() {
        Conexion laConexion; // Instancia del objeto Conexion

        elAnalisis.tIListaUsuarios = hora(); // Guardo el tiempo de inicio del procesamiento de la lista de usuarios

        elAnalisis.numeroUsuarios = Integer.parseInt(elAnalisis.datosArchivo.get(0).toString());
        elAnalisis.numeroConexiones = Integer.parseInt(elAnalisis.datosArchivo.get(1).toString());

        for (int i = 2; i < elAnalisis.datosArchivo.size(); i++) { // Empiezo en 2 porque las posiciones 0 y 1 son el numero de usuarios y las conexiones
            String conexion = elAnalisis.datosArchivo.get(i).toString(); // Recojo la conexion existente

            /**
             * Creo un nuevo objeto Conexion y le mando como paramtero al
             * constructor la conexión recogida. El constructor será el
             * encargado de separar los usuarios de la conexión, crear la nueva
             * conexión y almacenarla
             */
            laConexion = new Conexion(conexion);
        }
        elAnalisis.tFListaUsuarios = hora(); // Guardo el tiempo de finalización del procesamiento de la lista de usuarios
    }

    /**
     * Función que se llama a sí misma de manera recursiva para encontrar los
     * grumos. COmprueba si el usuario se ha procesado ya. Si no es así, lo
     * añade al ArrayList usuariosProcesados. Luego, recorre la lista de
     * conexiones buscando una conexión con otro usuario. Si la encuentra,
     * agrega al ArrayList grumo a ambos usuarios de la conexiones si estos ya
     * no estaban en el grumo. Una vez hecho eso, se vuelve a llamar con nuevos
     * parámetros.
     *
     * @param usuarioInicial usuario del que se quiere saber su grumo
     * (conexiones)
     * @param conexiones listado de conexiones existentes
     * @param grumo listado con los usuarios que pertenecen a un grumo
     */
    private static void uber_amigos(int usuarioInicial, ArrayList<Conexion> conexiones, ArrayList grumo) {
        if (!usuariosProcesados.contains(usuarioInicial)) { // Si ese usuario no se ha procesado todavia
            usuariosProcesados.add(usuarioInicial); // Agrego el usuario a usuariosProcesados
            for (Conexion laConexion : conexiones) { // Recorro la lista de conexiones
                if (laConexion.usuario1 == usuarioInicial) { // El usuario1 de la conexión es el usuarioInicial que estábamos procesando
                    if (!grumo.contains(laConexion.usuario2)) { // Si el grumo todavía no contiene al otro usuario (usuario2 de la conexion), lo agrega
                        grumo.add(laConexion.usuario2);
                        uber_amigos(laConexion.usuario2, conexiones, grumo); // Se vuelve a llamar de forma recursiva
                    }
                } else if (laConexion.usuario2 == usuarioInicial) { // El usuario2 de la conexión es el usuarioInicial que estábamos procesando
                    if (!grumo.contains(laConexion.usuario1)) { // Si el grumo todavía no contiene al otro usuario (usuario1 de la conexion), lo agrega
                        grumo.add(laConexion.usuario1);
                        uber_amigos(laConexion.usuario1, conexiones, grumo); // Se vuelve a llamar de forma recursiva
                    }
                }
            }
        }
    }

    /**
     * Elimina las posiciones vacías de cualquier ArrayList que se le pase por
     * parámetro
     *
     * @param listado ArrayList del que se desean eliminar las posiciones vacías
     */
    private static void quitarVacios(ArrayList listado) {
        boolean unaVacia; // Existió una posición vacía
        do {
            unaVacia = false;
            for (int i = 0; i < listado.size(); i++) {
                ArrayList sublista = (ArrayList) listado.get(i); // Recojo cada lista que contiene el ArrayList
                if (sublista.isEmpty()) { // Comprueba si la lista está vacía
                    unaVacia = true;
                    listado.remove(i); // Elimina la lista vacía
                }
            }
        } while (unaVacia);
    }

    /**
     * Ordena los grumos en función de su número de usuarios. Cuanto más grande
     * sea el número de usuarios, antes estará almacenado en el ArrayList de
     * grumos. Esto es así para que, a la hora de unir grumos, sean los menores
     * posibles.
     */
    private static void ordenarGrumos() {
        elAnalisis.tIOrdenarYSeleccionar = hora(); // Almaceno el tiempo de inicio de Ordenar y Seleccionar Grumos
        for (int i = 0; i < elAnalisis.grus.size(); i++) {
            int siguiente = i + 1;
            if (siguiente >= elAnalisis.grus.size()) { // No hay siguiente posición
                break; // Se corta la ejecución
            }
            ArrayList lista1 = (ArrayList) elAnalisis.grus.get(i); //Cojo un grumo de la lista
            ArrayList lista2 = (ArrayList) elAnalisis.grus.get(siguiente); // Cojo el siguiente grumo de la lista
            if (lista2.size() > lista1.size()) { // Compruebo si el segundo grumo es mayor que el primero
                ArrayList listaTemporal = lista2; //Variable temporal para no perder los datos
                elAnalisis.grus.remove(siguiente); // Elimino la posición en la que estaba el segundo grumo
                elAnalisis.grus.add(i, listaTemporal); // Agrego el grumo temporal a la posición del primer grumo cojido (lo desplaza)
            }
        }
    }

    /**
     * Función que va seleccionando los grumos necesarios para llegar al
     * porcentaje solicitado por el usuario
     */
    private static void seleccionarGrumos() {
        int cantidadUsuarios = 0; // Número de usuarios que habrá en el futuro grumo
        float porcentaje = 0.0f; // Porcentaje que se ha conseguido hasta el momento
        ArrayList grumosSeleccionados = new ArrayList(); // Grumos seleccionados para cumplir con el porcentaje exigido

        for (int i = 0; i < elAnalisis.grus.size(); i++) {
            ArrayList grumo1 = (ArrayList) elAnalisis.grus.get(i);
            grumosSeleccionados.add(grumo1);

            cantidadUsuarios += grumo1.size();
            porcentaje = (float) (cantidadUsuarios * 100 / elAnalisis.numeroUsuarios);

            if (porcentaje > elAnalisis.porcentajeDeseado) {
                break;
            }
        }

        if (grumosSeleccionados.size() > 1) {
            System.out.println("Se deben unir los " + grumosSeleccionados.size() + " mayores");
            for (int i = 0; i < grumosSeleccionados.size(); i++) {
                int numeroGrumo = i + 1;
                ArrayList grumo = (ArrayList) grumosSeleccionados.get(i);
                int cantidadUsuariosEnGrumo = grumo.size();
                float porcentajeGrumo = cantidadUsuariosEnGrumo * 100 / elAnalisis.numeroUsuarios;
                System.out.println("#" + numeroGrumo + ": " + cantidadUsuariosEnGrumo + " usuarios " + porcentajeGrumo + " %");
            }
            System.out.println("Nuevas relaciones de amistad (salvadas en extra.txt)");
            for (int i = 0; i < grumosSeleccionados.size(); i++) {
                int usuario1, usuario2;
                int siguiente = i + 1;
                if (siguiente >= grumosSeleccionados.size()) {
                    break;
                }
                ArrayList grumo1 = (ArrayList) grumosSeleccionados.get(i);
                ArrayList grumo2 = (ArrayList) grumosSeleccionados.get(siguiente);

                if (grumo1.size() <= siguiente) {
                    usuario1 = (int) grumo1.get(i);
                } else {
                    usuario1 = (int) grumo1.get(i);
                }

                if (grumo2.size() <= siguiente) {
                    usuario2 = (int) grumo2.get(0);
                } else {
                    usuario2 = (int) grumo2.get(siguiente);
                }
                elAnalisis.conexionesExtra.add(new Conexion(usuario1, usuario2));
                System.out.println(usuario1 + " <-> " + usuario2);
            }
            salvarConexiones();
        } else {
            System.out.println("El mayor grumo contiene " + cantidadUsuarios + " usuarios (" + porcentaje + ")%");
            System.out.println("No son necesarias nuevas relaciones de amistad.");
        }
        elAnalisis.tFOrdenarYSeleccionar = hora();
    }

    /**
     * Guarda las conexiones nuevas en el fichero extra.txt. Estas conexiones
     * son las que harán posible que la red de grumos cumpla con el porcentaje
     * deseado por el usuario.
     */
    private static void salvarConexiones() {
        FileWriter fichero = null;
        PrintWriter pw = null;

        try {
            fichero = new FileWriter("DOCS/extra.txt");
            pw = new PrintWriter(fichero);
            for (int i = 0; i < elAnalisis.conexionesExtra.size(); i++) {
                Conexion laConexion = elAnalisis.conexionesExtra.get(i);
                pw.println(laConexion.usuario1 + " " + laConexion.usuario2);
            }
        } catch (IOException ex) {
            System.err.println("Archivo no encontrado.");
        } finally {
            try {
                if (null != fichero) {
                    fichero.close();
                }
            } catch (IOException ex) {
                System.err.println("El fichero no se pudo cerrar correctamente.");
            }
        }

    }

    /**
     * Función que devuelve el tiempo cuando es llamada. Sirve para almacenar
     * correctamente los tiempos de inicio y final
     */
    private static Date hora() {
        Date date = new Date();
        return date;
    }

    /**
     * Recorre el listado de usuarios y ejecuta la función uber_amigos(). Luego,
     * almacena el listado del grumo en el ArrayList de grumos del objeto
     * Analisis
     */
    private static void crearGrumos() {
        ArrayList usuariosGrumo; // Listado de los usuarios que pertenecen a un grumo

        elAnalisis.tIListaGrumos = hora();

        for (Object usuario : elAnalisis.usr) {
            usuariosGrumo = new ArrayList();
            uber_amigos((int) usuario, elAnalisis.red, usuariosGrumo);
            elAnalisis.grus.add(usuariosGrumo);
        }
        quitarVacios(elAnalisis.grus);

        elAnalisis.tFListaGrumos = hora();
    }

}
