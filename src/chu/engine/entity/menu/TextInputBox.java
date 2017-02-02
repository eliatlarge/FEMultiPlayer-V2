package chu.engine.entity.menu;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.List;

import org.lwjgl.input.Keyboard;

import chu.engine.Game;
import chu.engine.anim.BitmapFont;
import chu.engine.entity.Entity;
import chu.engine.event.KeyboardEvent;
import net.fe.FEResources;

public abstract class TextInputBox extends Entity {

	protected float width;

	protected float height;

	protected boolean hasFocus;

	protected int cursorPos;

	protected StringBuilder input;

	protected BitmapFont font;

	public TextInputBox(float x, float y, float w, float h, String fontname) {
		super(x, y);
		width = w;
		height = h;
		hasFocus = false;
		cursorPos = 0;
		input = new StringBuilder();
		font = FEResources.getBitmapFont(fontname);
	}

	//DON'T FORGETTI MOM'S SPAGHETTI
	public void beginStep() {
		if (hasFocus) {
			List<KeyboardEvent> keys = Game.getKeys();
			for (KeyboardEvent ke : keys) {
				if (ke.state) {
					System.out.println(ke + "\t" + ke.isControlDown() + "\t" + ke.eventChar);
					char c = ke.eventChar;
					if(ke.isControlDown())
						switch(ke.key){
							case Keyboard.KEY_C:
								Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(input.toString()), null);
								continue;
							case Keyboard.KEY_V:
								System.out.println("ding");
								String clip = getClipboardContent();
								input = input.insert(cursorPos, clip);
								cursorPos += clip.length();
								continue;
						}
					if (isValidCharacter(c) && FEResources.getBitmapFont("default_med").getStringWidth(input.toString() + c) < 246) {
						input.insert(cursorPos, c);
						cursorPos++;
					} else {
						if (ke.key == FEResources.getKeyMapped(Keyboard.KEY_LEFT) && cursorPos > 0) {
							cursorPos--;
						} else if (ke.key == FEResources.getKeyMapped(Keyboard.KEY_RIGHT)
								&& cursorPos < input.length()) {
							cursorPos++;
						} else if (ke.key == FEResources.getKeyMapped(Keyboard.KEY_BACK) && cursorPos > 0) {
							input.deleteCharAt(cursorPos - 1);
							cursorPos--;
						} else if (ke.key == FEResources.getKeyMapped(Keyboard.KEY_DELETE)
								&& cursorPos < input.length()) {
							input.deleteCharAt(cursorPos);
						}
					}
				}
			}
		}
	}

	/**
	 * Checks if c is valid character.
	 *
	 * @param c the character
	 * @return true, if c is valid character
	 */
	private boolean isValidCharacter(char c) {
		return font.containsCharacter(c);
	}

	public String getInput() {
		return input.toString();
	}
	
	private static String getClipboardContent(){
		Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
		try {
			return (String) cb.getData(DataFlavor.stringFlavor);
		} catch (UnsupportedFlavorException | IOException e) {
			e.printStackTrace();
			return "";
		}
	}
}