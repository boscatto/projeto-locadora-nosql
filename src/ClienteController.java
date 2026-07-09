import com.mongodb.client.MongoDatabase;
import java.util.List;
import java.util.Scanner;

public class ClienteController {
    public void criarCliente(Scanner input, MongoDatabase db) {
        System.out.print("Nome (ou pressione Enter para cancelar): "); 
        String nome = input.nextLine();
        
        if (nome.trim().isEmpty()) {
            System.out.println("Cadastro cancelado. Voltando ao menu...");
            return;
        }

        System.out.print("CPF: "); String cpf = input.nextLine();
        System.out.print("Telefone: "); String tel = input.nextLine();
        
        ClienteModel.create(new ClienteBean(0, nome, cpf, tel), db);
        System.out.println("Cliente cadastrado!");
    }

    public void listarClientes(MongoDatabase db) {
        List<ClienteBean> lista = ClienteModel.listAll(db);
        for(ClienteBean c : lista) { System.out.println(c.toString()); }
    }

    public void atualizarContatoCliente(Scanner input, MongoDatabase db) {
        System.out.print("ID do cliente a ser atualizado (ou Enter para cancelar): ");
        String idStr = input.nextLine();
        
        if (idStr.trim().isEmpty()) {
            System.out.println("Atualização cancelada. Voltando ao menu...");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            System.out.println("ERRO: ID inválido. Operação cancelada.");
            return;
        }

        ClienteBean clienteAtual = ClienteModel.findById(id, db);
        if (clienteAtual == null) {
            System.out.println("ERRO: Cliente não encontrado.");
            return;
        }

        System.out.println("Cliente encontrado: " + clienteAtual.getNome());
        System.out.print("Novo telefone (ou deixe vazio para manter o atual): ");
        String novoTel = input.nextLine();
        
        if (novoTel.trim().isEmpty()) novoTel = clienteAtual.getTelefone();

        ClienteModel.updateTelefone(id, novoTel, db);
        System.out.println("Cliente atualizado com sucesso!");
    }

    public void deletarCliente(Scanner input, MongoDatabase db) {
        System.out.print("ID do cliente a ser removido (ou Enter para cancelar): ");
        String idStr = input.nextLine();
        
        if (idStr.trim().isEmpty()) {
            System.out.println("Exclusão cancelada. Voltando ao menu...");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            System.out.println("ERRO: ID inválido. Operação cancelada.");
            return;
        }
        
        boolean removido = ClienteModel.remove(id, db);
        
        if (removido) {
            System.out.println("Cliente removido com sucesso!");
        } else {
            System.out.println("ERRO: Operação bloqueada! O cliente possui locações pendentes que precisam ser devolvidas.");
        }
    }
}