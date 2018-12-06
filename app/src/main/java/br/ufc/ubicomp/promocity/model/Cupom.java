package br.ufc.ubicomp.promocity.model;

public class Cupom {

    private int id;
    private String nome;
    private String descricao;
    private String codigo;
    private int idStore;

    public Cupom(){

    }

    public Cupom(int id, String nome, String descricao, String codigo){
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.codigo = codigo;

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

    public int getIdStore() {
        return idStore;
    }

    public void setIdStore(int idStore) {
        this.idStore = idStore;
    }

}
