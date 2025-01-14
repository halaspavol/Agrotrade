package Model.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import Model.IF.UnitIF;
import Model.Model.Category;
import Model.Model.MessagesEnum;
import Model.Model.Unit;

public class UnitDB implements UnitIF{
	
	@Override
	public Unit getUnit(long id) throws SQLException {
		Unit res = null;
		String sqlCategory = ("SELECT * FROM Unit WHERE id = ?");
		
		Connection con = DBConnection.getInstance().getConnection();

		try {
			PreparedStatement preparedStmt = con.prepareStatement(sqlCategory);
			
			preparedStmt.setLong(1, id);
				
			ResultSet rsUnit = preparedStmt.executeQuery();

			if(rsUnit.next()) {
				res = buildUnit(rsUnit);
			}

		} catch (SQLException e) {
			throw e;
		}
		
		return res;
	}

	@Override
	public ArrayList<Unit> getUnits() throws SQLException {
		ArrayList<Unit> units = new ArrayList<Unit>();
		String sqlUnit = ("SELECT * FROM Unit");
		
		Connection con = DBConnection.getInstance().getConnection();

    try {
			PreparedStatement preparedStmt = con.prepareStatement(sqlUnit);
			
			ResultSet rsUnit = preparedStmt.executeQuery();
			
			while (rsUnit.next()) {
				Unit res = buildUnit(rsUnit);
				units.add(res);
			}
		} catch (SQLException e) {
			throw e;
		}
		return units;
	}
	
	public Unit createUnit(Unit unit) throws SQLException {
		String sqlCreate = "INSERT INTO Unit (name) VALUES (?)";
		
	    Connection con = DBConnection.getInstance().getConnection();
	
	    try {
			PreparedStatement preparedStmt = con.prepareStatement(sqlCreate, Statement.RETURN_GENERATED_KEYS);
	
			preparedStmt.setString(1, unit.getName());
			preparedStmt.executeUpdate();
			
			ResultSet rs = preparedStmt.getGeneratedKeys();
            if (rs.next()) {
            	unit.setId(rs.getLong(1));
            }
            else {
            	throw new SQLException(MessagesEnum.DBSAVEERROR.text);
            }
            
		} catch (SQLException e) {
			throw e;
		}

		return unit;
	}
	
	public Unit buildUnit(ResultSet rsUnit) throws SQLException{
		return new Unit(rsUnit.getLong("id"), rsUnit.getString("name"));
	}

}
