/*---

    iGeo - http://igeo.jp

    Copyright (c) 2002-2012 Satoru Sugihara

    This file is part of iGeo.

    iGeo is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as
    published by the Free Software Foundation, version 3.

    iGeo is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with iGeo.  If not, see <http://www.gnu.org/licenses/>.

---*/

package igeo.gui;

import java.util.ArrayList;
import java.awt.event.*;
import java.io.*;
import javax.media.opengl.*;
import javax.swing.*;

import igeo.*;

/**
   A root GUI object of iGeo managing all IPane instance.
   An instance IG is keyed by IPanel object when it's in Graphic mode.
   
   @author Satoru Sugihara
   @version 0.7.1.0;
*/
public class IPanel extends IComponent implements IServerI, MouseListener, MouseMotionListener, MouseWheelListener, KeyListener, FocusListener, ComponentListener{
    
    public ArrayList<IPane> panes;
    
    public IG ig;
    
    public IPane currentMousePane=null;
    
    //public IPane fullScreenPane=null;
    //public int fullPaneOrigX, fullPaneOrigY, fullPaneOrigWidth, fullPaneOrigHeight;
    
    public IBounds bounds;
    public int serverStateCount=-1;
    
    public boolean startDynamicServer=true;
    
    public IPanel(int x, int y, int width, int height){
	super(x,y,width,height);
	panes = new ArrayList<IPane>();
	//this.ig = ig;
    }
    
    public void setIG(IG ig){
	this.ig = ig;
	//for(int i=0; i<panes.size(); i++) panes.get(i).setIG(ig);
    }
    
    public IServer server(){ return ig.server(); }
    
    public void addPane(IPane p){
	panes.add(p);
	p.setParent(this);
	//if(ig!=null) p.setIG(ig);
    }
    
    public IPane getPane(int i){ return panes.get(i); }
    
    public int paneNum(){ return panes.size(); }
    
    public void removePane(int i){ panes.remove(i); }
    public void clearPane(){ panes.clear(); }
    
    public void show(){ for(int i=0; i<panes.size(); i++) panes.get(i).show(); }
    public void hide(){ for(int i=0; i<panes.size(); i++) panes.get(i).hide(); }
    
    /** focus on all pane
     */
    public void focus(){
	for(int i=0; i<panes.size(); i++) panes.get(i).focus(); 
    }
    
    
    public void setSize(int w, int h){
	int origW = width;
	int origH = height;
	
	for(int i=0; i<panes.size(); i++){
	    int nx = (int)(panes.get(i).getX()*w/origW);
	    int ny = (int)(panes.get(i).getY()*h/origH);
	    int nw = (int)(panes.get(i).getWidth()*w/origW);
	    int nh = (int)(panes.get(i).getHeight()*h/origH);
	    panes.get(i).setBounds(nx,ny,nw,nh);
	}
	width=w;
	height=h;
    }
    
    
    public void startDynamicServer(){
	if(ig!=null &&ig.dynamicServer()!=null &&
	   (ig.dynamicServer().num()>0 || ig.dynamicServer().addingNum()>0)){
	    ig.dynamicServer().start();
	    startDynamicServer=false;
	}
    }
    
    public void draw(IGraphics g){
	// some initialization process
	if(startDynamicServer){
	    // here is a point to start dynamicServer
	    startDynamicServer();
	}
	
	for(int i=0; i<panes.size(); i++){
	    synchronized(IG.lock){
		if(panes.get(i).isVisible()){ panes.get(i).draw(g); }
	    }
	}
    }
    
    public IPane getPaneAt(MouseEvent e){
	return getPaneAt(e.getX(),e.getY());
    }
    
