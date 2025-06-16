/**
 * Sistemas Distribuídos 25.1
 * Lab05: Sistema P2P
 * Ana Carolina Ribeiro Miranda: 2208407
 * Lucas Castilho Pinto Prado: 2367980
 * Professor: Lucio Agostinho Rocha
 *
 * Adicionar o novo Peer FEITO no PeerLista.java
 * UNBIND FEITO nesse arquivo
 * Usuário escolhe o Peer FEITO nesse arquivo e também fiz alteração no ClienteRMI
 * Interface gráfica para Peers ativos FEITO
 */

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Peer implements IMensagem {

	private Registry servidorRegistro;
	private PeerLista peer;

	public Peer() {}

	@Override
	public Mensagem enviar(Mensagem mensagem) throws RemoteException {
		Mensagem resposta;
		try {
			System.out.println("Mensagem recebida: " + mensagem.getMensagem());
			resposta = new Mensagem(parserJSON(mensagem.getMensagem()));
		} catch (Exception e) {
			e.printStackTrace();
			resposta = new Mensagem("{\n\"result\": false\n}");
		}
		return resposta;
	}

	public String parserJSON(String json) {
		String fortune = "-1";
		String[] v = json.split(":");
		String[] v1 = v[1].split("\"");

		if (v1[1].equals("write")) {
			String[] p = json.split("\\[");
			String[] p1 = p[1].split("]");
			String[] p2 = p1[0].split("\"");
			fortune = p2[1];
			new Principal().write(fortune);
		} else if (v1[1].equals("read")) {
			fortune = new Principal().read();
		}

		return "{\n\"result\": \"" + fortune + "\"\n}";
	}

	public void iniciar() {
		try {
			List<PeerLista> listaPeers = new ArrayList<>();
			for (PeerLista p : PeerLista.values())
				listaPeers.add(p);

			try {
				servidorRegistro = LocateRegistry.createRegistry(1099);
			} catch (java.rmi.server.ExportException e) {
				System.out.println("Registro já estava ativo.");
			}

			servidorRegistro = LocateRegistry.getRegistry();

			String[] listaAlocados = servidorRegistro.list();
			for (String nome : listaAlocados)
				System.out.println(nome + " ativo.");

			Scanner scanner = new Scanner(System.in);

			PeerLista escolhido = null;
			boolean valido = false;

			// Usuário escolhe o peer
			while (!valido) {
				System.out.println("Escolha um dos peers disponíveis:");

				for (int i = 0; i < listaPeers.size(); i++) {
					System.out.println(i + ") " + listaPeers.get(i).getNome());
				}

				System.out.print("Digite o número do peer desejado: ");
				int escolha = scanner.nextInt();
				escolhido = listaPeers.get(escolha);

				// Verifica se já está alocado
				boolean ocupado = false;
				for (String nome : listaAlocados) {
					if (nome.equals(escolhido.getNome())) {
						ocupado = true;
						break;
					}
				}

				if (ocupado) {
					System.out.println("Peer já está em uso. Escolha outro.\n");
				} else {
					valido = true;
					this.peer = escolhido;
				}
			}

			IMensagem stub = (IMensagem) UnicastRemoteObject.exportObject(this, 0);
			servidorRegistro.rebind(peer.getNome(), stub);

			System.out.println(peer.getNome() + " ativo no RMI. Aguardando conexões...");

			// Atualiza lista de peers após o rebind
			String[] listaAtualizada = servidorRegistro.list();

			// Interface gráfica mostrando os peers ativos
			javax.swing.SwingUtilities.invokeLater(() -> {
				javax.swing.JFrame frame = new javax.swing.JFrame("Peers Ativos no RMI");
				javax.swing.JTextArea textArea = new javax.swing.JTextArea(10, 30);
				textArea.setEditable(false);

				StringBuilder sb = new StringBuilder();
				sb.append("Peers registrados:\n");
				for (String nome : listaAtualizada) {
					sb.append("- ").append(nome).append("\n");
				}

				textArea.setText(sb.toString());
				frame.add(new javax.swing.JScrollPane(textArea));
				frame.pack();
				frame.setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			});

			// Inicia o cliente e conecta direto no peer escolhido
			new ClienteRMI().iniciarCliente(peer.getNome());

			// Espera o usuário digitar 'sair' e faz unbind
			System.out.println("\nDigite 'sair' para desconectar este peer:");
			scanner.nextLine(); // limpa \n pendente
			String input = scanner.nextLine();

			if (input.equalsIgnoreCase("sair")) {
				servidorRegistro.unbind(peer.getNome());
				UnicastRemoteObject.unexportObject(this, true);
				System.out.println("Peer desconectado com sucesso.");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Peer servidor = new Peer();
		servidor.iniciar();
	}
}
