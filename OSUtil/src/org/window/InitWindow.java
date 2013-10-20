package org.window;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import org.table.TableConvertBean;
import org.util.JdbcUtil;
import org.util.TableUtil;

/**
 * 数据库table 生成 实体bean 主窗体
 * @author 苟伟
 *
 */
public class InitWindow extends JSplitPane{
	private static final long serialVersionUID = 1L;
	private static int WIDTH = 630,HEIGHT = 500;  //主窗体的宽和高
	private static final JTextField jdbc_jar_text = new JTextField(10);
	private static final JTextField jdbc_driver_text = new JTextField(15);
	private static final JTextField jdbc_url_text = new JTextField(15);
	private static final JTextField jdbc_name_text = new JTextField(15);
	private static final JPasswordField jdbc_password_text = new JPasswordField(10);
	private static final JTable table = new JTable(100,2);
	private static  Vector<Vector<Object>> data = null;
	private static final Vector<String> columnNames = new Vector<String>();
	static{
		columnNames.add("表名");
		columnNames.add("选择");
	}
	private static JPanel panel = new JPanel(new GridLayout(2,1));
	private static JPanel panelTop = new JPanel(new GridLayout(9,1));
	private static JPanel panelCenter = new JPanel(new GridLayout(1,1));
	private static JPanel panelSelectDire = new JPanel();
	private static final JTextField selectDireTxt = new JTextField(20);
	private static final JTextField pageName = new JTextField(20);
	private static JPanel panelPage = new JPanel();
	private static JPanel panelRun = new JPanel();
	private static JPanel panelSouth = new JPanel();
	private static final JFrame jFrame = new JFrame();
	public InitWindow(){
		 try
		 {//显示外观风格
			 UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		 }
		 catch(Exception e){
			 e.printStackTrace();
		 } 
		 
		jFrame.setTitle("Abs将Table生成JavaBean窗口");
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension dimen = kit.getScreenSize();
		int x = (dimen.width - WIDTH)/ 2;
		int y = (dimen.height - HEIGHT)/ 2;
		jFrame.setLocation(x,y);
		jFrame.setSize(WIDTH, HEIGHT);
		createTopPanel();
		createGridTable();
		jFrame.setContentPane(this);
		 setOneTouchExpandable(true);
        setContinuousLayout(true);
        setPreferredSize(new Dimension (600,500));
        setOrientation (JSplitPane.HORIZONTAL_SPLIT);
        setLeftComponent(panelTop);
        setRightComponent(panel);  
        setDividerSize(3);
        setDividerLocation(270);
	        
		jFrame.setVisible(true);
	}
	//绘制panel左边部分
	private  void createTopPanel(){
		panelTop.setLocation(0, 0);
		JPanel jdbc_jar_panel = new JPanel();
		JPanel jdbc_driver_panel = new JPanel();
		JPanel jdbc_url_panel = new JPanel();
		JPanel jdbc_name_panel = new JPanel();
		JPanel jdbc_pass_panel = new JPanel();
		JPanel jdbc_button_panel = new JPanel();
		
		/***********添加默认值 *****************/
		
		jdbc_jar_text.setText("F:\\Workspaces\\AbsDao\\oracle-10.2.0.3.0.jar");
		jdbc_driver_text.setText("oracle.jdbc.driver.OracleDriver");
		jdbc_url_text.setText("jdbc:oracle:thin:@localhost:1521:orcl");
		jdbc_name_text.setText("sso");
		jdbc_password_text.setText("sso");
		
		JButton jButton3 = new JButton("select jar");
		jButton3.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) { 
				JFileChooser chooser = new JFileChooser();
				chooser.setCurrentDirectory(new File("."));
				chooser.addChoosableFileFilter(new FileFilter() {  //文件过滤器
					@Override
					public boolean accept(File f) {
						return (f.getAbsolutePath().endsWith(".jar") || f.getAbsolutePath().endsWith(".zip") || f.isDirectory());
					}
					@Override
					public String getDescription() {
						return "Jar文件";
					}
				});
				int r = chooser.showOpenDialog(new JFrame());
				if(r == JFileChooser.APPROVE_OPTION)
				{ 
					String path = chooser.getSelectedFile().getPath();
					jdbc_jar_text.setText(path);
				}
			}
		});
		jdbc_jar_panel.add(jdbc_jar_text,"West");
		jdbc_jar_panel.add(jButton3,"Center");
		panelTop.add(jdbc_jar_panel, "North");
		JLabel jdbc_driver_lable = new JLabel("driver: ");
		JLabel jdbc_url_lable = new JLabel(" url: ");
		JLabel jdbc_name_lable = new JLabel("name: ");
		JLabel jdbc_password_lable = new JLabel("password: ");
		
		jdbc_driver_panel.add(jdbc_driver_lable,"West");
		jdbc_driver_panel.add(jdbc_driver_text,"Center");
		panelTop.add(jdbc_driver_panel, "North");
		
		jdbc_url_panel.add(jdbc_url_lable,"West");
		jdbc_url_panel.add(jdbc_url_text,"Center");
		panelTop.add(jdbc_url_panel, "North");

		jdbc_name_panel.add(jdbc_name_lable,"West");
		jdbc_name_panel.add(jdbc_name_text,"Center");
		panelTop.add(jdbc_name_panel, "Center");
		
		jdbc_pass_panel.add(jdbc_password_lable,"West");
		jdbc_pass_panel.add(jdbc_password_text,"Center");
		panelTop.add(jdbc_pass_panel, "Center");
		
		JButton jButton1 = new JButton("conn as");
		JButton jButton2 = new JButton("cencel");
		
		jButton1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String jarPth = jdbc_jar_text.getText();
				String driver = jdbc_driver_text.getText();
				String url = jdbc_url_text.getText();
				String name = jdbc_name_text.getText();
				char[] pass = jdbc_password_text.getPassword();
				String passWord = null;
				if(check(jarPth,driver,url,name,pass)){
					passWord = String.valueOf(pass);
				}
				try {
					JdbcUtil jdbc = new JdbcUtil(driver,url,name,passWord,jarPth);
					jdbc.getInitConnection();
					TableUtil tableUtil = new TableUtil();
					List<String> tables = tableUtil.getTables();
					refreshTable(tables);
				} catch (SQLException e1) {
					e1.printStackTrace(); //日志记录C：/
					JOptionPane.showMessageDialog(jFrame, "连接失败!", "消息提示", JOptionPane.ERROR_MESSAGE);
					return ;
					
				} catch (ClassNotFoundException e1) {
					e1.printStackTrace(); //日志记录C：/
					JOptionPane.showMessageDialog(jFrame, "数据库驱动不正确,连接失败!", "消息提示", JOptionPane.ERROR_MESSAGE);
					return ;
				}
				
			}
		});
		jButton2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String jarPth = jdbc_jar_text.getText();
				String driver = jdbc_driver_text.getText();
				String url = jdbc_url_text.getText();
				String name = jdbc_name_text.getText();
				char[] pass = jdbc_password_text.getPassword();
				if(check(jarPth,driver,url,name,pass)){
					new JdbcUtil().close(null,null, JdbcUtil.conn());
				}
				
			}
		});
		
		jdbc_button_panel.add(jButton1,"West");
		jdbc_button_panel.add(jButton2,"Center");
		
		panelTop.add(jdbc_button_panel, "Center");
	}
	
	private static boolean check(String jarPath,String driver,String url,String name,char[] pass){

		if(jarPath == null || jarPath.trim().equals("")){
			JOptionPane.showMessageDialog(jFrame, "请输选择数据驱动jar文件", "消息提示", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		if(driver == null || driver.trim().equals("")){
			JOptionPane.showMessageDialog(jFrame, "请输入数据库连接驱动", "消息提示", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		if(url == null || url.trim().equals("")){
			JOptionPane.showMessageDialog(jFrame, "请输入数据库连接URL", "消息提示", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		if(name == null || name.trim().equals("")){
			JOptionPane.showMessageDialog(jFrame, "请输入数据库连接用户名", "消息提示", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		if(pass== null || pass.length == 0 ){
			JOptionPane.showMessageDialog(jFrame, "请输入数据库连接用户密码", "消息提示", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return true;
	}
	//从新加载表格数据
	public void refreshTable(List<String> tableNames){
		 Vector<Vector<Object>> tables = new Vector<Vector<Object>>();
		for(String t : tableNames){
			Vector<Object> vector = new Vector<Object>();
			vector.add(t);
			vector.add(true);
			tables.add(vector);
		}
		if(data!= null) 
			data.clear();
		data = tables;
		CheckTableModle tableModel=new CheckTableModle(data,columnNames);
        table.setModel(tableModel);
        table.getTableHeader().setDefaultRenderer(new CheckHeaderCellRenderer(table));
        table.validate();
        table.repaint(); 
		
		
	}
	 //绘制右边部分
	public  void createGridTable( ){
		CheckTableModle tableModel=new CheckTableModle(data,columnNames);
        table.setModel(tableModel);
        table.getTableHeader().setDefaultRenderer(new CheckHeaderCellRenderer(table));
        JScrollPane sp=new JScrollPane(table,ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS ,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        panelCenter.setOpaque(true);
        panelCenter.add(sp,BorderLayout.CENTER);
        panelCenter.setBackground(Color.white);
        JButton jButton1 = new JButton("run as");
		JButton jButton2 = new JButton("close");
		jButton1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//得到表格选中的数据
				int column = table.getColumnCount();
				int rowlum = table.getRowCount();
				if(column == 0 && rowlum == 0){
					JOptionPane.showMessageDialog(jFrame, "没有可生成实体bean的表", "消息提示", JOptionPane.DEFAULT_OPTION);
					return ;
				}
				if( selectDireTxt.getText() == null || "".equals(selectDireTxt.getText().trim())){
					JOptionPane.showMessageDialog(jFrame, "请选择JavaBean文件存放路径", "消息提示", JOptionPane.DEFAULT_OPTION);
					return ;
				}
				
				List<String> tableNames = new  ArrayList<String>();
				for(int i =0 ;i < rowlum ; i++){
					if((Boolean)table.getValueAt(i, 1)){
						tableNames.add((String)table.getValueAt(i, 0));
					}
				}
				try {
					TableConvertBean tableConvertBean = new TableConvertBean();
					String pName = pageName.getText();
					if(pName != null && pName.length() > 0){
						pName = pName.trim();
						pName = pName.lastIndexOf(";")>0? pName.substring(0,pName.lastIndexOf(";")):pName;
					}
					int count  = tableConvertBean.convertBean(tableNames,selectDireTxt.getText(),pName);
					
					String driver = jdbc_driver_text.getText();
					String url = jdbc_url_text.getText();
					String name = jdbc_name_text.getText();
					String pass ="";
					for(char c : jdbc_password_text.getPassword()){
						pass+=c;
					}
					int abs = TableConvertBean.createAbsProperties(selectDireTxt.getText(), driver, url, name, pass);
					JOptionPane.showMessageDialog(jFrame, "文件生成成功! ["+count+" 个java文件,"+abs+"个properties文件!]", "消息提示", JOptionPane.DEFAULT_OPTION);
				} catch (Exception e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(jFrame, "生成实体bean异常", "消息提示", JOptionPane.ERROR_MESSAGE);
					return ;
				}
			}
		});
		jButton2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(-1);
			}
		});
		panelRun.add(jButton1,"West");
		panelRun.add(jButton2,"Center");
		
		JButton jButton3 = new JButton("select dir");
		jButton3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.setCurrentDirectory(new File("."));
				int r = chooser.showOpenDialog(new JFrame());
				if(r == JFileChooser.APPROVE_OPTION)
				{ 
					String path = chooser.getSelectedFile().getPath();
					selectDireTxt.setText(path);
				}
			}
		});
		Label pageL = new Label("package name:");
		panelPage.add(pageL,"West");
		panelPage.add(pageName,"Center");
		panelSelectDire.add(selectDireTxt,"West");
		panelSelectDire.add(jButton3,"Center");
		panelSouth.add(panelSelectDire,"North");
		panelSouth.add(panelPage,"South");
		panelSouth.add(panelRun,"South");
		
		panel.add(panelCenter,"North");
		panel.add(panelSouth,"South");
		
	}

  public class CheckTableModle extends DefaultTableModel {

	private static final long serialVersionUID = 1L;

		public CheckTableModle( Vector<Vector<Object>> data, Vector<String> columnNames) {
	        super(data,columnNames);
	    }
	    public Class<?> getColumnClass(int c) {
	        return getValueAt(1, c).getClass();
	    }
	    public void selectAllOrNull(boolean value) {
	        for (int i = 0; i < getRowCount(); i++) {
	            this.setValueAt(value, i, 1);
	        }
	    }
  }
//设置全选
public class CheckHeaderCellRenderer implements TableCellRenderer {
    CheckTableModle tableModel;
    JTableHeader tableHeader;
    final JCheckBox selectBox;

    public CheckHeaderCellRenderer(JTable table) {
        this.tableModel = (CheckTableModle)table.getModel();
        this.tableHeader = table.getTableHeader();
        selectBox = new JCheckBox("选择");
        selectBox.setSelected(true);
        tableHeader.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() > 0) {
                    //获得选中列
                    int selectColumn = tableHeader.columnAtPoint(e.getPoint());
                    if (selectColumn == 1) {
                        boolean value = !selectBox.isSelected();
                        selectBox.setSelected(value);
                        tableModel.selectAllOrNull(value);
                        tableHeader.repaint();
                    }
                }
            }
        });
    }
    
    public Component getTableCellRendererComponent(JTable table, Object value,boolean isSelected, boolean hasFocus, int row, int column) {
        String valueStr = (String) value;
        JLabel label = new JLabel(valueStr);
        label.setHorizontalAlignment(SwingConstants.CENTER); // 表头标签剧中
        selectBox.setHorizontalAlignment(SwingConstants.CENTER);// 表头标签剧中
        selectBox.setBorderPainted(true);
        JComponent component = (column ==1) ? selectBox : label;
        component.setForeground(tableHeader.getForeground());
        component.setBackground(tableHeader.getBackground());
        component.setFont(tableHeader.getFont());
        component.setBorder(UIManager.getBorder("TableHeader.cellBorder"));

        return component;
    }
    
}

	public static void main(String[] args) {
		new Thread(new Runnable() {
			public void run() {
				new InitWindow();
			}
		}).start();
	}
	
}
