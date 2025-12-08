package com.safepass.manager;

import java.io.Serializable;
import java.util.Objects;

import java.time.LocalDate;

public class PasswordEntry implements Serializable, Cloneable {
    private String sitio;
    private String usuario;
    private String password;
    private Categoria categoria;
    private LocalDate fechaCreacion;

    public PasswordEntry(String sitio, String usuario, String password, Categoria categoria) {
        this(sitio, usuario, password, categoria, LocalDate.now());
    }

    public PasswordEntry(String sitio, String usuario, String password, Categoria categoria, LocalDate fechaCreacion) {
        this.sitio = sitio;
        this.usuario = usuario;
        this.password = password;
        this.categoria = categoria;
        this.fechaCreacion = fechaCreacion;
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

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDate fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    @Override
    public String toString() {
        return "Sitio: " + sitio + " | Usuario: " + usuario + " | Password: " + password + " | Cat: " + categoria + " | Fecha: " + fechaCreacion;
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
