package com.safepass.manager;

import java.io.Serializable;
import java.util.Objects;

public class PasswordEntry implements Serializable, Cloneable {
    private String sitio;
    private String usuario;
    private String password;
    private Categoria categoria;

    public PasswordEntry(String sitio, String usuario, String password, Categoria categoria) {
        this.sitio = sitio;
        this.usuario = usuario;
        this.password = password;
        this.categoria = categoria;
    }

    public String getSitio() {
        return sitio;
    }

    public void setSitio(String sitio) {
        this.sitio = sitio;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    @Override
    public String toString() {
        return "Sitio: " + sitio + " | Usuario: " + usuario + " | Cat: " + categoria;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PasswordEntry that = (PasswordEntry) o;
        return Objects.equals(sitio, that.sitio) && Objects.equals(usuario, that.usuario);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
