package Model.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import Model.DBIF.UnitIF;
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
	
	public Unit buildUnit(ResultSet rsUnit) throws SQLException{
		return new Unit(rsUnit.getLong("id"), rsUnit.getString("name"));
	}

}
