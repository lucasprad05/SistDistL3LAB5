/**
 * Sistemas Disribuídos 25.1
 * Lab05: Sistema P2P
 * Ana Carolina Ribeiro Miranda: 2208407
 * Lucas Castilho Pinto Prado: 2367980
 * Professor: Lucio Agostinho Rocha
 *
 * PEER NOVO ADICIONADO
 */


public enum PeerLista {
    
    PEER1 {
        @Override
        public String getNome() {
            return "PEER1";
        }        
    },
    PEER2 {
        public String getNome() {
            return "PEER2";
        }        
    },
    PEER3 {
        public String getNome() {
            return "PEER3";
        }        
    },
    PEER4 {
        public String getNome() { return "PEER4"; }
    };
    public String getNome(){
        return "NULO";
    }    
}
