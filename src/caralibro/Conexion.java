package caralibro;

import static caralibro.CaraLibro.elAnalisis;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Rafa
 */
public class Conexion {

    int usuario1;
    int usuario2;

    Conexion(int usuario1, int usuario2) {
        this.usuario1 = usuario1;
        this.usuario2 = usuario2;
    }

    Conexion(String datos) {
        this.usuario1 = Integer.parseInt(datos.split(" ")[0]);
        this.usuario2 = Integer.parseInt(datos.split(" ")[1]);

        elAnalisis.listadoConexiones.add(this);

        if (!elAnalisis.listadoUsuarios.contains(usuario1)) { //Si el usuario no existe
            elAnalisis.listadoUsuarios.add(usuario1);
        }

        if (!elAnalisis.listadoUsuarios.contains(usuario2)) {
            elAnalisis.listadoUsuarios.add(usuario2);
        }
    }

    public int getUsuario1() {
        return usuario1;
    }

    public void setUsuario1(int usuario1) {
        this.usuario1 = usuario1;
    }

    public int getUsuario2() {
        return usuario2;
    }

    public void setUsuario2(int usuario2) {
        this.usuario2 = usuario2;
    }

    public boolean comparadorMayor(Object obj2) {
        if (usuario1 > usuario2) {
            return true;
        } else {
            return false;
        }
    }
}
