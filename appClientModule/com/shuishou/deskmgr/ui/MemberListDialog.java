package com.shuishou.deskmgr.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;

import com.shuishou.deskmgr.ConstantValue;
import com.shuishou.deskmgr.Messages;
import com.shuishou.deskmgr.beans.Member;


public class MemberListDialog extends JDialog{

	private Member choosedMember;
	private JTable table = new JTable();
	private MemberTableModel model = new MemberTableModel();
	private MainFrame parent;
	
	public MemberListDialog(MainFrame parent, ArrayList<Member> searchResult, int width, int height){
		setTitle("Choose Member");
		setSize(width, height);
		this.setModal(true);
		this.parent = parent;
		initUI(searchResult);
		
	}
	
	private void initUI(ArrayList<Member> searchResult){
		model.setData(searchResult);
		table.setModel(model);
		table.setRowHeight(40);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getColumnModel().getColumn(0).setPreferredWidth(200);
		table.getColumnModel().getColumn(1).setPreferredWidth(200);
		table.getColumnModel().getColumn(2).setPreferredWidth(150);
		table.getColumnModel().getColumn(3).setPreferredWidth(150);
		table.getColumnModel().getColumn(4).setPreferredWidth(120);
		table.getColumnModel().getColumn(5).setPreferredWidth(120);
		table.getColumnModel().getColumn(6).setPreferredWidth(120);
		JScrollPane jspTable = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		JButton btnConfirm = new JButton(Messages.getString("ConfirmDialog"));
		JButton btnCancel = new JButton(Messages.getString("CloseDialog"));
		JPanel pButton = new JPanel();
		pButton.add(btnConfirm);
		pButton.add(btnCancel);
		Container c = this.getContentPane();
		c.setLayout(new BorderLayout());
		c.add(jspTable, BorderLayout.CENTER);
		c.add(pButton, BorderLayout.SOUTH);
		setLocation((int)(parent.getWidth() / 2 - this.getWidth() /2 + parent.getLocation().getX()), 
				(int)(parent.getHeight() / 2 - this.getHeight() / 2 + parent.getLocation().getY()));
		btnConfirm.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				doConfirm();
				
			}});
		btnCancel.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				MemberListDialog.this.setVisible(false);
			}});
		table.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					doConfirm();
				}
			}
		});
	}
	
	private void doConfirm(){
		if (table.getSelectedRow() < 0){
			JOptionPane.showMessageDialog(this, "No select any record");
			return;
		}
		choosedMember = ((MemberTableModel)table.getModel()).getObjectAt(table.getSelectedRow());
		this.setVisible(false);
	}
	
	public void doEnterClick(){
		doConfirm();
	}
	
	public Member getChoosedMember(){
		return choosedMember;
	}
	
	class MemberTableModel extends DefaultTableModel{
		private ArrayList<Member> items = new ArrayList<>();
		private String[] header = new String[]{"Name", "Member Card", "Join Date", "Telephone", "Points", "Discount Rate", "Balance"};

		public MemberTableModel(){
		}
		
		public MemberTableModel(ArrayList<Member> items){
			this.items = items;
		}
		@Override
		public int getRowCount() {
			if (items == null)
				return 0;
			return items.size();
		}

		@Override
		public int getColumnCount() {
			return header.length;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			Member m = getObjectAt(rowIndex);
			switch(columnIndex){
			case 0:
				return m.getName();
			case 1:
				return m.getMemberCard();
			case 2:
				return ConstantValue.DFYMD.format(m.getCreateTime());
			case 3:
				return m.getTelephone();
			case 4:
				return m.getScore();
			case 5:
				return m.getDiscountRate();
			case 6:
				return String.format(ConstantValue.FORMAT_DOUBLE, m.getBalanceMoney());
			}
			return "";
		}
		
		@Override
		public String getColumnName(int column) {
			return header[column];
	    }
		
		public void setData(ArrayList<Member> items){
			this.items = items;
		}
		
		public ArrayList<Member> getData(){
			return items;
		}
		
		public Member getObjectAt(int index){
			return items.get(index);
		}
		
		public boolean isCellEditable(int row, int column) {
        return false;
    }
	}
}
