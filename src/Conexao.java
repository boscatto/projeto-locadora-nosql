import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class Conexao {
    private MongoClient mongoClient;
    private MongoDatabase database;
    
    public Conexao() {
        try {
            String uri = "mongodb://localhost:27017";
            this.mongoClient = MongoClients.create(uri);
            this.database = mongoClient.getDatabase("projeto_locadora_db");
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }  
    }
    
    public MongoDatabase getDatabase() {
        return database;
    }
    
    public void closeConnection() {
        if (this.mongoClient != null) {
            this.mongoClient.close();
        }
    }
}