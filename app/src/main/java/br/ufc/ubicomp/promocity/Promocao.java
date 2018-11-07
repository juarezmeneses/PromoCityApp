package br.ufc.ubicomp.promocity;

import java.util.ArrayList;
import java.util.Date;

public class Promocao {

    private int id;
    private String nome;
    private String descricao;
    private String codigo;
    private Date dataInicio;
    private Date dataFim;
    private ArrayList<Cupom> listaDeCupons;

    public Promocao(){

    }


    public Promocao(int id, String nome, String descricao){
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.listaDeCupons = new ArrayList<>();

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Date getDataFim() {
        return dataFim;
    }

    public void setDataFim(Date dataFim) {
        this.dataFim = dataFim;
    }


    ///
    public ArrayList<Cupom> getListaDeCupons() {
        return listaDeCupons;
    }

    public void setListaDeCupons(ArrayList<Cupom> listaDeCupons) {
        this.listaDeCupons = listaDeCupons;
    }

    public void addCupom(Cupom cupom) {
        this.listaDeCupons.add(cupom);
    }

    public void removerCupom(Cupom cupom) {
        this.listaDeCupons.remove(cupom);
    }

    public void removerTodosOsCupons() {
        this.listaDeCupons.clear();
    }




}
