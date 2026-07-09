import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import java.util.ArrayList;
import java.util.List;

public class FilmeModel {
    public static void create(FilmeBean f, MongoDatabase db) {
        MongoCollection<Document> collection = db.getCollection("filmes");
        Document doc = new Document("titulo", f.getTitulo())
                .append("ano_lancamento", f.getAnoLancamento())
                .append("diretor", f.getDiretor())
                .append("genero", f.getGenero())
                .append("copias", new ArrayList<Document>());
        collection.insertOne(doc);
    }

    public static List<FilmeBean> listAll(MongoDatabase db) {
        List<FilmeBean> list = new ArrayList<>();
        MongoCollection<Document> collection = db.getCollection("filmes");
        for (Document doc : collection.find()) {
            list.add(new FilmeBean(
                doc.getObjectId("_id").toHexString(),
                doc.getString("titulo"), doc.getInteger("ano_lancamento"),
                doc.getString("diretor"), doc.getString("genero")
            ));
        }
        return list;
    }

    public static boolean addCopia(String tituloFilme, String tipoMidia, MongoDatabase db) {
        MongoCollection<Document> collection = db.getCollection("filmes");
        
        Document filtro = new Document("titulo", new Document("$regex", "^" + tituloFilme + "$").append("$options", "i"));
        Document filme = collection.find(filtro).first();

        if (filme == null) {
            return false; 
        }
        
        String prefixo = tituloFilme.length() >= 3 
                         ? tituloFilme.substring(0, 3).toUpperCase() 
                         : tituloFilme.toUpperCase();
                         
        List<Document> copias = filme.getList("copias", Document.class);
        int proxNumero = (copias != null) ? copias.size() + 1 : 1;
        String sequencial = String.format("%02d", proxNumero);
        
        String sufixo = tipoMidia.toUpperCase().startsWith("BLU") ? "BLU" : "DVD";
        
        String idCopia = prefixo + "-" + sequencial + "-" + sufixo;

        Document novaCopia = new Document("id_copia", idCopia)
                .append("tipo_midia", tipoMidia)
                .append("disponivel", true);
                
        collection.updateOne(
            new Document("_id", filme.getObjectId("_id")),
            new Document("$push", new Document("copias", novaCopia))
        );
        
        return true;
    }

    public static void remove(String id, MongoDatabase db) {
        MongoCollection<Document> collection = db.getCollection("filmes");
        collection.deleteOne(new Document("_id", new ObjectId(id)));
    }
}