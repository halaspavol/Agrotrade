package Model.DBIF;

import java.sql.SQLException;

public interface CountryIF {
	long createCountry(String name) throws SQLException;
}
