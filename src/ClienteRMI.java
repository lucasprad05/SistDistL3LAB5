/**
 * Lab05: Sistema P2P
 * 
 * Autor: Lucio A. Rocha
 * Ultima atualizacao: 22/05/2023
 * 
 * Referencias: 
 * https://docs.oracle.com/javase/tutorial/essential/io
 * http://fortunes.cat-v.org/
 */

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class ClienteRMI {

	public void iniciarCliente(String peerNome){
		
		List<PeerLista> listaPeers = new ArrayList<PeerLista>();
        for( PeerLista peer : PeerLista.values())
        	listaPeers.add(peer);
		
        try {
                        
            Registry registro = LocateRegistry.getRegistry("127.0.0.1", 1099);

			//removi o codigo anterior que fazia a seleção aleatoria
			IMensagem stub = (IMensagem) registro.lookup(peerNome);
			System.out.println("Conectado no peer: " + peerNome);
    		

            String opcao="";
            Scanner leitura = new Scanner(System.in);
            do {
            	System.out.println("1) Read");
            	System.out.println("2) Write");
            	System.out.println("x) Exit");
            	System.out.print(">> ");
            	opcao = leitura.next();
            	switch(opcao){
            	case "1": {
            		Mensagem mensagem = new Mensagem("", opcao);
            		Mensagem resposta = stub.enviar(mensagem); //dentro da mensagem tem o campo 'read'
            		System.out.println(resposta.getMensagem());
            		break;
            	}
            	case "2": {
            		//Monta a mensagem                		
            		System.out.print("Add fortune: ");
            		String fortune = leitura.next();
            		
            		Mensagem mensagem = new Mensagem(fortune, opcao);
            		Mensagem resposta = stub.enviar(mensagem); //dentro da mensagem tem o campo 'write'
            		System.out.println(resposta.getMensagem());
            		break;
            	}
            	}
            } while(!opcao.equals("x"));
            
        } catch(Exception e) {
            e.printStackTrace();
        }

	}
	
    /*public static void main(String[] args) {
                
    	new ClienteRMI().iniciarCliente();
    	        
    }*/
    
}
