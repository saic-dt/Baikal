/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.datatech.baikal.task.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.datatech.baikal.task.common.DbType;
import org.datatech.baikal.task.common.SourceJdbc;
import org.datatech.baikal.task.dao.SourceJdbcDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Helper class providing methods to work with JDBC data source.
 */
@Service
public class DbHelper {
    private static final Logger logger = LoggerFactory.getLogger(DbHelper.class);

    @Autowired
    private SourceJdbcDao sourceJdbcDao;

    /**
     * Previous method the get oracle table schema.
     *
     * @param instanceName instanceName
     * @param owner        owner
     * @param tableName    tableName
     * @return a pair tuple contains a list of hive column type and a list of column name
     * @throws Exception Exception
     */
    public Pair<List<String>, List<String>> getOracleTableSchema(String instanceName, String owner, String tableName)
            throws Exception {
        Pair<Connection, DbType> p = getConnection(instanceName, owner);
        Connection connection = p.getKey();
        String sql = "SELECT COLUMN_NAME,DATA_TYPE,DATA_SCALE "
                + "FROM all_tab_cols WHERE OWNER = ? AND TABLE_NAME = ? " + "ORDER BY COLUMN_NAME";
        List<String> listCt = new ArrayList<String>();
        List<String> listC = new ArrayList<String>();
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, owner.toUpperCase());
        statement.setString(2, tableName.toUpperCase());
        ResultSet rs = statement.executeQuery();
        while (rs.next()) {
            String columnType = rs.getString(2);
            String columnName = rs.getString(1);
            listC.add(columnName);
            if (columnType.contains("NUMBER") && ((rs.getInt(3) == 0) || (rs.getString(3) == null))) {
                listCt.add(String.format("`%s` bigint", columnName));
            } else if (columnType.contains("NUMBER") && rs.getString(3) != null) {
                listCt.add(String.format("`%s` decimal(20,%d)", columnName, rs.getInt(3)));
            } else {
                listCt.add(String.format("`%s` string", columnName));
            }
        }
        p.getLeft().close();
        return Pair.of(listCt, listC);
    }

    /**
     * Get database table schema for hive table creation.
     *
     * @param instanceName instanceName
     * @param schemaName   schemaName
     * @param tableName    tableName
     * @return a pair tuple contains a list of hive column type and a list of column name
     * @throws Exception Exception
     */
    public Pair<List<String>, List<String>> getTableSchema(String instanceName, String schemaName, String tableName)
            throws Exception {
        Pair<Connection, DbType> p = getConnection(instanceName, schemaName);
        DbType dbType = p.getRight();
        Connection connection = p.getLeft();
        ArrayList<Integer> numTypeList = new ArrayList<Integer>();
        numTypeList.add(Types.TINYINT);
        numTypeList.add(Types.SMALLINT);
        numTypeList.add(Types.INTEGER);
        numTypeList.add(Types.BIGINT);
        numTypeList.add(Types.FLOAT);
        numTypeList.add(Types.REAL);
        numTypeList.add(Types.DOUBLE);
        numTypeList.add(Types.NUMERIC);
        numTypeList.add(Types.DECIMAL);

        Statement st = connection.createStatement();
        ResultSet res = st.executeQuery(String.format("SELECT * FROM %s.%s WHERE 0=1", schemaName, tableName));
        List<Pair<String, String>> columnList = new ArrayList<Pair<String, String>>();
        ResultSetMetaData metadata = res.getMetaData();
        int columnCount = metadata.getColumnCount();
        for (int i = 1; i <= columnCount; i++) {
            String columnName = metadata.getColumnName(i);
            int columnType = metadata.getColumnType(i);
            String columnTypeStr;
            try {
                columnTypeStr = JDBCType.valueOf(columnType).getName();
            } catch (IllegalArgumentException ex) {
                // not supported column type
                columnTypeStr = "__STRING__";
            }
            int precision = metadata.getPrecision(i);
            int scale = metadata.getScale(i);
            String hiveType = "string";
            if (numTypeList.contains(columnType)) {
                if (scale == 0) {
                    // previously the hive type is bigint, change to decimal for better precision
                    hiveType = "decimal(38,0)";
                } else if (scale < 0) {
                    // oracle float/number column type or any other column type that scale is not valid
                    hiveType = "decimal(38,16)";
                } else {
                    hiveType = String.format("decimal(38,%d)", scale);
                }

                switch (dbType) {
                case DB2:
                    if (scale == 0 && columnType == 8) {
                        hiveType = "decimal(38,31)";
                    }
                    break;
                case MSSQL:
                    if (scale == 0 && (columnType == 6 || columnType == 7 || columnType == 8)) {
                        hiveType = "decimal(38,31)";
                    }
                    break;
                default:
                    break;
                }
            }
            columnList.add((Pair<String, String>) Pair.of(columnName, hiveType));
            logger.info("column: {}, {}, {}, {}, {}", columnName, columnTypeStr, columnType, precision, scale);
        }
        List<String> listCt = new ArrayList<>();
        List<String> listC = new ArrayList<>();
        for (Pair<String, String> element : columnList) {
            listC.add(element.getLeft());
            listCt.add(String.format("`%s` %s", element.getLeft(), element.getRight()));
        }
        p.getLeft().close();
        return Pair.of(listCt, listC);
    }

    public List<Pair<String, String>> getColumnList(String instanceName, String schemaName, String tableName)
            throws Exception {
        Pair<Connection, DbType> p = getConnection(instanceName, schemaName);
        Connection connection = p.getLeft();

        ArrayList<Integer> numTypeList = new ArrayList<Integer>();
        numTypeList.add(Types.TINYINT);
        numTypeList.add(Types.SMALLINT);
        numTypeList.add(Types.INTEGER);
        numTypeList.add(Types.BIGINT);
        numTypeList.add(Types.FLOAT);
        numTypeList.add(Types.REAL);
        numTypeList.add(Types.DOUBLE);
        numTypeList.add(Types.NUMERIC);
        numTypeList.add(Types.DECIMAL);

        Statement st = connection.createStatement();
        ResultSet res = st.executeQuery(String.format("SELECT * FROM %s.%s WHERE 0=1", schemaName, tableName));
        List<Pair<String, String>> columnList = new ArrayList<Pair<String, String>>();
        ResultSetMetaData metadata = res.getMetaData();
        int columnCount = metadata.getColumnCount();

        for (int i = 1; i <= columnCount; i++) {
            String columnName = metadata.getColumnName(i);
            int columnType = metadata.getColumnType(i);
            String columnTypeStr;
            try {
                columnTypeStr = JDBCType.valueOf(columnType).getName();
            } catch (IllegalArgumentException ex) {
                // not supported column type
                columnTypeStr = "__STRING__";
            }
            int precision = metadata.getPrecision(i);
            int scale = metadata.getScale(i);
            String hiveType = "string";
            if (numTypeList.contains(columnType)) {
                if (columnType == Types.SMALLINT || columnType == Types.TINYINT) {
                    hiveType = "short";
                } else if (columnType == Types.INTEGER) {
                    hiveType = "int";
                } else if (columnType == Types.BIGINT) {
                    hiveType = "long";
                } else if (columnType == Types.FLOAT) {
                    hiveType = "float";
                } else {
                    hiveType = "double";
                }

                /*if (scale == 0) {
                  // previously the hive type is bigint, change to decimal for better precision
                  hiveType = "long";
                } else if (scale < 0) {
                  // oracle float/number column type or any other column type that scale is not valid
                  hiveType = "double";
                }*/
            }
            columnList.add((Pair<String, String>) Pair.of(columnName, hiveType));
            logger.info("column: {}, {}, {}, {}, {}", columnName, columnTypeStr, columnType, precision, scale);
        }
        return columnList;
    }

    /**
     * Get jdbc connection and database type.
     *
     * @param instanceName instanceName
     * @param owner        owner
     * @return a pair tuple contains the jdbc connection and database type
     * @throws Exception Exception
     */
    public Pair<Connection, DbType> getConnection(String instanceName, String owner) throws Exception {
        SourceJdbc sourceJdbc = sourceJdbcDao.get(instanceName, owner);
        DbType dbType = sourceJdbc.getDB_TYPE();
        String className = dbType.getClassName();
        String jdbcUrl = sourceJdbc.getJDBC_URL();
        String user = sourceJdbc.getUSER();
        String password = sourceJdbc.getPASSWORD();
        logger.info("the source jdbc driver [{}], url [{}], user [{}], password [{}]", className, jdbcUrl,
            user, password);
        Class.forName(className);
        return Pair.of(
                DriverManager.getConnection(jdbcUrl, user, password),
                dbType);
    }

}
