package com.shuishou.deskmgr.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import com.shuishou.deskmgr.Messages;
import com.shuishou.deskmgr.beans.Desk;
import com.shuishou.deskmgr.ui.components.JBlockedButton;

public class MergeDeskDialog extends JDialog {
	private final Logger logger = Logger.getLogger(MergeDeskDialog.class.getName());
	private MainFrame mainFrame;
	private JButton btnClose = new JButton(Messages.getString("CloseDialog"));
	private JBlockedButton btnConfirm = new JBlockedButton(Messages.getString("DishSubitemDialog.Confirm"), null);
	private ArrayList<DeskCell> listDeskCell = new ArrayList<>();
	/**
	 * use this flag to know this dialog closed with CANCEL or CONFIRM
	 */
	public boolean isConfirm = false;
	public MergeDeskDialog(MainFrame mainFrame, String title, ArrayList<Desk> desks){
		super(mainFrame, title, true);
		this.mainFrame = mainFrame;
		initUI(desks);
	}
	
	private void initUI(ArrayList<Desk> availableDesks){
		btnClose.setPreferredSize(new Dimension(150, 50));
		btnConfirm.setPreferredSize(new Dimension(150, 50));
		JPanel pButton = new JPanel(new FlowLayout(FlowLayout.CENTER, 100, 5));
		pButton.add(btnConfirm);
		pButton.add(btnClose);
		
		JPanel pAvailable = new JPanel(new FlowLayout(FlowLayout.LEFT));
		pAvailable.setBorder(BorderFactory.createTitledBorder("All Tables"));
		pAvailable.setBackground(Color.white);
		for (int i = 0; i < availableDesks.size(); i++) {
			DeskCell dc = new DeskCell(availableDesks.get(i));
			pAvailable.add(dc);
			listDeskCell.add(dc);
		}
		
		Container c = this.getContentPane();
		c.setLayout(new BorderLayout());
		c.add(pAvailable, BorderLayout.CENTER);
		c.add(pButton,BorderLayout.SOUTH);
		
		btnClose.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				MergeDeskDialog.this.setVisible(false);
			}});
		
		btnConfirm.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				doConfirm();
			}});
		this.setSize(new Dimension(900,400));
		this.setLocation((int)(mainFrame.getWidth() / 2 - this.getWidth() /2 + mainFrame.getLocation().getX()), 
				(int)(mainFrame.getHeight() / 2 - this.getHeight() / 2 + mainFrame.getLocation().getY()));
	}
	
	public ArrayList<DeskCell> getSelectDesks(){
		ArrayList<DeskCell> selected = new ArrayList<>();
		for (DeskCell dc : listDeskCell) {
			if (dc.isSelected()) {
				selected.add(dc);
			}
		}
		return selected;
	}
	
	private void doConfirm() {
		ArrayList<DeskCell> selected = getSelectDesks();
		if (selected.size() < 2) {
			JOptionPane.showMessageDialog(this, Messages.getString("ChangeDeskDialog.NeedSelectTwoDesk"), //$NON-NLS-1$
					Messages.getString("MainFrame.Error"), JOptionPane.YES_OPTION); //$NON-NLS-1$
			return;
		}
		isConfirm = true;
		setVisible(false);
	}
	
	class DeskCell extends JPanel {
		private Desk desk;
		private boolean isSelected = false;
		private Color colorUnselect = new Color(201,255,255);
		private Color colorSelect = new Color(209,210,255);
		public DeskCell(Desk desk){
			this.desk = desk;
			setBackground(colorUnselect);
			JLabel lbDeskNo = new JLabel(desk.getName());
			add(lbDeskNo);
			setPreferredSize(new Dimension(70,70));
			addMouseListener(new MouseAdapter(){
				public void mouseClicked(MouseEvent e){
					isSelected = !isSelected;
					if (isSelected)
						DeskCell.this.setBackground(colorSelect);
					else 
						DeskCell.this.setBackground(colorUnselect);
				}
			});
		}
		
		public boolean isSelected() {
			return isSelected;
		}

		public void setSelected(boolean isSelected) {
			this.isSelected = isSelected;
		}

		public Desk getDesk() {
			return desk;
		}

		public void setDesk(Desk desk) {
			this.desk = desk;
		}
	}
	
}
