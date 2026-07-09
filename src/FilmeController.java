import com.mongodb.client.MongoDatabase;
import java.util.Scanner;

public class FilmeController {
    public void criarFilme(Scanner input, MongoDatabase db) {
        System.out.print("Título (ou pressione Enter para cancelar): "); 
        String titulo = input.nextLine();
        
        if (titulo.trim().isEmpty()) {
            System.out.println("Cadastro cancelado. Voltando ao menu...");
            return;
        }

        System.out.print("Ano: "); 
        int ano = input.nextInt(); input.nextLine();
        System.out.print("Diretor: "); 
        String diretor = input.nextLine();
        System.out.print("Gênero: "); 
        String genero = input.nextLine();
        
        FilmeModel.create(new FilmeBean(null, titulo, ano, diretor, genero), db);
        System.out.println("Filme cadastrado com sucesso!");
    }

    public void listarFilmes(MongoDatabase db) {
        for(FilmeBean f : FilmeModel.listAll(db)) { System.out.println(f.toString()); }
    }

    public void adicionarCopia(Scanner input, MongoDatabase db) {
        System.out.print("Título do Filme (ou pressione Enter para cancelar): "); 
        String titulo = input.nextLine();
        
        if (titulo.trim().isEmpty()) {
            System.out.println("Operação cancelada. Voltando ao menu...");
            return;
        }
        
        System.out.print("Mídia (DVD/Blu-ray): "); 
        String midia = input.nextLine();
        
        boolean sucesso = FilmeModel.addCopia(titulo, midia, db);
        
        if (sucesso) {
            System.out.println("Cópia adicionada ao estoque com sucesso!");
        } else {
            System.out.println("ERRO: O filme '" + titulo + "' não foi encontrado no catálogo.");
        }
    }
}