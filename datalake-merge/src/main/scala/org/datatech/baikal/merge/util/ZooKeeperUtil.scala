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

package org.datatech.baikal.merge.util

import java.util
import java.util.StringTokenizer

import org.apache.zookeeper.data.Stat
import org.apache.zookeeper.{CreateMode, ZooDefs, ZooKeeper}
import org.datatech.baikal.merge.model.DatalakeHDFSNode
import org.slf4j.{Logger, LoggerFactory}

import scala.util.control.Breaks._

object ZooKeeperUtil {
  val log: Logger = LoggerFactory.getLogger("ZooKeeperUtil")

  def createSeqNode(client: ZooKeeper, path: String, uuid: String): String = {
    createPersistentIfNotExists(client, path)
    client.create(path.concat("/seq-"), uuid.getBytes("UTF-8"), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL)
  }

  /**
    * @param client ZooKeeper client
    * @param path   zk path
    * @return false if has no seq path or lock create-time > 1h
    */
  def checkLockNode(client: ZooKeeper, path: String): Boolean = {
    createPersistentIfNotExists(client, path)
    val children = client.getChildren(path, false, null)
    if (children.isEmpty)
      return false
    val childPath = path.concat("/").concat(children.get(0))
    var stat = new Stat
    client.getData(childPath, false, stat)
    println("lock node ctime:" + stat.getCtime)
    println("now:" + System.currentTimeMillis)
    if (System.currentTimeMillis - stat.getCtime > 1000 * 60 * 60) {
      log.info(">> lock is expired. remove it. <<")
      removeExpiredSeqNode(client, childPath)
      return false
    }
    true
  }

  private def removeExpiredSeqNode(client: ZooKeeper, seqPath: String): Unit = {
    client.delete(seqPath, 0)
  }

  def createPersistentIfNotExists(zooKeeper: ZooKeeper, zkPath: String): Unit = {
    createParentIfNotExists(zooKeeper, zkPath)
    if (zooKeeper.exists(zkPath, false) == null)
      zooKeeper.create(zkPath, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT)
  }

  def createParentIfNotExists(zooKeeper: ZooKeeper, zkPath: String): Unit = {
    val elements = zkPath.split("/")
    var str = ""
    for (i <- 0 until elements.length - 1) {
      val element = elements(i)
      if (!element.equals("")) {
        str = str.concat("/").concat(element)
        if (zooKeeper.exists(str, false) == null)
          zooKeeper.create(str, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT)
      }
    }
  }

  def removeSeqNode(client: ZooKeeper, node: DatalakeHDFSNode, uuid: String): Unit = {
    val path = node.zkLockPath
    createParentIfNotExists(client, path)
    val seqChildren = getChildren(client, path)
    breakable {
      for (i <- 0 until seqChildren.size()) {
        val child = seqChildren.get(i)
        val childPath = path + "/" + child
        val data = getNodeData(client, childPath)
        if (data.equals(uuid)) {
          client.delete(childPath, 0)
          break()
        }
      }
    }
  }

  def updateLastSuccessNode(client: ZooKeeper, node: DatalakeHDFSNode): Unit = {
    val path = node.zkLastMergePath
    createPersistentIfNotExists(client, path)
    client.setData(path, String.valueOf(System.currentTimeMillis()).getBytes(), -1)
  }

  def getNodeData(client: ZooKeeper, path: String): String = {
    createPersistentIfNotExists(client, path)
    new String(client.getData(path, false, null), "UTF-8")
  }

  def getChildren(client: ZooKeeper, path: String): util.List[String] = {
    createPersistentIfNotExists(client, path)
    client.getChildren(path, false)
  }

  /**
    * hdfsPath format:/datalake/{tenant}/{instance}/{schema}/{table}<br>
    * zkPath format:/datalake/merge/{tenant}/{instance}/{schema}/{table}<br>
    *
    * @param hdfsPath hdfsPath
    * @return
    */
  def convertDatalakeHDFSPathToZKLockPath(hdfsPath: String): String = {
    var realPath = hdfsPath
    if (hdfsPath.startsWith("hdfs://")) {
      realPath = realPath.substring(7)
      val firstCh = realPath.indexOf('/')
      realPath = realPath.substring(firstCh)
    }
    realPath.replaceFirst("datalake", "datalake/merge")
  }

  def convertDatalakeHDFSPathToZKLastMergePath(hdfsPath: String): String = {
    var realPath = hdfsPath
    if (hdfsPath.startsWith("hdfs://")) {
      realPath = realPath.substring(7)
      val firstCh = realPath.indexOf('/')
      realPath = realPath.substring(firstCh)
    }
    realPath.replaceFirst("datalake", "datalake/last_successful_merge")
  }

  /**
    * hdfsPath format:/datalake/{tenant}/{instance}/{schema}/{table}<br>
    * zkPath format:/datalake/metastore/{tenant}/meta/{instance}/{schema}/{table}<br>
    *
    * @param hdfsPath hdfsPath
    * @return
    */
  def convertDatalakeHDFSPathToZKMetaPath(hdfsPath: String): String = {
    var realPath = hdfsPath
    if (hdfsPath.startsWith("hdfs://")) {
      realPath = realPath.substring(7)
      val firstCh = realPath.indexOf('/')
      realPath = realPath.substring(firstCh)
    }
    val temp = realPath.replaceFirst("datalake", "datalake/metastore")
    val stringTokenizer = new StringTokenizer(temp, "/", true)

    val stringBuffer = new StringBuffer()
    var i = 0
    while (stringTokenizer.hasMoreElements) {
      stringBuffer.append(stringTokenizer.nextElement())
      if (i == 6)
        stringBuffer.append("meta/")
      i = i + 1
    }
    stringBuffer.toString
  }
}
