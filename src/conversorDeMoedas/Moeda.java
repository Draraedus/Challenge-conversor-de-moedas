package conversorDeMoedas;

public class Moeda {
    private final String abreviacao;
    private final String nome;

    public Moeda(String abreviacao, String nome) {
        this.abreviacao = abreviacao;
        this.nome = nome;
    }

    public String getAbreviacao() {
        return abreviacao;
    }

    public String getNome() {
        return nome;
    }

    @Override
    public String toString() {
        return abreviacao + " - " + nome;
    }
}

