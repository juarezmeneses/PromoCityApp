package br.ufc.ubicomp.promocity;

import java.util.ArrayList;

public class Estabelecimento {
    private int id;
    private double latitude;
    private double longetude;
    private String nome;
    private String descricao;
    private ArrayList<Promocao> listaDePromocoes;
    private String email;
    private String endereco;
    private String cidade;
    private String estado;

    public Estabelecimento(){

    }

    public Estabelecimento(int id, double latitude, double longetude, String nome, String descricao){
        this.id = id;
        this.latitude = latitude;
        this.longetude = longetude;
        this.nome = nome;
        this.descricao = descricao;
        this.listaDePromocoes = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
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


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }


}
