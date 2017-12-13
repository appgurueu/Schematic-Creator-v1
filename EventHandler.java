package appguru;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JPanel;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author lars
 */
public class EventHandler implements KeyListener,MouseListener,MouseWheelListener {
    public JFrame pane;
    public static final int NUM_KEYS=1024;
    public static final int MOUSE_BUTTON_LEFT=0;
    public static final int MOUSE_BUTTON_MIDDLE=1;
    public static final int MOUSE_BUTTON_RIGHT=2;
    public boolean mouseOn;
    public boolean mouseEntered;
    public boolean mouseExited;
    public int[] mouse;
    public Point[] mouseDraggingStart;
    public Point[] mouseDraggingEnd;
    public boolean[] mouseDragging;
    public static String[] keyNames=new String[NUM_KEYS];
    public java.util.List<Integer> pressedKeys;
    public java.util.List<String> pressedKeyChars;
    public int[] keys;
    static {
        for (int i=0; i < NUM_KEYS; i++) {
            keyNames[i]=KeyEvent.getKeyText(KeyEvent.KEY_FIRST+i);
        }
    }
    public EventHandler(JFrame plane) {
        this.keys=new int[NUM_KEYS];
        this.pressedKeys = new ArrayList<Integer>();
        this.pressedKeyChars = new ArrayList<String>();
        this.pane=plane;
        this.mouse=new int[MouseInfo.getNumberOfButtons()+3];
        //pane.requestFocus();
       /*plane.addKeyListener(this);
        plane.addMouseListener(this);
        plane.addMouseWheelListener(this);*/
    }
    @Override
    public void mousePressed(MouseEvent m) {
        mouse[m.getButton()-MouseEvent.BUTTON1+2]=1;
    }
    @Override
    public void mouseClicked(MouseEvent m) {
        mouse[m.getButton()-MouseEvent.BUTTON1+2]=2;
    }
    @Override
    public void mouseReleased(MouseEvent m) {
        mouse[m.getButton()-MouseEvent.BUTTON1+2]=3;
    }
    @Override
    public void mouseEntered(MouseEvent m) {
        mouseOn=true;
        mouseEntered=true;
    }
    @Override
    public void mouseExited(MouseEvent m) {
        mouseOn=false;
        mouseExited=true;
    }
    @Override
    public void mouseWheelMoved(MouseWheelEvent w) {
        mouse[mouse.length-1]=w.getWheelRotation();
    }
    @Override
    public void keyTyped(KeyEvent e) {
        keys[e.getKeyCode()]=2;
        addKey(e.getKeyCode());
        addKey(e.getKeyChar());
    }
    @Override
    public void keyReleased(KeyEvent e) { 
        keys[e.getKeyCode()]=3;
    }
    @Override
    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()]=1;
        addKey(e.getKeyCode());
        addKey(e.getKeyChar());
    }
    public void addKey(int s) {
        for (Integer i:pressedKeys) {
            if (s==i) {
                return;
            }
        }
        pressedKeys.add(s);
    }
    public void addKey(char c) {
        String s2=new String(new char[] {c});
        for (String s:pressedKeyChars) {
            if (s.equals(s2)) {
                return;
            }
        }
        pressedKeyChars.add(s2);
    }
    public int getKey(int key) {
        return keys[key];
    }
    public int getKey(String key) {
        for (int i=0; i < keyNames.length; i++) {
            if (keyNames.equals(key)) {
                return keys[i];
            }
        }
        return -1;
    }
    public boolean keyDown(int key) {
        return getKey(key) != 0;
    }
    public boolean keyDown(String key) {
        return getKey(key) != 0;
    }
    public int getMouseWheel() {
        return mouse[mouse.length-1];
    }
    public int mouseX() {
        return mouse[0];
    }
    public int mouseY() {
        return mouse[1];
    }
    public int[] getMousePosition() {
        return new int[] {mouse[0],mouse[1]};
    }
    public int getMouseButton(int button) {
        return mouse[2+button];
    }
    public boolean mouseOver() {
        return mouseOn;
    }
    public int popIndex() {
        try {
           int i=pressedKeys.get(0);
           pressedKeys.remove(0);
           return i;
        } catch (ArrayIndexOutOfBoundsException oob) {
           return -1;
        }
    }
    public String popName() {
        try {
           int i=pressedKeys.get(0);
           pressedKeys.remove(0);
           return keyNames[i];
        } catch (ArrayIndexOutOfBoundsException oob) {
           return null;
        }
    }
    public String popChar() {
        try {
           String i=pressedKeyChars.get(0);
           pressedKeyChars.remove(0);
           return i;
        } catch (ArrayIndexOutOfBoundsException oob) {
           return null;
        }
    }
    public int pullIndex() {
        try {
           int i=pressedKeys.get(0);
           return i;
        } catch (ArrayIndexOutOfBoundsException oob) {
           return -1;
        }
    }
    public String pullName() {
        try {
           int i=pressedKeys.get(0);
           return keyNames[i];
        } catch (ArrayIndexOutOfBoundsException oob) {
           return null;
        }
    }
    public String pullChar() {
        try {
           String i=pressedKeyChars.get(0);
           return i;
        } catch (ArrayIndexOutOfBoundsException oob) {
           return null;
        }
    }
    public java.util.List<Integer> getPressedKeys() {
        return pressedKeys;
    }
    public java.util.List<String> getPressedKeyChars() {
        return pressedKeyChars;
    }
    public String typedKeys() {
        String t="";
        for (Integer i:pressedKeys) {
            t+=keyNames[i];
        }
        return t;
    }
    public String typedKeyChars() {
        String t="";
        for (String s:pressedKeyChars) {
            t+=s;
        }
        return t;
    }
    public void work() {
       // pane.requestFocus();
        //Component[] rel = pane.getComponents();
        try {
        mouse[0]=pane.getMousePosition().x;
        mouse[1]=pane.getMousePosition().y;
        } catch (Exception e) {
        }
        //mouse[0]=MouseInfo.getPointerInfo().getLocation().x-pane.getLocationOnScreen().x;
        //mouse[1]=MouseInfo.getPointerInfo().getLocation().y-pane.getLocationOnScreen().y;
    }
    public void reset() {
        for (int i=0; i < keys.length; i++) {
           if (keys[i]==2 || keys[i]==3) {
               keys[i]=0;
           }
        }
        int index=0;
        for (Integer i:pressedKeys) {
            if (keys[i]==2 || keys[i]==3) {
                pressedKeys.remove(index);
                pressedKeyChars.remove(index);
            }
            index++;
        }
        for (int i=2; i < mouse.length; i++) {
            mouse[i]=0;
        }
        mouseEntered=false;
        mouseExited=false;
    }
}

