package userInterface;

import java.awt.*;
import java.awt.event.*;

import graphVisualization.Visualizer; 

public class PopupFrame
{
    
    public PopupFrame(Visualizer vis) {
    	Frame f= vis.getFrame();  
        PopupMenu popupmenu = new PopupMenu("Edit");   
        MenuItem cut = new MenuItem("Cut");  
        cut.setActionCommand("Cut");  
        MenuItem copy = new MenuItem("Copy");  
        copy.setActionCommand("Copy");  
        MenuItem paste = new MenuItem("Paste");  
        paste.setActionCommand("Paste");      
        popupmenu.add(cut);  
        popupmenu.add(copy);  
        popupmenu.add(paste);   
        
        f.addMouseListener(new MouseAdapter() {  
           public void mouseClicked(MouseEvent e) {              
               popupmenu.show(f , e.getX(), e.getY());  
           }                 
        });  
        f.add(popupmenu);   
        f.setSize(400,400);  
        f.setLayout(null);  
        f.setVisible(true);  
    }  

}
