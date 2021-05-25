package Model.DBIF;

import java.sql.SQLException;
import java.util.ArrayList;

import Model.Model.Order;
import Model.Model.OrderPageType;

public interface OrderIF {
	public Order createOrder(Order order) throws SQLException;
	
	public Order getOrder(long id, OrderPageType type) throws SQLException;
	
	public ArrayList<Order> getOrderList () throws SQLException;
}
