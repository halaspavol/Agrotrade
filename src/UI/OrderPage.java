package UI;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Properties;

import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import Model.Model.StockProduct;
import Model.Model.Supplier;

import javax.swing.border.EtchedBorder;
import java.awt.Color;
import javax.swing.JList;

import Controller.OrderController;
import Controller.ParsingHelper;
import Model.Model.Customer;
import Model.Model.LoginContainer;
import Model.Model.MessagesEnum;
import Model.Model.Order;
import Model.Model.OrderLine;
import Model.Model.OrderPageType;
import Model.Model.Sale;

import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import javax.swing.JSpinner;
import javax.swing.SwingConstants;

public class OrderPage extends JDialog {
	private OrderController orderCtrl = new OrderController();
	
	private final JPanel contentPanel = new JPanel();
	private JTextField quantityField = new JTextField();;
	private JTextField noteField;
	private JComboBox<String> customersComboBox;
	private JComboBox<String> stockProductsComboBox;
	private UtilDateModel shippingDateModel;
	private UtilDateModel deliveryDateModel;
	
	private JLabel onStockValue;
	private JLabel totalPriceValue;
	
	private JLabel msgLbl = new JLabel("");
	private JLabel msgOrderLineLbl = new JLabel("");
	
	private JButton btnAdd = new JButton("Add");
	private GridBagConstraints gbc_btnAdd = new GridBagConstraints();
	
	private ArrayList<StockProduct> stockProducts = new ArrayList<StockProduct>();
	private ArrayList<Customer> customers = new ArrayList<Customer>();
	
	private DefaultComboBoxModel<String> stockProductsDefaultModel = new DefaultComboBoxModel<String>();
	private DefaultComboBoxModel<String> customersDefaultModel = new DefaultComboBoxModel<String>();
	
	private StockProduct selectedStockProduct = null;
	private int availableAmountOfSelectedProduct;
	private ArrayList<OrderLine> orderLines = new ArrayList<OrderLine>();
	private Order order = null;
	
	private String columns[]={"Barcode", "Name", "On stock", "Requested quantity", "Quantity"};
	private Object[][] ols = new Object[0][];				
	private DefaultTableModel olsTabelModel;
	private JTable olsTable;
	private JScrollPane scrollPane;
	private GridBagConstraints gbc_scrollPane = new GridBagConstraints();
	
