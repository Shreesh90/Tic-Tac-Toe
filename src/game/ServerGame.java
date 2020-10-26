package game;

import game.packets.ClientPlayPacket;
import game.packets.GameEndPacket;
import game.packets.UpdatePacket;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerGame extends Game {

    private ServerSocket serverSocket;
    private Socket socket;
    
    private Connection connection;
    
    public ServerGame(){
        super(Game.PLAYER_ONE);
        try {
            
            serverSocket = new ServerSocket(Game.PORT);
            socket = serverSocket.accept();
            connection = new Connection(this, socket);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void inputReceived(int x, int y) {
        if(isMyTurn()){
            updateField(x, y);
        }
        gameWindow.repaint();
    }
    
    
    @Override
    public void packetReceived(Object object) {
        if(object instanceof ClientPlayPacket) {
            ClientPlayPacket packet = (ClientPlayPacket) object;
            
            updateField(packet.getX(), packet.getY()); 
        }
        
    }
    
    private void updateField(int x, int y){
        if( fields[x][y] == Game.FREE){
            fields[x][y] = currentPlayer;
            if(currentPlayer == Game.PLAYER_ONE){
                currentPlayer = Game.PLAYER_TWO;
            } else if(currentPlayer == Game.PLAYER_TWO){
                currentPlayer = Game.PLAYER_ONE;
            }
        }
        
        connection.sendPacket(new UpdatePacket(fields, currentPlayer));
        gameWindow.repaint();

        int winner = checkWin();
        
        if(winner != Game.FREE){
            endGame(winner);
//            showWinner(winner);
        } else {
            int emptyCount=0;
            
            for(int a=0;a<3;a++){
                for(int b=0;b<3;b++) {
                    if(fields[a][b] == Game.FREE){
                        emptyCount++;
                    }
                }
            }
            
            if(emptyCount == 9){
                endGame(winner);
            }
        }
        
    }
    
    private void endGame(int winner) {
        showWinner(winner);
        connection.sendPacket(new GameEndPacket(winner));  
    }
    
    private int checkWin() {
        for(int player=Game.PLAYER_ONE; player <= Game.PLAYER_TWO; player++){
            for(int y=0;y<3;y++) {
                int playerCount = 0;
                
                for(int x=0; x<3;x++) {
                    if(fields[x][y] == player) {
                        playerCount++;
                    }
                }
                if(playerCount == 3) {
                    return player;
                }
            }
            
            for(int x=0;x<3;x++) {
                int playerCount = 0;
                
                for(int y=0;y<3;y++) {
                    if(fields[x][y] == player) {
                        playerCount++;
                    }
                }
                if(playerCount == 3) {
                    return player;
                }
            }
            
            int playerCount=0;
            for(int coordinate = 0; coordinate<3;coordinate++) {
                if(fields[coordinate][coordinate] == player){
                    playerCount++;
                }
            }
            if(playerCount == 3){
                return player;
            }
            
            playerCount = 0;
            
            for(int coordinate = 0; coordinate<3; coordinate++ ){
                if(fields[2-coordinate][2-coordinate] == player){
                    playerCount++;
                }
            }
            if(playerCount == 3){
                return player;
            }
        }
        
        return Game.FREE;
    }
     

    @Override
    public void close() {
        try {
            connection.close();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
