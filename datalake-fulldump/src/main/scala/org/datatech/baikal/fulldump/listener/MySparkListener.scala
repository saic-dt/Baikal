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

package org.datatech.baikal.fulldump.listener

import org.apache.spark.scheduler._
import org.datatech.baikal.fulldump.meta.MetaConfigDao
import org.slf4j.LoggerFactory

/**
  * This class is used to listen the progress of submitted spark job
  * The number of completed tasks will be counted
  * In this way, the progress of submitted spark job can be estimated
  **/

class MySparkListener(metaConfigDao: MetaConfigDao, instanceName: String, schemaName: String, tableName: String, rowCount: Long, parallelismNum: Int, partitionNum: Int, pkExist: Boolean) extends SparkListener {

  val logger = LoggerFactory.getLogger(classOf[MySparkListener])
  var taskCount: Int = 0;

  override def onApplicationStart(applicationStart: SparkListenerApplicationStart): Unit = {
    super.onApplicationStart(applicationStart)
    logger.info("\n\n\n>>>>>> Spark application started")

  }

  override def onApplicationEnd(applicationEnd: SparkListenerApplicationEnd): Unit = {
    super.onApplicationEnd(applicationEnd)
    logger.info("\n\n\n>>>>>> Spark application ended")

  }


  override def onJobEnd(jobEnd: SparkListenerJobEnd): Unit = {
    super.onJobEnd(jobEnd)

  }


  override def onStageCompleted(stageCompleted: SparkListenerStageCompleted): Unit = {
    super.onStageCompleted(stageCompleted)

  }


  override def onTaskEnd(taskEnd: SparkListenerTaskEnd): Unit = {
    super.onTaskEnd(taskEnd)
    taskCount = taskCount + 1


    // if pk exists
    if (pkExist) {
      if (taskCount <= parallelismNum + 1) {
        val sparkJobProgress = Math.floor(rowCount * (0.1 + taskCount * 0.76 / (parallelismNum + 1))).toLong
        if (sparkJobProgress <= rowCount) {
          metaConfigDao.updateSoFar(instanceName, schemaName, tableName, sparkJobProgress)
        }
      }
      else if (taskCount <= parallelismNum + 1 + partitionNum) {
        val sparkJobProgress = Math.floor(rowCount * (0.86 + (taskCount - parallelismNum - 1) * 0.1 / partitionNum)).toLong
        if (sparkJobProgress <= rowCount) {
          metaConfigDao.updateSoFar(instanceName, schemaName, tableName, sparkJobProgress)
        }

      }

    }


    //if pk does not exists
    if (!pkExist) {
      if (taskCount <= 1) {
        val sparkJobProgress = Math.floor(0.86 * rowCount).toLong
        if (sparkJobProgress <= rowCount) {
          metaConfigDao.updateSoFar(instanceName, schemaName, tableName, sparkJobProgress)
        }
      }
      else if (taskCount <= 1 + partitionNum) {
        val sparkJobProgress = Math.floor(rowCount * (0.86 + (taskCount - 1) * 0.1 / partitionNum)).toLong
        if (sparkJobProgress <= rowCount) {
          metaConfigDao.updateSoFar(instanceName, schemaName, tableName, sparkJobProgress)
        }

      }
    }


  }


}
