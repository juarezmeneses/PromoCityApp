package br.ufc.ubicomp.promocity;

import java.util.ArrayList;

public class Usuario {
    private int id;
    private double latitude;
    private double longetude;
    private String nome;
    private String email;
    private String senha;
    private ArrayList<Promocao> listaDePromocoes;

    public Usuario(int id, double latitude, double longetude, String nome, String email, String senha){
        this.id = id;
        this.latitude = latitude;
        this.longetude = longetude;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.listaDePromocoes = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setSenha(int id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(int latitude) {
        this.latitude = latitude;

    }

    public double getLongetude() {
        return longetude;
    }

    public void setLongetude(int longetude) {
        this.longetude = longetude;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public ArrayList<Promocao> getListaDePromocoes() {
        return listaDePromocoes;
    }

    public void setListaDePromocoes(ArrayList<Promocao> listaDePromocoes) {
        this.listaDePromocoes = listaDePromocoes;
    }

    public void addPromocao(Promocao promocao) {
        this.listaDePromocoes.add(promocao);
    }

    public void removerPromocao(Promocao promocao) {
        this.listaDePromocoes.remove(promocao);
    }

    public void removerTodasAsPromocoes() {
        this.listaDePromocoes.clear();
    }
}
