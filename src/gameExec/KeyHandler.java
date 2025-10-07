package gameExec;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import gameExec.UI;

public class KeyHandler implements KeyListener{
	GameUi gp;
	public boolean upPressed, downPressed, leftPressed, rightPressed;
	@Override
	public void keyTyped(KeyEvent e) {
		
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		
		int code = e.getKeyCode();
		if (gp.gameState == gp.playState) {
		
			if(code == KeyEvent.VK_W) {
				upPressed = true;
			}
			if(code == KeyEvent.VK_S) {
				downPressed = true;
			}
			if(code == KeyEvent.VK_A) {
				leftPressed = true;
			}
			if(code == KeyEvent.VK_D) {
				rightPressed = true;
			}
			if(code == KeyEvent.VK_P) {
				gp.gameState = gp.pauseState;
			}
		} 
			
		else if (gp.gameState == gp.dialogueState) {
		    if (code == KeyEvent.VK_ENTER) {
		        gp.ui.currentDialogueIndex++;

		        // If there are still dialogues left
		        if (gp.ui.currentDialogueIndex < gp.ui.dialogues.length && gp.ui.dialogues[gp.ui.currentDialogueIndex] != null) {
		            gp.ui.currentText = gp.ui.dialogues[gp.ui.currentDialogueIndex];
		        } else {
		            // No more dialogues, go back to play
		            gp.gameState = gp.playState;
		        }
		    }
		}

		else if (gp.gameState == gp.pauseState) {
			if(code == KeyEvent.VK_P) {
				gp.gameState = gp.playState;
				System.out.println("pause");
			}
				// stuff
			}
		}
	
	
	
	@Override public void keyReleased(KeyEvent e) {
		int code = e.getKeyCode();
		if(code == KeyEvent.VK_W) {
			upPressed = false;
		}
		if(code == KeyEvent.VK_S) {
			downPressed = false;
		}
		if(code == KeyEvent.VK_A) {
			leftPressed = false;
		}
		if(code == KeyEvent.VK_D) {
			rightPressed = false;
		}
	}
	public KeyHandler(GameUi gp) {
		this.gp = gp;
	}
}
