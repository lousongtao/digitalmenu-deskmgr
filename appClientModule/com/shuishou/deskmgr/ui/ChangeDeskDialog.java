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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.shuishou.deskmgr.ConstantValue;
import com.shuishou.deskmgr.Messages;
import com.shuishou.deskmgr.beans.Desk;
import com.shuishou.deskmgr.beans.DeskWithIndent;
import com.shuishou.deskmgr.beans.HttpResult;
import com.shuishou.deskmgr.http.HttpUtil;
import com.shuishou.deskmgr.ui.components.JBlockedButton;

public class ChangeDeskDialog extends JDialog {
	private final Logger logger = Logger.getLogger(ChangeDeskDialog.class.getName());
	private Desk desk;
	private MainFrame mainFrame;
	private JButton btnClose = new JButton(Messages.getString("CloseDialog"));
	private JBlockedButton btnConfirm = new JBlockedButton(Messages.getString("DishSubitemDialog.Confirm"), null);
	private ArrayList<DeskCell> listDeskCell = new ArrayList<>();
	private String mergeResponse;
	public ChangeDeskDialog(MainFrame mainFrame, String title, Desk desk, ArrayList<Desk> availableDesks){
		super(mainFrame, title, true);
		this.mainFrame = mainFrame;
		this.desk = desk;
		initUI(availableDesks);
	}
	
	private void initUI(ArrayList<Desk> availableDesks){
		btnClose.setPreferredSize(new Dimension(150, 50));
		btnConfirm.setPreferredSize(new Dimension(150, 50));
		JPanel pButton = new JPanel(new FlowLayout(FlowLayout.CENTER, 100, 5));
		pButton.add(btnConfirm);
		pButton.add(btnClose);
		
		JPanel pAvailable = new JPanel();
		pAvailable.setBorder(BorderFactory.createTitledBorder("Available Tables"));
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
				ChangeDeskDialog.this.setVisible(false);
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
	
	private void doConfirm(){
		DeskCell selected = null;
		for(DeskCell dc : listDeskCell){
			if (dc.isSelected()){
				selected = dc;
				break;
			}
		}
		if (selected == null){
			JOptionPane.showMessageDialog(this, Messages.getString("MainFrame.NeedSelectOneDesk"), Messages.getString("MainFrame.Error"), JOptionPane.YES_OPTION); //$NON-NLS-1$ //$NON-NLS-2$
			return;
		}
		String url = "indent/changedesks";
		Map<String, String> params = new HashMap<String, String>();
		params.put("userId", mainFrame.getOnDutyUser().getId() + "");
		params.put("deskId1", desk.getId() + "");
		params.put("deskId2", selected.getDesk().getId() + "");
		String response = HttpUtil.getJSONObjectByPost(ConstantValue.SERVER_URL + url, params, "UTF-8");
		if (response == null || response.length() == 0){
			logger.error(ConstantValue.DFYMDHMS.format(new Date()) + "\n");
			logger.error("get null from server while changing tables. URL = " + url + ", param = "+ params);
			JOptionPane.showMessageDialog(this, "get null from server while changing tables. URL = " + url + ", param = "+ params);
			return;
		}
		Gson gson = new GsonBuilder().setDateFormat(ConstantValue.DATE_PATTERN_YMDHMS).create();
		HttpResult<ArrayList<DeskWithIndent>> result = gson.fromJson(response, new TypeToken<HttpResult<ArrayList<DeskWithIndent>>>(){}.getType());
		if (!result.success){
			logger.error(ConstantValue.DFYMDHMS.format(new Date()) + "\n");
			logger.error("return false while changing tables. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(this, result.result);
			return;
		}
//		mainFrame.loadCurrentIndentInfo();
		mergeResponse = response;
		ChangeDeskDialog.this.setVisible(false);
	}
	
	
	
	public String getMergeResponse() {
		return mergeResponse;
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
			setPreferredSize(new Dimension(50,50));
			addMouseListener(new MouseAdapter(){
				public void mouseClicked(MouseEvent e){
					//unselect all first, then select this
					for(DeskCell dc : listDeskCell){
						dc.setSelected(false);
						dc.setBackground(colorUnselect);
					}
					isSelected = true;
					DeskCell.this.setBackground(colorSelect);
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