    public IPane getPaneAt(int x, int y){
	//for(IPane p: panes) if(p.isVisible()&&p.contains(x,y)) return p;
	// to match with drawing order in case they overlap and some panes come to the front
	for(int i=panes.size()-1; i>=0; i--)
	    if(panes.get(i).isVisible()&&panes.get(i).contains(x,y)) return panes.get(i);
	return null;
    }
    
    
    public void mousePressed(MouseEvent e){
	IPane p = getPaneAt(e);
	if(p!=null){
	    currentMousePane = p;
	    p.mousePressed(e);
	}
	else{
	    IOut.err("no pane"); //
	}
    }
    public void mouseReleased(MouseEvent e){
	IPane p=null;
	if(currentMousePane!=null){
	    //p = currentMousePane;
	    currentMousePane.mouseReleased(e);
	    //currentMousePane = getPaneAt(e); // update
	}
	else{
	    p = getPaneAt(e);
	    if(p!=null){
		//currentMousePane = null;
		p.mouseReleased(e);
		currentMousePane = p;
	    }
	}
    }
    public void mouseClicked(MouseEvent e){
	//IOut.p();//
	
	IPane p = getPaneAt(e);
	if(p!=null){
	    p.mouseClicked(e);
	}
	
	//if(fullScreenPane==null){ if(p!=null) enableFullScreen(p); }
	//else disableFullScreen();
	
	currentMousePane = p; // update
    }
    public void mouseEntered(MouseEvent e){
	//IPane p = getPaneAt(e);
	//if(p!=null){ currentMousePane = p; }
	
	//IPane p = getPaneAt(e);
	//if(p!=null){ p.mouseEntered(e); }
    }
    public void mouseExited(MouseEvent e){
	//IPane p = getPaneAt(e);
	//if(p!=null){ p.mouseExited(e); }
    }
    public void mouseMoved(MouseEvent e){
	IPane p = getPaneAt(e);
	if(p!=null){
	    p.mouseMoved(e);
	}
    }
    public void mouseDragged(MouseEvent e){
	IPane p=null;
	if(currentMousePane!=null){ p = currentMousePane; }
	else{ p = getPaneAt(e); }
	if(p!=null){
	    p.mouseDragged(e);
	}
    }
    
    
    public void mouseWheelMoved(MouseWheelEvent e){
	if(currentMousePane!=null){ currentMousePane.mouseWheelMoved(e); }
	/*
	IPane p = getPaneAt(e);
	if(p!=null){
	    currentMousePane=p;
	    currentMousePane.mouseWheelMoved(e);
	}
	*/
    }
    
    
    public void keyPressed(KeyEvent e){
	
	int key = e.getKeyCode();
	boolean shift = e.isShiftDown();
	boolean control = e.isControlDown();
	
	if(key==KeyEvent.VK_F && /*!shift &&*/!control){
	    currentMousePane.focus();
	}
	/*
	else if(key==KeyEvent.VK_F && shift &&!control){
	    setBounds();
	    currentMousePane.focus();
	}
	*/
	else if(key==KeyEvent.VK_S&& !shift &&!control){
	    // fill & wireframe
	    currentMousePane.getView().mode().setDrawMode(true,true,false);
	}
	else if(key==KeyEvent.VK_S&& shift &&!control){
	    // toggle fill shading
	    //currentMousePane.getView().mode().toggleFill();
	    // fill 
	    currentMousePane.getView().mode().setDrawMode(false,true,false);
	}
	else if(key==KeyEvent.VK_W&& !shift &&!control){
	    // wireframe
	    currentMousePane.getView().mode().setDrawMode(true,false,false);
	}
	/*
	else if(key==KeyEvent.VK_W&& shift &&!control){
	    // toggle wireframe
	    currentMousePane.getView().mode().toggleWireframe();
	}
	*/
	else if(key==KeyEvent.VK_T&& !shift &&!control){
	    // transparent fill & wireframe
	    currentMousePane.getView().mode().setDrawMode(true,true,true);
	}
	else if(key==KeyEvent.VK_T&& shift &&!control){
	    // toggle transparency
	    //currentMousePane.getView().mode().toggleTransparent();
	    // transparent fill
	    currentMousePane.getView().mode().setDrawMode(false,true,true);
	}
	//else if(key==KeyEvent.VK_Q && control&& !shift){
	else if( (key==KeyEvent.VK_W || key==KeyEvent.VK_Q)
		 && control&& !shift){ // to match with Processing closing behavior
	    System.exit(0); // temporary.
	}
	else if(key==KeyEvent.VK_S && control&& !shift){
	    
	    // create folder of the base path if not existing
	    if(ig.basePath!=null){
		File baseDir = new File(ig.basePath);
		if(!baseDir.isDirectory()){
		    IOut.debug(20, "creating directory"+baseDir.toString());
		    if(!baseDir.mkdir()){
			IOut.err("failed to create directory: "+baseDir.toString());
		    }
		}
	    }
	    
	    File file = chooseFile(new String[][]{ new String[]{ "3dm", "3DM" },
						   new String[]{ "obj", "Obj", "OBJ" } },
				   new String[]{ "Rhinoceros 3D file v4 (.3dm)",
						 "Wavefront OBJ file (.obj)" },
				   "Save",
				   true,
				   ig.basePath,
				   null);
	    
	    if(file!=null) ig.saveFile(file.getAbsolutePath());
	    
	    /*
	    boolean canceled = false;
	    do{
		JFileChooser jfc = new JFileChooser(ig.basePath);
		int retval = jfc.showSaveDialog(null);
		
		if(retval==JFileChooser.APPROVE_OPTION){
		    File file = jfc.getSelectedFile();
		    ig.saveFile(file.getAbsolutePath());
		}
	    }while(canceled);
	    */
	    
	}
	else if(key==KeyEvent.VK_ENTER && !control&& !shift){
	    ig.pauseDynamics();
	    //if(ig.isDynamicsRunning()){ ig.pauseDynamics(); }
	    //else{ ig.resumeDynamics(); }
	}
	
	if(currentMousePane!=null){ currentMousePane.keyPressed(e); }
    }
    public void keyReleased(KeyEvent e){
	if(currentMousePane!=null){ currentMousePane.keyReleased(e); }
    }
    public void keyTyped(KeyEvent e){
	if(currentMousePane!=null){ currentMousePane.keyTyped(e); }
    }
    