	public static void start(OrderPageType type) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					OrderPage dialog = new OrderPage(type);
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dialog.setVisible(true);
				} catch (Exception e) {
					throw e;
				}
			}
		});
	}

	/**
	 * Create the dialog.
	 */
	public OrderPage(OrderPageType type) {
		setBounds(150, 150, 1280, 800);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[]{0, 100, 259, 84, 0, 0};
		gbl_contentPanel.rowHeights = new int[]{0, 45, 0, 0, 200, 30, 24, 30, 30, 0};
		gbl_contentPanel.columnWeights = new double[]{1.0, 0.0, 1.0, 0.0, 1.0, Double.MIN_VALUE};
		gbl_contentPanel.rowWeights = new double[]{1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE, 1.0};
		contentPanel.setLayout(gbl_contentPanel);
		{
			JLabel title = new JLabel("Create Order");
			title.setFont(new Font("Tahoma", Font.BOLD, 14));
			GridBagConstraints gbc_title = new GridBagConstraints();
			gbc_title.insets = new Insets(0, 0, 5, 5);
			gbc_title.gridx = 2;
			gbc_title.gridy = 1;
			contentPanel.add(title, gbc_title);
		}
		{
			JLabel customerCvrNoLbl = new JLabel("Customer");
			GridBagConstraints gbc_customerCvrNoLbl = new GridBagConstraints();
			gbc_customerCvrNoLbl.anchor = GridBagConstraints.WEST;
			gbc_customerCvrNoLbl.insets = new Insets(0, 0, 5, 5);
			gbc_customerCvrNoLbl.gridx = 1;
			gbc_customerCvrNoLbl.gridy = 2;
			contentPanel.add(customerCvrNoLbl, gbc_customerCvrNoLbl);
		}
		{

			customersComboBox = new JComboBox<String>(customersDefaultModel);
			GridBagConstraints gbc_customerComboBox = new GridBagConstraints();
			gbc_customerComboBox.fill = GridBagConstraints.HORIZONTAL;
			gbc_customerComboBox.insets = new Insets(0, 0, 5, 5);
			gbc_customerComboBox.gridx = 2;
			gbc_customerComboBox.gridy = 2;
			contentPanel.add(customersComboBox, gbc_customerComboBox);
		}
		{
			JPanel panel = new JPanel();
			panel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(160, 160, 160)), "Add Products", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
			GridBagConstraints gbc_panel = new GridBagConstraints();
			gbc_panel.insets = new Insets(0, 0, 5, 5);
			gbc_panel.fill = GridBagConstraints.BOTH;
			gbc_panel.gridwidth = 3;
			gbc_panel.gridx = 1;
			gbc_panel.gridy = 3;
			contentPanel.add(panel, gbc_panel);
			GridBagLayout gbl_panel = new GridBagLayout();
			gbl_panel.columnWidths = new int[]{100, 259, 84, 0};
			gbl_panel.rowHeights = new int[]{0, 0, 0, 0, 0};
			gbl_panel.columnWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
			gbl_panel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
			panel.setLayout(gbl_panel);
			{
				JLabel selectedProductLbl = new JLabel("Select Product");
				GridBagConstraints gbc_selectedProductLbl = new GridBagConstraints();
				gbc_selectedProductLbl.anchor = GridBagConstraints.WEST;
				gbc_selectedProductLbl.insets = new Insets(0, 0, 5, 5);
				gbc_selectedProductLbl.gridx = 0;
				gbc_selectedProductLbl.gridy = 0;
				panel.add(selectedProductLbl, gbc_selectedProductLbl);
			}
			{

				stockProductsComboBox = new JComboBox<String>(stockProductsDefaultModel);
				stockProductsComboBox.addItemListener(new ItemListener() {
					public void itemStateChanged(ItemEvent e) {
						getCurrentStock();
					}
				});
				GridBagConstraints gbc_comboBox = new GridBagConstraints();
				gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
				gbc_comboBox.insets = new Insets(0, 0, 5, 5);
				gbc_comboBox.gridx = 1;
				gbc_comboBox.gridy = 0;
				panel.add(stockProductsComboBox, gbc_comboBox);
			}
			{
				JLabel onStockLbl = new JLabel("On Stock");
				GridBagConstraints gbc_onStockLbl = new GridBagConstraints();
				gbc_onStockLbl.anchor = GridBagConstraints.WEST;
				gbc_onStockLbl.insets = new Insets(0, 0, 5, 5);
				gbc_onStockLbl.gridx = 0;
				gbc_onStockLbl.gridy = 1;
				panel.add(onStockLbl, gbc_onStockLbl);
			}
			{
				onStockValue = new JLabel("");
				GridBagConstraints gbc_onStockValue = new GridBagConstraints();
				gbc_onStockValue.anchor = GridBagConstraints.WEST;
				gbc_onStockValue.insets = new Insets(0, 0, 5, 5);
				gbc_onStockValue.gridx = 1;
				gbc_onStockValue.gridy = 1;
				panel.add(onStockValue, gbc_onStockValue);
			}
			{
				Object[][] data = new Object[0][];
				
				olsTabelModel = new DefaultTableModel(data, columns);
				olsTable = new JTable(olsTabelModel);
				scrollPane = new JScrollPane(olsTable);
				gbc_scrollPane.fill = GridBagConstraints.BOTH;
				
				gbc_scrollPane.insets = new Insets(0, 0, 5, 5);
				gbc_scrollPane.gridx = 2;
				gbc_scrollPane.gridy = 4;
				contentPanel.add(scrollPane, gbc_scrollPane);
			}
			{
				btnAdd.setEnabled(false);
				btnAdd.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						try {
							contentPanel.remove(scrollPane);
							int requestedAmount = ParsingHelper.tryParseInt(quantityField.getText());
							
							if(requestedAmount < 1) {
								throw new Exception(MessagesEnum.VALUEHIGHERTHANZEROERROR.text);
							}
							
							availableAmountOfSelectedProduct = selectedStockProduct.getAmount();
							orderLines.stream().filter(ol -> ol.getStockProduct().getProduct().getBarcode() == selectedStockProduct.getProduct().getBarcode())
											   .forEach(ol -> {
												   availableAmountOfSelectedProduct -= ol.getAmount();
											   });
							int amount = requestedAmount > availableAmountOfSelectedProduct ? availableAmountOfSelectedProduct : requestedAmount;
							orderLines.add(new OrderLine(-1, requestedAmount, amount, selectedStockProduct));
							
							totalPriceValue.setText(String.valueOf(orderCtrl.calculateTotalPrice(orderLines, type)));
							
							ols = new Object[orderLines.size()][];
							
							for(int i = 0; i < orderLines.size(); i++) {
								OrderLine ol = orderLines.get(i);
								Object [] newData = {
									ol.getStockProduct().getProduct().getBarcode(),
									ol.getStockProduct().getProduct().getName(),
									Integer.toString(ol.getStockProduct().getAmount()),
									Integer.toString(ol.getRequestedAmount()),
									Integer.toString(ol.getAmount()),
								};

								ols[i] = newData;
							}
							
							quantityField.setText("");
							
							olsTabelModel = new DefaultTableModel(ols, columns);
							olsTable = new JTable(olsTabelModel);
							scrollPane = new JScrollPane(olsTable);
							
							contentPanel.add(scrollPane, gbc_scrollPane);
							contentPanel.revalidate();
							contentPanel.repaint();
							
							msgOrderLineLbl.setText(MessagesEnum.PRODUCTADDEDTOORDER.text);
							msgOrderLineLbl.setForeground(Color.GREEN);
						}
						catch(NumberFormatException e1) {
							contentPanel.add(scrollPane, gbc_scrollPane);
							e1.printStackTrace();
							msgOrderLineLbl.setText(MessagesEnum.PARSEERROR.text);
							msgOrderLineLbl.setForeground(Color.RED);
						} catch (Exception e2) {
							contentPanel.add(scrollPane, gbc_scrollPane);
							e2.printStackTrace();
							msgOrderLineLbl.setText(e2.getMessage());
							msgOrderLineLbl.setForeground(Color.RED);
						}
					}
				});
				gbc_btnAdd.insets = new Insets(0, 0, 5, 0);
				gbc_btnAdd.gridx = 2;
				gbc_btnAdd.gridy = 1;
				panel.add(btnAdd, gbc_btnAdd);
			}
			{
				JLabel quantityLbl = new JLabel("Quantity");
				GridBagConstraints gbc_quantityLbl = new GridBagConstraints();
				gbc_quantityLbl.anchor = GridBagConstraints.WEST;
				gbc_quantityLbl.insets = new Insets(0, 0, 5, 5);
				gbc_quantityLbl.gridx = 0;
				gbc_quantityLbl.gridy = 2;
				panel.add(quantityLbl, gbc_quantityLbl);
			}
			{
				quantityField.getDocument().addDocumentListener(new DocumentListener() {
					public void changedUpdate(DocumentEvent e) {
						repaint();
					}
					public void removeUpdate(DocumentEvent e) {
						repaint();
					}
					public void insertUpdate(DocumentEvent e) {
						repaint();
					}
	
				    public void repaint() {
				    	panel.remove(btnAdd);
						boolean enabled = quantityField.getText().length() != 0;
						btnAdd.setEnabled(enabled);
						panel.add(btnAdd, gbc_btnAdd);
						
						panel.revalidate();
						panel.repaint();
					  }
					});
				GridBagConstraints gbc_quantityField = new GridBagConstraints();
				gbc_quantityField.fill = GridBagConstraints.HORIZONTAL;
				gbc_quantityField.insets = new Insets(0, 0, 5, 5);
				gbc_quantityField.gridx = 1;
				gbc_quantityField.gridy = 2;
				panel.add(quantityField, gbc_quantityField);
				quantityField.setColumns(10);
			}
			{
				GridBagConstraints gbc_msgOrderLineLbl = new GridBagConstraints();
				gbc_msgOrderLineLbl.anchor = GridBagConstraints.WEST;
				gbc_msgOrderLineLbl.insets = new Insets(0, 0, 0, 5);
				gbc_msgOrderLineLbl.gridx = 1;
				gbc_msgOrderLineLbl.gridy = 3;
				panel.add(msgOrderLineLbl, gbc_msgOrderLineLbl);
			}
		}
		{
			JLabel totalPriceLbl = new JLabel("Total Price ");
			GridBagConstraints gbc_totalPriceLbl = new GridBagConstraints();
			gbc_totalPriceLbl.anchor = GridBagConstraints.WEST;
			gbc_totalPriceLbl.insets = new Insets(0, 0, 5, 5);
			gbc_totalPriceLbl.gridx = 1;
			gbc_totalPriceLbl.gridy = 5;
			contentPanel.add(totalPriceLbl, gbc_totalPriceLbl);
		}
		{
			totalPriceValue = new JLabel("");
			GridBagConstraints gbc_totalPriceValue = new GridBagConstraints();
			gbc_totalPriceValue.anchor = GridBagConstraints.WEST;
			gbc_totalPriceValue.insets = new Insets(0, 0, 5, 5);
			gbc_totalPriceValue.gridx = 2;
			gbc_totalPriceValue.gridy = 5;
			contentPanel.add(totalPriceValue, gbc_totalPriceValue);
		}
		{
			JLabel noteLbl = new JLabel("Note");
			GridBagConstraints gbc_noteLbl = new GridBagConstraints();
			gbc_noteLbl.anchor = GridBagConstraints.WEST;
			gbc_noteLbl.insets = new Insets(0, 0, 5, 5);
			gbc_noteLbl.gridx = 1;
			gbc_noteLbl.gridy = 6;
			contentPanel.add(noteLbl, gbc_noteLbl);
		}
		{
			noteField = new JTextField();
			noteField.setColumns(10);
			GridBagConstraints gbc_noteField = new GridBagConstraints();
			gbc_noteField.insets = new Insets(0, 0, 5, 5);
			gbc_noteField.fill = GridBagConstraints.HORIZONTAL;
			gbc_noteField.gridx = 2;
			gbc_noteField.gridy = 6;
			contentPanel.add(noteField, gbc_noteField);
		}
		{
			JPanel buttonPane = new JPanel();
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			GridBagLayout gbl_buttonPane = new GridBagLayout();
			gbl_buttonPane.columnWidths = new int[]{81, 70, 311, 80, 84, 0};
			gbl_buttonPane.rowHeights = new int[]{23, 0};
			gbl_buttonPane.columnWeights = new double[]{1.0, 0.0, 1.0, 0.0, 1.0, Double.MIN_VALUE};
			gbl_buttonPane.rowWeights = new double[]{0.0, Double.MIN_VALUE};
			buttonPane.setLayout(gbl_buttonPane);
			{
				JButton backBtn = new JButton("Back");
				GridBagConstraints gbc_backBtn = new GridBagConstraints();
				gbc_backBtn.anchor = GridBagConstraints.EAST;
				gbc_backBtn.insets = new Insets(0, 0, 0, 5);
				gbc_backBtn.gridx = 1;
				gbc_backBtn.gridy = 0;
				buttonPane.add(backBtn, gbc_backBtn);
				backBtn.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
			}
			{
				JButton saveBtn = new JButton("Save");
				GridBagConstraints gbc_saveBtn = new GridBagConstraints();
				gbc_saveBtn.anchor = GridBagConstraints.WEST;
				gbc_saveBtn.insets = new Insets(0, 0, 0, 5);
				gbc_saveBtn.gridx = 3;
				gbc_saveBtn.gridy = 0;
				buttonPane.add(saveBtn, gbc_saveBtn);
				saveBtn.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if(type == OrderPageType.SALE) {
							saveSale();
						}
					}
				});
			}
		}
			
			
		if(type == OrderPageType.SALE) {
			{
				JLabel shippingDateLbl = new JLabel("Shipping date");
				GridBagConstraints gbc_shippingDateLbl = new GridBagConstraints();
				gbc_shippingDateLbl.anchor = GridBagConstraints.WEST;
				gbc_shippingDateLbl.insets = new Insets(0, 0, 5, 5);
				gbc_shippingDateLbl.gridx = 1;
				gbc_shippingDateLbl.gridy = 7;
				contentPanel.add(shippingDateLbl, gbc_shippingDateLbl);
			}
			{
				shippingDateModel = new UtilDateModel();
				Properties p = new Properties();
				p.put("text.today", "Today");
				p.put("text.month", "Month");
				p.put("text.year", "Year");
				JDatePanelImpl shippingDatePanel = new JDatePanelImpl(shippingDateModel, p);
				JDatePickerImpl shippingDatePicker = new JDatePickerImpl(shippingDatePanel, new CalendarFormater());
				
				GridBagConstraints gbc_shippingDatePicker = new GridBagConstraints();
				gbc_shippingDatePicker.insets = new Insets(0, 0, 5, 5);
				gbc_shippingDatePicker.fill = GridBagConstraints.HORIZONTAL;
				gbc_shippingDatePicker.gridx = 2;
				gbc_shippingDatePicker.gridy = 7;
				contentPanel.add(shippingDatePicker, gbc_shippingDatePicker);
			}
			{
				JLabel deliveryDateLbl = new JLabel("Delivery date");
				GridBagConstraints gbc_deliveryDateLbl = new GridBagConstraints();
				gbc_deliveryDateLbl.anchor = GridBagConstraints.WEST;
				gbc_deliveryDateLbl.insets = new Insets(0, 0, 5, 5);
				gbc_deliveryDateLbl.gridx = 1;
				gbc_deliveryDateLbl.gridy = 8;
				contentPanel.add(deliveryDateLbl, gbc_deliveryDateLbl);
			}
			{
				deliveryDateModel = new UtilDateModel();
				Properties p = new Properties();
				p.put("text.today", "Today");
				p.put("text.month", "Month");
				p.put("text.year", "Year");
				JDatePanelImpl deliveryDatePanel = new JDatePanelImpl(deliveryDateModel, p);
				JDatePickerImpl deliveryDatePicker = new JDatePickerImpl(deliveryDatePanel, new CalendarFormater());
				
				GridBagConstraints gbc_deliveryDatePicker = new GridBagConstraints();
				gbc_deliveryDatePicker.insets = new Insets(0, 0, 5, 5);
				gbc_deliveryDatePicker.fill = GridBagConstraints.HORIZONTAL;
				gbc_deliveryDatePicker.gridx = 2;
				gbc_deliveryDatePicker.gridy = 8;
				contentPanel.add(deliveryDatePicker, gbc_deliveryDatePicker);
			}
		}
		{
			msgLbl.setVerticalAlignment(SwingConstants.TOP);
			msgLbl.setHorizontalAlignment(SwingConstants.LEFT);
			GridBagConstraints gbc_msgLbl = new GridBagConstraints();
			gbc_msgLbl.anchor = GridBagConstraints.NORTHWEST;
			gbc_msgLbl.insets = new Insets(0, 0, 0, 5);
			gbc_msgLbl.gridx = 2;
			if(type == OrderPageType.SALE) {
				gbc_msgLbl.gridy = 9;
			}
			
			contentPanel.add(msgLbl, gbc_msgLbl);
		}
		
		getCurrentStock();
		
		try {
			loadData();
			
			stockProducts.stream().forEach(stockProduct -> stockProductsDefaultModel.addElement(String.valueOf(stockProduct.getProduct().getBarcode()) + " - " + stockProduct.getProduct().getName()));
			customers.stream().forEach(customer -> customersDefaultModel.addElement(String.valueOf(customer.getCvrNo()) + " - " + customer.getFirstName() + " " + customer.getLastName()));
		} catch (SQLException e) {
			e.printStackTrace();
			msgLbl.setText(MessagesEnum.DBERROR.text);
			msgLbl.setForeground(Color.RED);
		}
	}
	
	private void loadData () throws SQLException {	
		try {
			stockProducts = orderCtrl.getStockProducts(LoginContainer.getInstance().getCurrentUser().getWarehouse().getId());
			customers = orderCtrl.getCustomers();
		} catch (SQLException e) {
			throw e;
		}
	}
	
	private void getCurrentStock() {
		if(stockProductsComboBox.getSelectedItem() != null) {
			String stringCurrentStock = String.valueOf(stockProductsComboBox.getSelectedItem()).substring(0,12);
			selectedStockProduct = stockProducts.stream()
										 .filter(sp -> stringCurrentStock.equals(sp.getProduct().getBarcode()))
										 .findAny()
										 .orElse(null);
			onStockValue.setText(String.valueOf(selectedStockProduct.getAmount()));
		}
	}
	
	private Customer getCurrentCustomer() {
		if(customersComboBox.getSelectedItem() != null) {
			String stringCustomer = String.valueOf(customersComboBox.getSelectedItem()).substring(0,8);
			return customers.stream()
								.filter(c -> stringCustomer.equals(c.getCvrNo()))
								.findAny()
								.orElse(null);
		}
		return null;
	}
	
	private void saveSale() {
		try {
			
			order = new Sale(-1,
							  ParsingHelper.tryParseDouble(totalPriceValue.getText()),
							  noteField.getText(),
							  LocalDate.now(),
							  LoginContainer.getInstance().getCurrentUser().getWarehouse(),
							  orderLines,
							  null,
							  -1,
							  LocalDate.of(shippingDateModel.getYear(), shippingDateModel.getMonth(), shippingDateModel.getDay()),
							  LocalDate.of(deliveryDateModel.getYear(), deliveryDateModel.getMonth(), deliveryDateModel.getDay()),
							  getCurrentCustomer()
							);
			
			order = orderCtrl.createOrder(order);
			
			msgLbl.setText(MessagesEnum.SALECREATED.text);
			msgLbl.setForeground(Color.GREEN);
			
		} catch(SQLException e) {
			e.printStackTrace();
			msgLbl.setText(MessagesEnum.DBERROR.text);
			msgLbl.setForeground(Color.RED);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			msgLbl.setForeground(Color.RED);
			msgLbl.setText(MessagesEnum.PARSEERROR.text);
		} finally {
			msgOrderLineLbl.setText("");
		}
	}
}