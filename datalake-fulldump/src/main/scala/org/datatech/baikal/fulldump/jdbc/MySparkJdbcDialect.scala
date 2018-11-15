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

package org.datatech.baikal.fulldump.jdbc

import java.sql.Types

import org.apache.spark.sql.jdbc.{JdbcDialect, JdbcDialects, JdbcType}
import org.apache.spark.sql.types._
import org.slf4j.LoggerFactory

class MySparkJdbcDialect {

}

object MySparkJdbcDialect {


  /**
    * This class is used for unregistering default jdbcDialect and registering self-defined jdbcDialect
    * 1. It support self-defined conversions from database sql type to java data type
    * 2. It can be used to solving unsupported data type problem in fetching data from database
    **/

  def useMyJdbcDIalect(jdbcUrl: String, dbType: String): Unit = {

    val logger = LoggerFactory.getLogger(classOf[MySparkJdbcDialect])

    val dialect = JdbcDialects
    JdbcDialects.unregisterDialect(dialect.get(jdbcUrl))

    if (dbType == "MSSQL") {
      val MSSqlDialect = new JdbcDialect {
        override def canHandle(url: String): Boolean = url.startsWith("jdbc:sqlserver") || url.contains("sqlserver")

        override def getCatalystType(sqlType: Int, typeName: String, size: Int, md: MetadataBuilder): Option[DataType] = {

          if (sqlType == Types.TIMESTAMP || sqlType == -155 || sqlType == Types.TIME) {
            Some(StringType)
          }
          else {
            Some(StringType)
          }
        }

        override def getJDBCType(dt: DataType): Option[JdbcType] = dt match {
          case StringType => Some(JdbcType("VARCHAR2(2000)", java.sql.Types.VARCHAR))
          case BooleanType => Some(JdbcType("NUMBER(1)", java.sql.Types.NUMERIC))
          case IntegerType => Some(JdbcType("NUMBER(10)", java.sql.Types.NUMERIC))
          case LongType => Some(JdbcType("NUMBER(19)", java.sql.Types.NUMERIC))
          case DoubleType => Some(JdbcType("NUMBER(19,4)", java.sql.Types.NUMERIC))
          case FloatType => Some(JdbcType("NUMBER(19,4)", java.sql.Types.NUMERIC))
          case ShortType => Some(JdbcType("NUMBER(5)", java.sql.Types.NUMERIC))
          case ByteType => Some(JdbcType("NUMBER(3)", java.sql.Types.NUMERIC))
          case BinaryType => Some(JdbcType("BLOB", java.sql.Types.BLOB))
          case TimestampType => Some(JdbcType("DATE", java.sql.Types.TIMESTAMP))
          case DateType => Some(JdbcType("DATE", java.sql.Types.DATE))
          case _ => None
        }

        //Imp from Spark2.0 since otherwise oracle table columns would be case-sensitive
        override def quoteIdentifier(colName: String): String = {
          colName
        }
      }
      JdbcDialects.registerDialect(MSSqlDialect)
    }

    else {
      if (dbType == "ORACLE") {
        val OracleDialect = new JdbcDialect {
          override def canHandle(url: String): Boolean = url.startsWith("jdbc:oracle") || url.contains("oracle")

          override def getCatalystType(sqlType: Int, typeName: String, size: Int, md: MetadataBuilder): Option[DataType] = {

            if (sqlType == Types.TIMESTAMP || sqlType == -101 || sqlType == -102) {
              logger.debug(">>>>>> JdbcDialect sqlType is Timestamp || -101 || -102.")
              Some(TimestampType)
            }
            else if (sqlType == Types.BLOB) {
              Some(BinaryType)
            }
            else {
              Some(StringType)
            }
          }

          override def getJDBCType(dt: DataType): Option[JdbcType] = dt match {
            case StringType => Some(JdbcType("VARCHAR2(2000)", java.sql.Types.VARCHAR))
            case BooleanType => Some(JdbcType("NUMBER(1)", java.sql.Types.NUMERIC))
            case IntegerType => Some(JdbcType("NUMBER(10)", java.sql.Types.NUMERIC))
            case LongType => Some(JdbcType("NUMBER(19)", java.sql.Types.NUMERIC))
            case DoubleType => Some(JdbcType("NUMBER(19,4)", java.sql.Types.NUMERIC))
            case FloatType => Some(JdbcType("NUMBER(19,4)", java.sql.Types.NUMERIC))
            case ShortType => Some(JdbcType("NUMBER(5)", java.sql.Types.NUMERIC))
            case ByteType => Some(JdbcType("NUMBER(3)", java.sql.Types.NUMERIC))
            case BinaryType => Some(JdbcType("BLOB", java.sql.Types.BLOB))
            case TimestampType => Some(JdbcType("DATE", java.sql.Types.TIMESTAMP))
            case DateType => Some(JdbcType("DATE", java.sql.Types.DATE))
            case _ => None
          }

          //Imp from Spark2.0 since otherwise oracle table columns would be case-sensitive
          override def quoteIdentifier(colName: String): String = {
            colName
          }

        }
        JdbcDialects.registerDialect(OracleDialect)

      }

      else {
        if (dbType == "MYSQL") {
          val MysqlDialect = new JdbcDialect {
            override def canHandle(url: String): Boolean = url.startsWith("jdbc:mysql") || url.contains("mysql")

            override def getCatalystType(sqlType: Int, typeName: String, size: Int, md: MetadataBuilder): Option[DataType] = {
              // Handle NUMBER fields that have no precision/scale in special way because JDBC ResultSetMetaData converts this to 0 procision and -127 scale

              if (sqlType == Types.TIMESTAMP || sqlType == Types.TIME) {
                Some(TimestampType)
              }
              else {
                Some(StringType)
              }
            }

            override def getJDBCType(dt: DataType): Option[JdbcType] = dt match {
              case StringType => Some(JdbcType("VARCHAR2(2000)", java.sql.Types.VARCHAR))
              case BooleanType => Some(JdbcType("NUMBER(1)", java.sql.Types.NUMERIC))
              case IntegerType => Some(JdbcType("NUMBER(10)", java.sql.Types.NUMERIC))
              case LongType => Some(JdbcType("NUMBER(19)", java.sql.Types.NUMERIC))
              case DoubleType => Some(JdbcType("NUMBER(19,4)", java.sql.Types.NUMERIC))
              case FloatType => Some(JdbcType("NUMBER(19,4)", java.sql.Types.NUMERIC))
              case ShortType => Some(JdbcType("NUMBER(5)", java.sql.Types.NUMERIC))
              case ByteType => Some(JdbcType("NUMBER(3)", java.sql.Types.NUMERIC))
              case BinaryType => Some(JdbcType("BLOB", java.sql.Types.BLOB))
              case TimestampType => Some(JdbcType("DATE", java.sql.Types.TIMESTAMP))
              case DateType => Some(JdbcType("DATE", java.sql.Types.DATE))
              case _ => None
            }

            //Imp from Spark2.0 since otherwise oracle table columns would be case-sensitive
            override def quoteIdentifier(colName: String): String = {
              colName
            }
          }
          JdbcDialects.registerDialect(MysqlDialect)
        }

        else if (dbType == "DB2") {
          val Db2Dialect = new JdbcDialect {
            override def canHandle(url: String): Boolean = url.startsWith("jdbc:db2") || url.contains("db2")

            override def getCatalystType(sqlType: Int, typeName: String, size: Int, md: MetadataBuilder): Option[DataType] = {
              // Handle NUMBER fields that have no precision/scale in special way because JDBC ResultSetMetaData converts this to 0 procision and -127 scale

              if (sqlType == Types.TIMESTAMP || sqlType == Types.TIME || sqlType == Types.DATE) {
                Some(TimestampType)
              }
              else {
                Some(StringType)
              }
            }

            override def getJDBCType(dt: DataType): Option[JdbcType] = dt match {
              case StringType => Some(JdbcType("VARCHAR2(2000)", java.sql.Types.VARCHAR))
              case BooleanType => Some(JdbcType("NUMBER(1)", java.sql.Types.NUMERIC))
              case IntegerType => Some(JdbcType("NUMBER(10)", java.sql.Types.NUMERIC))
              case LongType => Some(JdbcType("NUMBER(19)", java.sql.Types.NUMERIC))
              case DoubleType => Some(JdbcType("NUMBER(19,4)", java.sql.Types.NUMERIC))
              case FloatType => Some(JdbcType("NUMBER(19,4)", java.sql.Types.NUMERIC))
              case ShortType => Some(JdbcType("NUMBER(5)", java.sql.Types.NUMERIC))
              case ByteType => Some(JdbcType("NUMBER(3)", java.sql.Types.NUMERIC))
              case BinaryType => Some(JdbcType("BLOB", java.sql.Types.BLOB))
              case TimestampType => Some(JdbcType("DATE", java.sql.Types.TIMESTAMP))
              case DateType => Some(JdbcType("DATE", java.sql.Types.DATE))
              case _ => None
            }

            //Imp from Spark2.0 since otherwise oracle table columns would be case-sensitive
            override def quoteIdentifier(colName: String): String = {
              colName
            }
          }
          JdbcDialects.registerDialect(Db2Dialect)
        }
      }
    }

  }


}
