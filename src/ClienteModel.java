import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import java.util.ArrayList;
import java.util.List;

public class ClienteModel {
    public static void create(ClienteBean c, MongoDatabase db) {
        MongoCollection<Document> collection = db.getCollection("clientes");
        
        Document sort = new Document("id_cliente", -1);
        Document max = collection.find().sort(sort).first();
        int nextId = (max != null && max.getInteger("id_cliente") != null) ? max.getInteger("id_cliente") + 1 : 1;

        Document doc = new Document("id_cliente", nextId)
                .append("nome", c.getNome())
                .append("cpf", c.getCpf())
                .append("telefone", c.getTelefone());
        collection.insertOne(doc);
    }

    public static List<ClienteBean> listAll(MongoDatabase db) {
        List<ClienteBean> list = new ArrayList<>();
        MongoCollection<Document> collection = db.getCollection("clientes");
        
        for (Document doc : collection.find()) {
            if (doc.containsKey("id_cliente")) {
                list.add(new ClienteBean(
                    doc.getInteger("id_cliente"),
                    doc.getString("nome"), doc.getString("cpf"), doc.getString("telefone")
                ));
            }
        }
        return list;
    }

    public static ClienteBean findById(int id, MongoDatabase db) {
        MongoCollection<Document> collection = db.getCollection("clientes");
        Document doc = collection.find(new Document("id_cliente", id)).first();
        
        if (doc != null) {
            return new ClienteBean(
                doc.getInteger("id_cliente"),
                doc.getString("nome"), doc.getString("cpf"), doc.getString("telefone")
            );
        }
        return null;
    }

    public static void updateTelefone(int id, String novoTelefone, MongoDatabase db) {
        MongoCollection<Document> collection = db.getCollection("clientes");
        collection.updateOne(
            new Document("id_cliente", id),
            new Document("$set", new Document("telefone", novoTelefone))
        );
    }

    public static boolean remove(int id, MongoDatabase db) {
        MongoCollection<Document> locacoes = db.getCollection("locacoes");
        
        // Verifica se o cliente tem alguma locação com status "Aberta"
        Document locacaoPendente = locacoes.find(
            new Document("id_cliente", id).append("status", "Aberta")
        ).first();

        // Se encontrou uma locação pendente, bloqueia a exclusão
        if (locacaoPendente != null) {
            return false; 
        }

        // Se não tem pendências, prossegue com a exclusão
        MongoCollection<Document> collection = db.getCollection("clientes");
        collection.deleteOne(new Document("id_cliente", id));
        return true;
    }
}