    public void focusLost(FocusEvent e){
    }
    public void focusGained(FocusEvent e){
    }
    
    
    public void componentHidden(ComponentEvent e){
    }
    public void componentMoved(ComponentEvent e){
    }
    public void componentResized(ComponentEvent e){
	int w = e.getComponent().getBounds().width;
	int h = e.getComponent().getBounds().height;
	setSize(w,h);
    }
    public void componentShown(ComponentEvent e){
    }
    
    public IBounds getBounds(){ return bounds; } 
    
    public void setBounds(){
	if(bounds==null) bounds = new IBounds();
	if(ig.server().stateCount()!=serverStateCount){
	    bounds.setObjects(ig.server());
	    serverStateCount = ig.server().stateCount();
	    //IOut.err("bounds Updated: "+bounds); //
	}
    }
    
    
    // file chooser dialog
    public File chooseFile(String acceptableExtension,
			   String extensionDescription,
                           String approveButtonText,
			   boolean writing,
			   String defaultPath,
			   File defaultFile){
        return chooseFile(IFileFilter.createCaseVariation(acceptableExtension),
                          extensionDescription, approveButtonText,writing,
			  defaultPath, defaultFile);
    }
    
    public File chooseFile(String[] acceptableExtensions, String extensionDescription,
                           String approveButtonText, boolean writing,
			   String defaultPath, File defaultFile){
	String[][] extensions = new String[1][];
	extensions[0] = acceptableExtensions;
	String[] description = new String[1];
	description[0] = extensionDescription;

	return chooseFile(extensions, description, approveButtonText, writing, defaultPath, defaultFile);
	
	/*
        File file=null;
        boolean canceled=false;
	
	if(defaultPath==null) defaultPath=".";
        file = defaultFile;
	
        for(int i=0; i<acceptableExtensions.length; i++){
            if(!acceptableExtensions[i].startsWith(".")){
                acceptableExtensions[i] = "." + acceptableExtensions[i];
            }
        }    
        do{
            canceled=false; // in the case once canceled
            JFileChooser chooser = new JFileChooser(defaultPath);
            chooser.addChoosableFileFilter(new IFileFilter(acceptableExtensions,
							   extensionDescription));
            
            if(file!=null){
                chooser.setCurrentDirectory(new File(file.getParent()));
                chooser.setSelectedFile(file);
            }
            
            int result = chooser.showDialog(null, approveButtonText);
            
            if(result==JFileChooser.APPROVE_OPTION){
                file = chooser.getSelectedFile();
                String filename = file.toString();
                boolean endWithExtension=false;
                for(int i=0; (i<acceptableExtensions.length)&&!endWithExtension; i++){
                    if(filename.endsWith(acceptableExtensions[i])) endWithExtension=true;
                }
                if( ! endWithExtension ){
		    // should it be changed?
                    IOut.err("extension of file is invalid: "+filename);
                    filename = filename.concat(acceptableExtensions[0]);
                    file = new File(filename);
                    //IOut.err("renamed to "+ file.toString());
                }
                if(writing){
                    if(file.exists()){
                        IOut.err("overwiting file?: "+file.toString());
                        String message =
                            "file is existing\ndo you want to overwrite it?";
                        // Modal dialog with yes/no button
                        int answer = JOptionPane.showConfirmDialog(null, message);
                        if (answer == JOptionPane.YES_OPTION);
                        else if (answer == JOptionPane.NO_OPTION) return null;
                        else if (answer == JOptionPane.CANCEL_OPTION) canceled=true;
                        else return null;
                    }
                }
                else{
                    if(!file.exists()){
                        IOut.err("file doesn't exist "+file.toString());
                        String message = "file doesn't exist: "+file.toString();
                        // Modal dialog with yes/no button
                        JOptionPane.showMessageDialog(null, message);
                        canceled=true;
                    }
                }
                
            }
            else{ file=null; }
        }while(canceled);
        return file;
	*/
    }
    
