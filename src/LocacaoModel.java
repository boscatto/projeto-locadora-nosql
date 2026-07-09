import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import java.util.Date;
import java.util.Calendar;

public class LocacaoModel {
    
    public static void efetuarLocacao(int idCliente, String idCopia, int dias, double valorDiaria, MongoDatabase db) {
        MongoCollection<Document> locacoes = db.getCollection("locacoes");
        MongoCollection<Document> filmes = db.getCollection("filmes");

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, dias);
        Date dataPrevista = cal.getTime();

        Document locacao = new Document("id_cliente", idCliente)
                .append("data_locacao", new Date())
                .append("data_prevista", dataPrevista)
                .append("valor_total", valorDiaria * dias)
                .append("status", "Aberta")
                .append("id_copia_alugada", idCopia);

        locacoes.insertOne(locacao);
        String idLocacaoGerada = locacao.getObjectId("_id").toHexString();

        filmes.updateOne(
            new Document("copias.id_copia", idCopia),
            new Document("$set", new Document("copias.$.disponivel", false))
        );

        System.out.println("Locação realizada! ID do Recibo: " + idLocacaoGerada);
    }

    public static void devolverLocacao(String idCopia, MongoDatabase db) {
        MongoCollection<Document> locacoes = db.getCollection("locacoes");
        MongoCollection<Document> filmes = db.getCollection("filmes");

        // Busca a locação que está 'Aberta' para a cópia específica informada
        Document filtro = new Document("id_copia_alugada", idCopia).append("status", "Aberta");
        Document locacao = locacoes.find(filtro).first();

        if (locacao == null) {
            System.out.println("Erro: Nenhuma locação em aberto foi encontrada para esta cópia.");
            return;
        }

        // Pega o ObjectId interno da locação encontrada para poder atualizá-la
        org.bson.types.ObjectId idLocacao = locacao.getObjectId("_id");

        // Fecha a locação e registra a data atual como data de devolução
        locacoes.updateOne(
            new Document("_id", idLocacao),
            new Document("$set", new Document("status", "Fechada").append("data_devolvida", new Date()))
        );

        // Devolve a cópia para o estoque do catálogo
        filmes.updateOne(
            new Document("copias.id_copia", idCopia),
            new Document("$set", new Document("copias.$.disponivel", true))
        );

        System.out.println("Devolução registrada com sucesso!");
    }

    public static void listarLocacoesEmAberto(MongoDatabase db) {
        MongoCollection<Document> locacoes = db.getCollection("locacoes");
        MongoCollection<Document> clientes = db.getCollection("clientes");

        System.out.println("\n--- Cópias Atualmente Alugadas ---");
        boolean temAluguel = false;
        
        // Busca apenas as locações que ainda não foram devolvidas
        for (Document loc : locacoes.find(new Document("status", "Aberta"))) {
            temAluguel = true;
            String idCopia = loc.getString("id_copia_alugada");
            int idCliente = loc.getInteger("id_cliente");
            
            // Busca o nome do cliente para exibir na tela
            Document cli = clientes.find(new Document("id_cliente", idCliente)).first();
            String nomeCli = (cli != null) ? cli.getString("nome") : "Desconhecido";

            System.out.printf("Cópia ID: %s | Cliente: %s | Data Prevista: %s\n",
                idCopia, nomeCli, loc.getDate("data_prevista").toString());
        }
        
        if (!temAluguel) {
            System.out.println("Nenhuma cópia alugada no momento.");
        }
        System.out.println("----------------------------------");
    }
}