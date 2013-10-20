package org.abs.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

public interface MyIC extends Connection{
	void myclose() throws SQLException;

}