    public File chooseFile(String[][] acceptableExtensions,
			   String[] extensionDescriptions,
                           String approveButtonText, boolean writing,
			   String defaultPath, File defaultFile){
        File file=null;
        boolean canceled=false;
	
	if(defaultPath==null) defaultPath=".";
        file = defaultFile;
	
	IFileFilter[] filters = new IFileFilter[acceptableExtensions.length];
        for(int i=0; i<acceptableExtensions.length; i++){
	    for(int j=0; j<acceptableExtensions[i].length; j++){
		if(!acceptableExtensions[i][j].startsWith(".")){
		    acceptableExtensions[i][j] = "." + acceptableExtensions[i][j];
		}
	    }
	    String description = "";
	    if(extensionDescriptions!=null && extensionDescriptions.length > i &&
	       extensionDescriptions[i] != null){
		description = extensionDescriptions[i];
	    }
	    filters[i] = new IFileFilter(acceptableExtensions[i], description);
        }
	
        do{
            canceled=false; // in the case once canceled
            JFileChooser chooser = new JFileChooser(defaultPath);
	    //for(int i=0; i<filters.length; i++){
	    for(int i=filters.length-1; i>=0; i--){ // opposite order
		chooser.addChoosableFileFilter(filters[i]);
	    }
	    
            if(file!=null){
                chooser.setCurrentDirectory(new File(file.getParent()));
                chooser.setSelectedFile(file);
            }
            
            int result = chooser.showDialog(null, approveButtonText);
            
            if(result==JFileChooser.APPROVE_OPTION){
                file = chooser.getSelectedFile();
                String filename = file.toString();
                boolean endWithExtension=false;
		
		javax.swing.filechooser.FileFilter currentFilter = chooser.getFileFilter();
		int currentFilterIndex = -1;
		for(int i=0; i<filters.length && currentFilterIndex<0; i++){
		    if(currentFilter==filters[i]) currentFilterIndex=i;
		}
		
		for(int i=0; currentFilterIndex>=0 &&
			(i<acceptableExtensions[currentFilterIndex].length) &&
			!endWithExtension; i++){
                    if(filename.endsWith(acceptableExtensions[currentFilterIndex][i])) endWithExtension=true;
                }
		
		if(currentFilterIndex>=0 && ! endWithExtension ){
		    // should it be changed?
                    //IOut.err("extension of file is invalid: "+filename);
                    filename = filename.concat(acceptableExtensions[currentFilterIndex][0]);
                    file = new File(filename);
                    //IOut.err("renamed to "+ file.toString());
                }
		
                if(writing){
                    if(file.exists()){
                        //IOut.err("overwiting file?: "+file.toString());
                        String message =
                            "file is existing\ndo you want to overwrite it?";
                        // Modal dialog with yes/no button
                        int answer = JOptionPane.showConfirmDialog(null, message);
                        if (answer == JOptionPane.YES_OPTION);
                        else if (answer == JOptionPane.NO_OPTION) return null;
                        else if (answer == JOptionPane.CANCEL_OPTION) canceled=true;
                        else return null;
                    }
                }
                else{
                    if(!file.exists()){
                        IOut.err("file doesn't exist "+file.toString());
                        String message = "file doesn't exist: "+file.toString();
                        // Modal dialog with yes/no button
                        JOptionPane.showMessageDialog(null, message);
                        canceled=true;
                    }
                }
                
            }
            else{ file=null; }
        }while(canceled);
        return file;
    }
    
    
    public File[] chooseFiles(String acceptableExtension,
                              String extensionDescription,
			      String approveButtonText,
			      String defaultPath){
        return chooseFiles(IFileFilter.createCaseVariation(acceptableExtension),
                           extensionDescription, approveButtonText, defaultPath);
    }
    
    public File[] chooseFiles(String[] acceptableExtensions,
                              String extensionDescription,
                              String approveButtonText,
			      String defaultPath){
        
        File[] files=null;
        boolean canceled=false;
        
        for(int i=0; i<acceptableExtensions.length; i++){
            if(!acceptableExtensions[i].startsWith(".")){
                acceptableExtensions[i] = "." + acceptableExtensions[i];
            }
        }

	if(defaultPath==null) defaultPath=".";
        
        canceled=false; // in the case once canceled
        JFileChooser chooser = new JFileChooser(defaultPath);
        chooser.addChoosableFileFilter(new IFileFilter(acceptableExtensions,
						       extensionDescription));
        
        chooser.setMultiSelectionEnabled(true);
        
        int result = chooser.showDialog(null, approveButtonText);
        
        if(result==JFileChooser.APPROVE_OPTION){
            files = chooser.getSelectedFiles();
        }
        else{ files=null; }
        return files;
    }                        
    
}
