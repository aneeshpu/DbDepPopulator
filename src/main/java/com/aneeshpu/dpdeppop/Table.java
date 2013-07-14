package com.aneeshpu.dpdeppop;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Table {
    public static final String PRIMARY_KEY_TABLE_NAME = "pktable_name";
    public static final String PRIMARY_KEY_COLUMN_NAME = "pkcolumn_name";
    private final String name;
    private final Connection connection;

    public static final Logger LOG = Logger.getLogger(Table.class);
    private List<Table> parentTables = new ArrayList<Table>();

    public Table(String name, final Connection connection) {
        this.name = name;
        this.connection = connection;
    }

    public Table initialize() throws SQLException {

        try {
            final ResultSet importedKeysResultSet = connection.getMetaData().getImportedKeys(null, null, name);

            while (importedKeysResultSet.next()) {
                final String primaryKeyTableName = importedKeysResultSet.getString(PRIMARY_KEY_TABLE_NAME);
                parentTables.add(new Table(primaryKeyTableName, connection).initialize());
            }

            return this;


        } catch (SQLException e) {
            LOG.error(e);

            throw e;
        }
    }

    public List<Table> parents() {
        return parentTables;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Table table = (Table) o;

        if (!name.equals(table.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }
}
