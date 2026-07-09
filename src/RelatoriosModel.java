import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RelatoriosModel {

    public static List<String> relatorioHistoricoCliente(int idCliente, MongoDatabase db) {
        List<String> relatorio = new ArrayList<>();
        MongoCollection<Document> collection = db.getCollection("locacoes");
        
        for (Document doc : collection.find(new Document("id_cliente", idCliente))) {
                double valorTotal = ((Number) doc.get("valor_total")).doubleValue();

                relatorio.add(String.format("Data Locação: %s | Valor: R$%.2f | Status: %s | ID Cópia: %s",
                doc.getDate("data_locacao").toString(),
                valorTotal,
                doc.getString("status"),
                doc.getString("id_copia_alugada")));
        }
        return relatorio;
    }

    public static List<String> relatorioInadimplentes(MongoDatabase db) {
        List<String> relatorio = new ArrayList<>();
        MongoCollection<Document> locacoes = db.getCollection("locacoes");
        MongoCollection<Document> clientes = db.getCollection("clientes");
        
        Date hoje = new Date();
        
        for (Document loc : locacoes.find(new Document("status", "Aberta"))) {
            Date dataPrevista = loc.getDate("data_prevista");
            
            if (dataPrevista.before(hoje)) {
                
                Integer idCli = loc.getInteger("id_cliente");
                Document cli = clientes.find(new Document("id_cliente", idCli)).first();
                
                String nome = (cli != null) ? cli.getString("nome") : "Desconhecido";
                String tel = (cli != null) ? cli.getString("telefone") : "N/A";
                
                relatorio.add(String.format("Cliente: %s | Tel: %s | Previsão de Devolução que passou: %s", 
                              nome, tel, dataPrevista.toString()));
            }
        }
        return relatorio;
    }
}