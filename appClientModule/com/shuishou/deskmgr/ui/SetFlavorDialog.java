package com.shuishou.deskmgr.ui;

import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import com.shuishou.deskmgr.Messages;
import com.shuishou.deskmgr.beans.Flavor;
import com.shuishou.deskmgr.ui.OpenTableDialog.ChoosedDish;

public class SetFlavorDialog extends JDialog{
	private final Logger logger = Logger.getLogger(SetFlavorDialog.class.getName());
	private JPanel pChoosedFlavor = new JPanel(new FlowLayout(FlowLayout.LEFT));
	private JButton btnClose = new JButton(Messages.getString("CloseDialog"));
	private JTextField tfOther = new JTextField();
	private JButton btnAddOther = new JButton("Add");
	private Dialog parent;
	private ArrayList<Flavor> allFlavors;
	private ArrayList<Flavor> choosedFlavors = new ArrayList<>();
	
	public SetFlavorDialog(Dialog parent, String title, ArrayList<Flavor> allFlavors, ArrayList<Flavor> choosedFlavors){
		super(parent, title, true);
		this.parent = parent;
		this.allFlavors = allFlavors;
		if (choosedFlavors != null)
			this.choosedFlavors = choosedFlavors;
		initUI();
	}
	
	private void initUI(){
//		JPanel pFunction = new JPanel(new GridBagLayout());
//		pFunction.add(btnClose,		new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 50, 0, 0), 0, 0));

		pChoosedFlavor.setBorder(BorderFactory.createTitledBorder(Messages.getString("SetFlavorDialog.ChoosedFlavor")));
		for(Flavor f : choosedFlavors){
			final Flavor fi = f;
			final FlavorButton btn = new FlavorButton(f);
			btn.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {
					choosedFlavors.remove(fi);
					pChoosedFlavor.remove(btn);
					pChoosedFlavor.updateUI();
				}});
			pChoosedFlavor.add(btn);
		}
		JPanel pAllFlavor = new JPanel(new FlowLayout(FlowLayout.LEFT));
		pAllFlavor.setBorder(BorderFactory.createTitledBorder(Messages.getString("SetFlavorDialog.Flavor")));
		for(Flavor f : allFlavors){
			final Flavor fi = f;
			final FlavorButton btn = new FlavorButton(f);
			btn.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {
					for(Flavor chofi : choosedFlavors){
						if (chofi.getId() == btn.flavor.getId()){
							return;
						}
					}
					
					final FlavorButton choosedBtn = new FlavorButton(fi);
					choosedBtn.addActionListener(new ActionListener(){

						@Override
						public void actionPerformed(ActionEvent e) {
							choosedFlavors.remove(fi);
							pChoosedFlavor.remove(choosedBtn);
							pChoosedFlavor.updateUI();
						}});
					choosedFlavors.add(fi);
					pChoosedFlavor.add(choosedBtn);
					pChoosedFlavor.updateUI();
				}});
			pAllFlavor.add(btn);
		}
		btnClose.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				SetFlavorDialog.this.setVisible(false);
			}});
		btnAddOther.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				final Flavor f = new Flavor();
				f.setFirstLanguageName(tfOther.getText());
				f.setSecondLanguageName(tfOther.getText());
				final FlavorButton choosedBtn = new FlavorButton(f);
				choosedBtn.addActionListener(new ActionListener(){

					@Override
					public void actionPerformed(ActionEvent e) {
						choosedFlavors.remove(f);
						pChoosedFlavor.remove(choosedBtn);
						pChoosedFlavor.updateUI();
					}});
				choosedFlavors.add(f);
				pChoosedFlavor.add(choosedBtn);
				pChoosedFlavor.updateUI();
			}});
		btnClose.setPreferredSize(new Dimension(100, 50));
//		tfOther.setMinimumSize(new Dimension(200, 40));
		JPanel pBottom = new JPanel(new GridBagLayout());
		JLabel lbOther = new JLabel(Messages.getString("SetFlavorDialog.OtherFlavor"));
		pBottom.add(lbOther,			new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		pBottom.add(tfOther,			new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		pBottom.add(btnAddOther,		new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		pBottom.add(btnClose,			new GridBagConstraints(3, 0, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 150, 0, 0), 0, 0));
		
		Container c = this.getContentPane();
		c.setLayout(new GridBagLayout());
		c.add(pChoosedFlavor, 		new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		c.add(pAllFlavor, 			new GridBagConstraints(0, 1, 1, 1, 1, 2, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		c.add(pBottom, 				new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		
		this.setSize(600, 400);
		this.setLocation((int)(parent.getWidth() / 2 - this.getWidth() /2 + parent.getLocation().getX()), 
				(int)(parent.getHeight() / 2 - this.getHeight() / 2 + parent.getLocation().getY()));
	}
	
	
	public ArrayList<Flavor> getChoosedFlavors() {
		return choosedFlavors;
	}

	public void setChoosedFlavors(ArrayList<Flavor> choosedFlavors) {
		this.choosedFlavors = choosedFlavors;
	}


	class FlavorButton extends JButton{
		public Flavor flavor;
		public FlavorButton(Flavor f){
			super(f.getFirstLanguageName());
			this.flavor = f;
		}
	}
}
