This project aims at full-dumping data from RDBMs to HDFS in parquet format in a fast & efficient way by utilizing Spark. Supported databases include: RDBMS (ORACLE,DB2,MYSQL,MSSQL SERVER) & NoSQL(MongoDB).

Tasks that need to be completed include:

1. Fetching data (in parallel) from RDBMs by using spark jdbc, and spark dataframe/RDD will be obtained.

2. All data in RDD needs to be converted to byte array, and make sure the full-dumped data should have same format(e.g. timestamp format) as that fetched by incremental sync

3. New schema and new dataframe will be generated.

4. Write new dataframe into HDFS in parquet format.

5. Before and after fulldumping data, the meta info(such as sofarwork, META_FLAG) in zookeeper should be updated.



Attention:

1.This jar should be launched by task-jar via submitting a spark application.


2.Besides, three zookeeper nodes should be provided, including:


/{ROOT NODE}/config/sparkJdbc.parallelismNum        (default value: 16)

/{ROOT NODE}/config/sparkJdbc.partitionNum          (default value: 64)

/{ROOT NODE}/config/sparkJdbc.fetchsize             (default value: 100000)




