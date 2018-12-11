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

package org.datatech.baikal.web.modules.pcf.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.data.Stat;
import org.datatech.baikal.common.exp.BizException;
import org.datatech.baikal.util.JsonUtil;
import org.datatech.baikal.util.Md5Utils;
import org.datatech.baikal.web.common.Config;
import org.datatech.baikal.web.common.Enums;
import org.datatech.baikal.web.core.ZkHandler;
import org.datatech.baikal.web.entity.User;
import org.datatech.baikal.web.modules.pcf.service.UserService;
import org.datatech.baikal.web.utils.DateUtil;
import org.datatech.baikal.web.utils.EhcacheUtils;
import org.datatech.baikal.web.utils.StringUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import net.sf.json.JSONObject;
import sun.misc.BASE64Decoder;

/**
 * <p>
 * Description
 * <p>
 */
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private ZkHandler handler;

    @Value("${datalake-ui.regex}")
    private String regex;

    @Value("${update.password.time.range}")
    private int updatePasswordTimeRange;

    public static boolean checkPassword(String regex, String userName, String password) throws IOException {

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);
        if (matcher.matches() && !password.contains(userName)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Pair<User, String> login(User user) throws Exception {
        String failNumberCacheKey = user.getUsername() + "AAAA";
        String lastErrorTimeKey = user.getUsername() + "BBBB";
        String lockFlagKey = user.getUsername() + "CCCC";
        String lockTimeKey = user.getUsername() + "DDDD";
        String resultCode = "200";
        BizException.throwWhenFalse(!StringUtil.isNull(user), "参数传入错误");
        BizException.throwWhenFalse(!StringUtil.isNull(user.getUsername()), "账号为空，请输入账号");
        BizException.throwWhenFalse(!StringUtil.isNull(user.getPassword()), "密码为空，请输入账号");
        String rowkey = user.getUsername();
        CuratorFramework client = handler.getClient();
        String path = Config.ZK_NODE_USER + "/" + rowkey;
        Stat check = client.checkExists().forPath(path);
        if (null == check) {
            BizException.throwWhenFalse(false, "账号不存在");
        }
        String content = new String(client.getData().forPath(path));
        JSONObject jsonObject = JSONObject.fromObject(content);
        String passwd = (String) jsonObject.get("PASSWORD");
        String tenantName = (String) jsonObject.get("TENANT_NAME");
        String secPassword;
        String loginPassword = Md5Utils.hash(Md5Utils.hash(user.getPassword()));
        String character = "taskClient";
        if (character.equals(user.getFromClient())) {
            secPassword = user.getPassword();
            BizException.throwWhenFalse(secPassword.equals(Md5Utils.hash(Md5Utils.hash("taskClient" + passwd))),
                    "账号或密码错误");
        } else {
            if (passwd.equals(loginPassword)) {
                long nowTime = System.currentTimeMillis();
                long lastMotifyPasswordTime = (long) jsonObject.get("LAST_MOTIFY_TIME");
                long days = DateUtil.getDifferentDaysByMillisecond(nowTime, lastMotifyPasswordTime);
                long restDays = updatePasswordTimeRange - days;
                if (updatePasswordTimeRange - Config.REMINDER_TIME <= days && days <= updatePasswordTimeRange) {
                    int errorNumber = EhcacheUtils.getFailureNumberCache(failNumberCacheKey);
                    if ((errorNumber < Config.FAILURE_NUMBER)
                            || StringUtil.isNull(EhcacheUtils.getLockTime(lockTimeKey))) {
                        EhcacheUtils.putFailureNumberCache(failNumberCacheKey, 0);
                        EhcacheUtils.putLockFlag(lockFlagKey, false);
                        resultCode = Enums.ResultCode.RESULTCODE_REMINDER_MESSAGE.value() + "," + restDays;
                    } else {
                        int lockTimeCacheTimeToLive = EhcacheUtils.getLockTimeCacheTimeToLive(lockTimeKey) / 60;
                        BizException.throwWhenFalse(false, "24小时内连续输错5次密码,账号已锁定," + lockTimeCacheTimeToLive + "分钟后解锁");
                    }

                } else if (days > updatePasswordTimeRange) {
                    resultCode = Enums.ResultCode.RESULTCODE_RESETPASSWORD.value();
                } else {
                    if (StringUtil.isNull(EhcacheUtils.getFailureNumberCache(failNumberCacheKey))) {
                        EhcacheUtils.putFailureNumberCache(failNumberCacheKey, 0);
                    }
                    if (StringUtil.isNull(EhcacheUtils.getLockFlag(lockFlagKey))) {
                        EhcacheUtils.putLockFlag(lockFlagKey, false);
                    }
                    int failureNumber = EhcacheUtils.getFailureNumberCache(failNumberCacheKey);
                    if ((failureNumber < Config.FAILURE_NUMBER)
                            || StringUtil.isNull(EhcacheUtils.getLockTime(lockTimeKey))) {
                        secPassword = Md5Utils.hash(Md5Utils.hash(user.getPassword()));
                        BizException.throwWhenFalse(secPassword.equals(passwd), "账号或密码错误");
                        EhcacheUtils.putFailureNumberCache(failNumberCacheKey, 0);
                        EhcacheUtils.putLockFlag(lockFlagKey, false);
                    } else {
                        int lockTimeCacheTimeToLive = EhcacheUtils.getLockTimeCacheTimeToLive(lockTimeKey) / 60;
                        BizException.throwWhenFalse(false, "24小时内连续输错5次密码,账号已锁定," + lockTimeCacheTimeToLive + "分钟后解锁");
                    }
                }
            } else {
                if (StringUtil.isNull(EhcacheUtils.getFailureNumberCache(failNumberCacheKey))) {
                    EhcacheUtils.putFailureNumberCache(failNumberCacheKey, 0);
                    EhcacheUtils.putLockFlag(lockFlagKey, false);
                }
                if ((int) EhcacheUtils.getFailureNumberCache(failNumberCacheKey) < Config.FAILURE_NUMBER) {
                    long nowTime = System.currentTimeMillis();
                    EhcacheUtils.putFailureNumberCache(failNumberCacheKey,
                            (int) EhcacheUtils.getFailureNumberCache(failNumberCacheKey) + 1);
                    EhcacheUtils.putLastLoginFailureTime(lastErrorTimeKey, nowTime);
                    resultCode = Enums.ResultCode.RESULTCODE_PASSWORD_ERROR.value();
                    int errorNumber = 5;
                    if ((int) EhcacheUtils.getFailureNumberCache(failNumberCacheKey) == errorNumber) {
                        EhcacheUtils.putLockTime(lockTimeKey, 1800);
                        int lockTimeCacheTimeToLive = EhcacheUtils.getLockTimeCacheTimeToLive(lockTimeKey) / 60;
                        BizException.throwWhenFalse(false, "24小时内连续输错5次密码,账号已锁定," + lockTimeCacheTimeToLive + "分钟后解锁");
                    }
                } else {
                    EhcacheUtils.putFailureNumberCache(failNumberCacheKey, 5);
                    EhcacheUtils.putLockFlag(lockFlagKey, true);
                    resultCode = Enums.ResultCode.RESULTCODE_DISABLED.value();
                    int lockTimeCacheTimeToLive = EhcacheUtils.getLockTimeCacheTimeToLive(lockTimeKey) / 60;
                    BizException.throwWhenFalse(false, "24小时内连续输错5次密码,账号已锁定," + lockTimeCacheTimeToLive + "分钟后解锁");
                }
                BizException.throwWhenFalse(jsonObject.size() > 0, "账号或密码错误");
            }
        }

        return Pair.of(new User(rowkey, tenantName == null ? null : tenantName), resultCode);
    }

    @Override
    public Boolean updatePassword(User user, String oldPassword, String newPassword) throws Exception {
        String rowkey = user.getUsername();
        CuratorFramework client = handler.getClient();
        String path = Config.ZK_NODE_USER + "/" + rowkey;
        Stat check = client.checkExists().forPath(path);
        String userName = user.getUsername();
        BASE64Decoder decoder = new BASE64Decoder();
        String entryOldPassword = new String(decoder.decodeBuffer(oldPassword));
        String combine = userName + entryOldPassword;
        String zkNewPassword = new String(decoder.decodeBuffer(newPassword));
        String newCombine = userName + zkNewPassword;

        if (null == check) {
            BizException.throwWhenFalse(false, "用户名不存在,请输入正确的用户名");
        }
        String content = new String(client.getData().forPath(path));
        JSONObject json = JSONObject.fromObject(content);
        String passwd = (String) json.get("PASSWORD");
        String tenantName = (String) json.get("TENANT_NAME");
        long addUserTime = System.currentTimeMillis();
        String userInfo1 = new String(handler.getClient().getData().forPath(path));
        String[] passwords = JsonUtil
                .getStringArray4Json(JSONObject.fromObject(userInfo1).get("UPDATE_PASSWORD").toString());
        ArrayList<String> passwordGroup = new ArrayList<>(Arrays.asList(passwords));
        String updatePassword = Md5Utils.hash(Md5Utils.hash(Md5Utils.hash(newCombine)));
        String enctryPassword = Md5Utils.hash(Md5Utils.hash(Md5Utils.hash(combine)));
        if (enctryPassword.equals(passwd)) {
            if (checkPassword(regex, userName, zkNewPassword)) {
                if (!passwordGroup.contains(updatePassword)) {
                    if (passwordGroup.size() < Config.PASSWORD_GROUP_SIZE) {
                        user.setLastMotifyPasswordTime(addUserTime);
                        passwordGroup.add(updatePassword);
                        json.element("PASSWORD", updatePassword);
                        json.element("TENANT_NAME", tenantName);
                        json.element("UPDATE_PASSWORD", passwordGroup);
                        json.element("LAST_MOTIFY_TIME", addUserTime);
                        client.setData().forPath(path, json.toString().getBytes());

                    } else {
                        user.setLastMotifyPasswordTime(addUserTime);
                        json.element("PASSWORD", updatePassword);
                        json.element("TENANT_NAME", tenantName);
                        passwordGroup.remove(0);
                        passwordGroup.add(updatePassword);
                        json.element("UPDATE_PASSWORD", passwordGroup);
                        json.element("LAST_MOTIFY_TIME", addUserTime);
                        client.setData().forPath(path, json.toString().getBytes());

                    }
                } else {
                    BizException.throwWhenFalse(false, "设置的新密码需要和前六次不同");
                }
            } else {
                BizException.throwWhenFalse(false, "请输入正确的新密码,密码必须包含大小写字母，数字,长度至少10位且不包含用户名");
            }
        } else {
            BizException.throwWhenFalse(false, "请输入正确的原密码");
        }
        return true;
    }
}
