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

package org.datatech.baikal.fulldump.utils

import org.apache.zookeeper.{CreateMode, ZooDefs, ZooKeeper}

class CreateNotify_freshNode {

}

object CreateNotify_freshNode {

  def createSeqNode(client: ZooKeeper, path: String, isMongo: Boolean, mongoValue: String): String = {
    createPersistentIfNotExists(client, path)
    if (isMongo) {
      println(">>>>>> create mongo_notify_refresh node in: " + path)
      client.create(path.concat("/seq-"), mongoValue.getBytes("UTF-8"), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL)
    }
    else {
      client.create(path.concat("/seq-"), "".getBytes("UTF-8"), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL)
    }
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


}
