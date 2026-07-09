import com.mongodb.client.MongoDatabase;
import java.util.Scanner;

public class Principal {
    public static void main(String[] args) {
        Conexao c = new Conexao();
        MongoDatabase db = c.getDatabase();
        Scanner input = new Scanner(System.in);
        int op = 0;
        
        do {
            System.out.println("\n--- LOCADORA DE FILMES ---");
            System.out.println("1 - Novo Cliente");
            System.out.println("2 - Listar Clientes");
            System.out.println("3 - Atualizar Cliente");
            System.out.println("4 - Deletar Cliente");
            System.out.println("5 - Novo Filme");
            System.out.println("6 - Listar Filmes");
            System.out.println("7 - Adicionar Cópia ao Estoque");
            System.out.println("8 - Alugar Filme");
            System.out.println("9 - Devolver Filme");
            System.out.println("10 - Ver Catálogo Disponível");
            System.out.println("11 - Histórico do Cliente");
            System.out.println("12 - Clientes Inadimplentes");
            System.out.println("0 - Sair");
            System.out.print("Escolha: ");

            if(input.hasNextInt()) {
                op = input.nextInt();
                input.nextLine();
            } else {
                System.out.println("Opção inválida.");
                input.nextLine();
                continue;
            }

            try {
                switch (op) {
                    case 1: new ClienteController().criarCliente(input, db); break;
                    case 2: new ClienteController().listarClientes(db); break;
                    case 3: new ClienteController().atualizarContatoCliente(input, db); break;
                    case 4: new ClienteController().deletarCliente(input, db); break;
                    
                    case 5: new FilmeController().criarFilme(input, db); break;
                    case 6: new FilmeController().listarFilmes(db); break;
                    case 7: new FilmeController().adicionarCopia(input, db); break;
                    
                    case 8: new LocacaoController().alugarFilme(input, db); break;
                    case 9: new LocacaoController().devolverFilme(input, db); break;
                    case 10: new LocacaoController().listarCatalogoDisponivel(db); break;
                    
                    case 11: 
                        System.out.print("ID do Cliente: ");
                        int idC = input.nextInt(); input.nextLine();
                        System.out.println("\n--- Histórico do Cliente ---");
                        RelatoriosModel.relatorioHistoricoCliente(idC, db).forEach(System.out::println);
                        break;
                    case 12: 
                        System.out.println("\n--- Clientes com Atraso ---");
                        RelatoriosModel.relatorioInadimplentes(db).forEach(System.out::println);
                        break;
                        
                    case 0: System.out.println("Finalizando sistema..."); break;
                    default: System.out.println("Opção inválida."); break;
                }
            } catch(Exception ex) {
                System.out.println("ERRO: " + ex.getMessage());
                ex.printStackTrace();
            }
        } while(op != 0);  
        
        input.close();
        c.closeConnection();
    }
}