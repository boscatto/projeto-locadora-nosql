public class FilmeBean {
    private String idFilme;
    private String titulo;
    private int anoLancamento;
    private String diretor;
    private String genero;

    public FilmeBean(String idFilme, String titulo, int anoLancamento, String diretor, String genero) {
        this.idFilme = idFilme;
        this.titulo = titulo;
        this.anoLancamento = anoLancamento;
        this.diretor = diretor;
        this.genero = genero;
    }

    public String getIdFilme() { return idFilme; }
    public String getTitulo() { return titulo; }
    public int getAnoLancamento() { return anoLancamento; }
    public String getDiretor() { return diretor; }
    public String getGenero() { return genero; }

    @Override
    public String toString() {
        return "Título: " + titulo + " | Ano: " + anoLancamento + 
               " | Diretor: " + diretor + " | Gênero: " + genero;
    }
}