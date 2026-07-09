import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import java.util.List;
import java.util.Scanner;

public class LocacaoController {

    public void listarCatalogoDisponivel(MongoDatabase db) {
        MongoCollection<Document> filmes = db.getCollection("filmes");
        System.out.println("\n--- Cópias Disponíveis ---");
        
        for (Document filme : filmes.find()) {
            List<Document> copias = filme.getList("copias", Document.class);
            if (copias != null) {
                for (Document copia : copias) {
                    if (copia.getBoolean("disponivel")) {
                        System.out.printf("Filme: %s | Mídia: %s | ID da Cópia: %s\n", 
                            filme.getString("titulo"), 
                            copia.getString("tipo_midia"), 
                            copia.getString("id_copia"));
                    }
                }
            }
        }
        System.out.println("---------------------------------------");
    }

    public void alugarFilme(Scanner input, MongoDatabase db) {
        listarCatalogoDisponivel(db);
        
        System.out.print("ID da Cópia desejada (ou pressione Enter para cancelar): "); 
        String idCopia = input.nextLine();
        
        if (idCopia.trim().isEmpty()) {
            System.out.println("Locação cancelada. Voltando ao menu...");
            return;
        }
        
        System.out.print("ID do Cliente: "); int idCliente = input.nextInt();
        System.out.print("Prazo (dias): "); int dias = input.nextInt();
        System.out.print("Valor da Diária: "); double diaria = input.nextDouble();
        input.nextLine();
        
        LocacaoModel.efetuarLocacao(idCliente, idCopia, dias, diaria, db);
    }

    public void devolverFilme(Scanner input, MongoDatabase db) {
        LocacaoModel.listarLocacoesEmAberto(db);
        
        System.out.print("Digite o ID da Cópia Devolvida (ou deixe vazio para cancelar): "); 
        String idCopia = input.nextLine();
        
        if (!idCopia.trim().isEmpty()) {
            LocacaoModel.devolverLocacao(idCopia, db);
        } else {
            System.out.println("Devolução cancelada. Voltando ao menu...");
        }
    }
}