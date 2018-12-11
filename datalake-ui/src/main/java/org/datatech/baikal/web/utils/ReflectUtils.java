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

/**
 *
 */
package org.datatech.baikal.web.utils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.PropertyUtils;
import org.datatech.baikal.common.exp.SysException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Date: 13-2-23 下午1:25
 * <p>
 * Version: 1.0
 */
public class ReflectUtils {
    protected static Logger logger = LoggerFactory.getLogger(ReflectUtils.class.getName());

    /**
     * <pre>
     * 根据给出的名字, 获得对象指定的属性的值.
     * 并且转换为String类型.
     * 不通过getter和setter方法
     * 无视private
     * </pre>
     *
     * @param orig 指定对象
     * @param fieldName 对象指定的属性
     * @param p_bIgnoreCase 是否忽略大小写
     * @return 属性对应的值
     * @throws IllegalAccessException 异常
     */
    public static String getFieldByName(Object orig, String fieldName, boolean p_bIgnoreCase)
            throws IllegalAccessException {
        Field[] origFields = getDeclaredFieldsForClass(orig.getClass());
        String fieldNameToFind = fieldName;
        if (p_bIgnoreCase) {
            fieldNameToFind = fieldName.toUpperCase();
        }
        Object objValue = null;
        String name;
        for (int i = 0; i < origFields.length; i++) {
            Field origField = origFields[i];
            name = origField.getName();
            if (p_bIgnoreCase) {
                name = name.toUpperCase();
            }
            if (name.equals(fieldNameToFind)) {
                origField.setAccessible(true);
                objValue = origField.get(orig);
                origField.setAccessible(false);
                break;
            }
        }
        if (objValue != null) {
            return ConvertUtils.convert(objValue);
        } else {
            return null;
        }
    }

    /**
     * 设置指定对象的值
     * @param orig 源对象
     * @param fieldName 属性
     * @param value 设置的属性值
     * @param p_bIgnoreCase 是否忽略大小写
     * @throws IllegalAccessException 异常
     */
    public static void setFieldByName(Object orig, String fieldName, String value, boolean p_bIgnoreCase)
            throws IllegalAccessException {
        Field[] origFields = getDeclaredFieldsForClass(orig.getClass());
        String fieldNameToFind = fieldName;
        if (p_bIgnoreCase) {
            fieldNameToFind = fieldName.toUpperCase();
        }
        boolean found = false;
        String name;
        for (int i = 0; i < origFields.length; i++) {
            Field origField = origFields[i];
            name = origField.getName();
            if (p_bIgnoreCase) {
                name = name.toUpperCase();
            }
            if (name.equals(fieldNameToFind)) {
                origField.setAccessible(true);
                origField.set(orig, value);
                origField.setAccessible(false);
                found = true;
                break;
            }
        }
        if (!found) {
            throw new IllegalArgumentException("Field not found. fieldName ='" + fieldName + "'");
        }
    }

    /**
     * 复制对象,可控制是否复制空值属性
     *
     * @param objFrom
     *            Object 源对象
     * @param objTo
     *            Object 目标对象
     * @param bIncludeNull
     *            boolean 是否复制空值
     */
    public static void copyAllPropertiesByName(Object objFrom, Object objTo, boolean bIncludeNull) {
        try {
            if (bIncludeNull) {
                PropertyUtils.copyProperties(objTo, objFrom);

            } else {
                copyProperties(objTo, objFrom, bIncludeNull);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new SysException(e.getMessage());
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            throw new SysException(e.getMessage());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            throw new SysException(e.getMessage());
        }
    }

    /**
     * <pre>
     * 复制两个javabean的值，或者一个map到一个javabean的值
     *
     * 自动进行类型转换
     * 不通过getter和setter方法
     * 而且无视private
     * </pre>
     *
     * @param objFrom 源对象
     * @param objTo  目标对象
     * @throws IllegalAccessException IllegalAccessException异常
     * @throws NoSuchFieldException NoSuchFieldException异常
     * @throws InvocationTargetException InvocationTargetException异常
     */
    public static void copyFieldsByName(Object objFrom, Object objTo)
            throws IllegalAccessException, NoSuchFieldException, InvocationTargetException {
        copyFields(objTo, objFrom);
    }

    /**
     * 复制源对象指定属性的属性值到目标对象对应的属性中
     *
     * @param objFrom
     *            Object 源对象
     * @param objTo
     *            Object 目标对象
     * @param sFieldName
     *            String 属性名称
     * @throws IllegalAccessException IllegalAccessException异常
     * @throws NoSuchFieldException NoSuchFieldException异常
     * @throws NoSuchMethodException NoSuchMethodException异常
     * @throws InvocationTargetException InvocationTargetException异常
     */
    public static void copyPropertyByName(Object objFrom, Object objTo, String sFieldName)
            throws IllegalAccessException, NoSuchFieldException, NoSuchMethodException, InvocationTargetException {
        BeanUtils.copyProperty(objTo, sFieldName, BeanUtils.getProperty(objFrom, sFieldName));
    }

    /*
     * 获取除传入对象的的某一指定属性(包括"公有","保护"和"私有",不包括自继承父类的)
     * 
     * @param objToListFields Object 对象
     * 
     * @param sFieldName String 属性名称
     *
     * @return Field 属性对象
     *
     * @throws NoSuchFieldException
     *
     * private static Field getField(Object objToListFields, String sFieldName)
     * throws NoSuchFieldException { return
     * objToListFields.getClass().getDeclaredField(sFieldName); }
     */
    public static void dumpBean(Object bean) throws IllegalAccessException {
        Map map = ReflectUtils.instanceFieldsToMap(bean, false);
        Iterator itor = map.keySet().iterator();
        logger.debug(" * ##### Dump Bean ##### * ");
        while (itor.hasNext()) {
            String sKey = (String) itor.next();
            logger.debug(" * Key : " + sKey + " " + map.get(sKey) + " * ");
        }
    }

    private static Field findField(Class objClz, String sFieldName) {
        Field fieldToRet = null;
        Map<String, Field> fieldmap = getDeclaredFieldsForClassInMap(objClz);
        Set<String> fieldnames = fieldmap.keySet();
        for (String fieldName : fieldnames) {
            if (fieldName.equalsIgnoreCase(sFieldName)) {
                fieldToRet = fieldmap.get(fieldName);
                break;
            }
        }
        return fieldToRet;
    }

    /**
     * 获取对象某一属性的真实属性名 找不到的时候返回null
     *
     * @param objClz
     *            Object 对象的类
     * @param sFieldName
     *            String 属性名
     * @return String 真实的属性名
     */
    public static String findFieldName(Class objClz, String sFieldName) {
        String nameToRet = null;
        Field[] fields = objClz.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            String fieldName = field.getName();
            if (fieldName.equalsIgnoreCase(sFieldName)) {
                nameToRet = fieldName;
                break;
            }
        }
        return nameToRet;
    }

    /**
     * 获取对象某一属性的真实属性名 找不到返回null
     *
     * @param objClz
     *            Object 对象的类
     * @param sFieldName
     *            String 属性名
     * @return String 真实的属性名
     */
    public static String findPropertyName(Class objClz, String sFieldName) {
        // Field[] fields = objClz.getDeclaredFields();
        Field[] fields = getDeclaredFields(objClz);
        String sToRet = null;
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            String fieldName = field.getName();
            if (fieldName.equalsIgnoreCase(sFieldName)) {
                sToRet = fieldName;
                break;
            }
        }
        return sToRet;
    }

    public static Field[] getDeclaredFields(Class objClz) {
        ArrayList fields = new ArrayList();
        Class curClz = objClz;
        Collections.addAll(fields, curClz.getDeclaredFields());
        while (curClz.getSuperclass() != Object.class) {
            curClz = curClz.getSuperclass();
            Collections.addAll(fields, curClz.getDeclaredFields());
        }
        return (Field[]) fields.toArray(new Field[fields.size()]);
    }

    /**
     * 执行指定Class的无参数构造函数，返回相应的实例。
     *
     * @param clz 类
     * @return 指定Class的实例
     * @throws IllegalAccessException IllegalAccessException异常
     * @throws InvocationTargetException InvocationTargetException异常
     * @throws InstantiationException InstantiationException异常
     * @throws NoSuchMethodException NoSuchMethodException异常
     */
    public static Object getInstance(Class clz)
            throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException {
        Constructor c = clz.getDeclaredConstructor();
        c.setAccessible(true);
        return c.newInstance();
    }

    /**
     * 获取对象指定属性的值
     *
     * @param objFrom
     *            Object 对象
     * @param sFieldName
     *            String 属性名
     * @param bIgnoreCase
     *            boolean 是否忽略大小写
     * @return String fieldValue.属性值
     * @throws NoSuchMethodException NoSuchMethodException异常
     * @throws IllegalAccessException IllegalAccessException异常
     * @throws InvocationTargetException InvocationTargetException异常
     */
    public static String getPropertyByName(Object objFrom, String sFieldName, boolean bIgnoreCase)
            throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        if (bIgnoreCase) {
            sFieldName = findPropertyName(objFrom.getClass(), sFieldName);

        }
        try {
            return BeanUtils.getProperty(objFrom, sFieldName == null ? "" : sFieldName);
        } catch (Exception e) {
            String exceptionName = e.getClass().getName();
            throw e;
        }
    }

    /**
     * 把集合中的实例存放到Map里面，以p_fieldname指定的属性名作为Map的Key。
     *
     * @param p_fieldname 属性名称
     * @param p_instCol 集合
     * @return Map 集合
     * @throws IllegalAccessException IllegalAccessException异常
     * @throws NoSuchMethodException NoSuchMethodException异常
     * @throws InvocationTargetException InvocationTargetException异常
     */
    public static Map instColtoMapWithFieldKey(String p_fieldname, Collection p_instCol)
            throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Map instMap = new HashMap();
        for (Iterator iterator = p_instCol.iterator(); iterator.hasNext();) {
            Object o = iterator.next();
            Object key = getPropertyByName(o, p_fieldname, true);
            instMap.put(key, o);
        }
        return instMap;
    }

    /**
     * 把对象的属性及相应的属性值转换为哈希表,属性名作key,属性值作value,可控制是否只获取公有属性.
     *
     * @param instance
     *            Object 对象
     * @param bOnlyPublicAccessLevelProperties
     *            boolean 是否只获取公有属性
     * @return Map 储存属性和属性值的哈希表
     * @throws IllegalAccessException IllegalAccessException异常
     */
    public static Map instanceFieldsToMap(Object instance, boolean bOnlyPublicAccessLevelProperties)
            throws IllegalAccessException {
        Field[] fields = null;
        Map mapReturn = null;
        if (bOnlyPublicAccessLevelProperties) {
            fields = instance.getClass().getFields();
        } else {
            fields = getFields(instance);
        }
        mapReturn = new HashMap(fields.length);
        for (int i = 0; i < fields.length; i++) {
            String sFieldName = null;
            Object fieldValue = null;
            Field field = fields[i];
            field.setAccessible(true);

            sFieldName = field.getName();
            fieldValue = field.get(instance);
            mapReturn.put(sFieldName, fieldValue);
        }
        return mapReturn;
    }

    /**
     * 为对象指定的属性赋值,可指定是否忽略属性名的大小写
     *
     * @param objTo
     *            Object 对象
     * @param sFieldName
     *            String 属性名称
     * @param value
     *            Object 值
     * @param bIgnoreCase
     *            boolean 是否忽略大小写
     * @throws IllegalAccessException IllegalAccessException异常
     * @throws InvocationTargetException InvocationTargetException异常
     */
    public static void setPropertyByName(Object objTo, String sFieldName, Object value, boolean bIgnoreCase)
            throws IllegalAccessException, InvocationTargetException {
        if (bIgnoreCase) {
            sFieldName = findPropertyName(objTo.getClass(), sFieldName);
        }
        BeanUtils.copyProperty(objTo, sFieldName, value);
    }

    // -------------------------- OTHER METHODS --------------------------

    /**
     * 和ReflectUtils.describe的区别是, 这个取信息的时候, 不是根据obj参数的属性,
     * 而是根据obj里面所定义的field来生成这个map.
     *
     * @param obj 对象
     * @param bGetSuperClassToo
     *            如果设置了这个参数, 那么不但会取出这个对象的field, 还会取出这个对象所继承的对象的field来构成这个map
     * @return Map集合
     * @throws IllegalAccessException IllegalAccessException异常
     * @throws InvocationTargetException InvocationTargetException异常
     * @throws NoSuchFieldException NoSuchFieldException异常
     */
    public static Map<String, Object> describeByFields(Object obj, boolean bGetSuperClassToo)
            throws IllegalAccessException, InvocationTargetException, NoSuchFieldException {
        if (obj == null) {
            throw new IllegalArgumentException("No obj specified");
        }
        Class classToView = obj.getClass();
        return describeByFields(obj, classToView, bGetSuperClassToo);
    }

    private static Map<String, Object> describeByFields(Object obj, Class p_classToView, boolean bGetSuperClassToo)
            throws IllegalAccessException {
        Map<String, Object> toReturn = new HashMap<String, Object>();
        if (bGetSuperClassToo) {
            Class superclz = p_classToView.getSuperclass();
            if (superclz != Object.class) {
                toReturn.putAll(describeByFields(obj, superclz, bGetSuperClassToo));
            }
        }
        Field[] origFields = p_classToView.getDeclaredFields();
        for (Field origField : origFields) {
            String name = origField.getName();
            origField.setAccessible(true);
            toReturn.put(name, origField.get(obj));
        }
        return toReturn;
    }

    /**
     * 复制对象,可控制是否复制空值属性
     *
     * @param dest
     *            Object 目标对象
     * @param orig
     *            Object 源对象
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private static void copyFields(Object dest, Object orig)
            throws IllegalAccessException, InvocationTargetException, NoSuchFieldException {
        // Validate existence of the specified beans
        if (dest == null) {
            throw new IllegalArgumentException("No destination bean specified");
        }
        if (orig == null) {
            throw new IllegalArgumentException("No origin bean specified");
        }
        // Copy the properties, converting as necessary
        if (orig instanceof Map) {
            Iterator names = ((Map) orig).keySet().iterator();
            while (names.hasNext()) {
                String name = (String) names.next();
                System.out.println("------->name:" + name);
                Field destFld = findField(dest.getClass(), name);
                // String fieldname = findFieldName(dest.getClass(), name);
                if (destFld != null) {
                    // Field destFld = dest.getClass().getDeclaredField(fieldname);
                    destFld.setAccessible(true);
                    Class destType = destFld.getType();
                    destFld.set(dest, ConvertUtils.lookup(destType).convert(destType, ((Map) orig).get(name)));
                    // destFld.set(dest, ((Map) orig).get(name));
                    // destFld.setAccessible(false);
                }
            }
        } else
        /* if (orig is a standard JavaBean) */ {
            // Field[] origFields = orig.getClass().getDeclaredFields();
            Field[] origFields = getDeclaredFieldsForClass(orig.getClass());
            for (int i = 0; i < origFields.length; i++) {
                Field origField = origFields[i];
                String name = origField.getName();
                // String fieldname = findFieldName(dest.getClass(), name);
                Field destFld = findField(dest.getClass(), name);
                if (destFld != null) {
                    // Field destFld = dest.getClass().getDeclaredField(fieldname);
                    destFld.setAccessible(true);
                    origField.setAccessible(true);
                    Class destType = destFld.getType();
                    destFld.set(dest, ConvertUtils.lookup(destType).convert(destType, origField.get(orig)));
                    // destFld.setAccessible(false);
                    // origField.setAccessible(false);
                }
            }
        }
    }

    /**
     * 返回指定的类的声明了的字段, 包括其父类所声明的字段, 但是不包含object所声明的字段
     *
     * @param clz 类
     * @return Field数组
     */
    private static Field[] getDeclaredFieldsForClass(Class clz) {
        if (clz == Object.class) {
            return new Field[0];
        } else {
            ArrayList<Field> fieldlist = new ArrayList<Field>();
            fieldlist.addAll(Arrays.asList(clz.getDeclaredFields()));
            Field[] fieldsOfSuperClz = getDeclaredFieldsForClass(clz.getSuperclass());
            if (fieldsOfSuperClz != null) {
                fieldlist.addAll(Arrays.asList(fieldsOfSuperClz));
            }
            return fieldlist.toArray(new Field[0]);
        }
    }

    /**
     * 返回指定的类的声明了的字段, 包括其父类所声明的字段, 但是不包含object所声明的字段
     * <p/>
     * <p/>
     * <p/>
     * Map的key是字段名, 大小写区分, Map的value是Field对象
     *
     * @param clz
     * @return Map集合
     */
    private static Map<String, Field> getDeclaredFieldsForClassInMap(Class clz) {
        // todo add a cache to here.
        if (clz == Object.class) {
            return new HashMap<String, Field>();
        } else {
            Map<String, Field> fieldmap = new HashMap<String, Field>();
            ArrayList<Field> fieldlist = new ArrayList<Field>();
            fieldlist.addAll(Arrays.asList(clz.getDeclaredFields()));
            Field[] fieldsOfSuperClz = getDeclaredFieldsForClass(clz.getSuperclass());
            if (fieldsOfSuperClz != null) {
                fieldlist.addAll(Arrays.asList(fieldsOfSuperClz));
            }
            for (Field field : fieldlist) {
                fieldmap.put(field.getName(), field);
            }
            return fieldmap;
        }
    }

    /**
     * 复制对象,可控制是否复制空值属性
     *
     * @param dest
     *            Object 目标对象
     * @param orig
     *            Object 源对象
     * @param bIncludeNull
     *            boolean 是否复制空值
     * @throws IllegalAccessException IllegalAccessException异常
     * @throws InvocationTargetException InvocationTargetException异常
     */
    private static void copyProperties(Object dest, Object orig, boolean bIncludeNull)
            throws IllegalAccessException, InvocationTargetException {
        // Validate existence of the specified beans
        if (dest == null) {
            throw new IllegalArgumentException("No destination bean specified");
        }
        if (orig == null) {
            throw new IllegalArgumentException("No origin bean specified");
        }
        // Copy the properties, converting as necessary
        if (orig instanceof DynaBean) {
            DynaProperty origDescriptors[] = ((DynaBean) orig).getDynaClass().getDynaProperties();
            for (int i = 0; i < origDescriptors.length; i++) {
                String name = origDescriptors[i].getName();
                if (PropertyUtils.isWriteable(dest, name)) {
                    Object value = ((DynaBean) orig).get(name);
                    if (bIncludeNull || value != null) {
                        BeanUtils.copyProperty(dest, name, value);
                    }
                }
            }
        } else if (orig instanceof Map) {
            Iterator names = ((Map) orig).keySet().iterator();
            while (names.hasNext()) {
                String name = (String) names.next();
                if (PropertyUtils.isWriteable(dest, name)) {
                    Object value = ((Map) orig).get(name);
                    if (bIncludeNull || value != null) {
                        BeanUtils.copyProperty(dest, name, value);
                    }
                }
            }
        } else
        /* if (orig is a standard JavaBean) */ {
            PropertyDescriptor origDescriptors[] = PropertyUtils.getPropertyDescriptors(orig);
            for (int i = 0; i < origDescriptors.length; i++) {
                String name = origDescriptors[i].getName();
                if ("class".equals(name)) {
                    continue;// No point in trying to set an object's class
                }
                if (PropertyUtils.isReadable(orig, name) && PropertyUtils.isWriteable(dest, name)) {
                    try {
                        Object value = PropertyUtils.getSimpleProperty(orig, name);
                        if (bIncludeNull || value != null) {
                            BeanUtils.copyProperty(dest, name, value);
                        }
                    } catch (NoSuchMethodException ex) {
                        ;// Should not happen
                    }
                }
            }
        }
    }

    /**
     * 获取除传入对象的所有属性(包括"公有","保护"和"私有",不包括自继承父类的)
     *
     * @param objToListFields
     *            Object 对象
     * @return Field[] 属性对象数组
     */
    private static Field[] getFields(Object objToListFields) {
        // after test, use cache is not faster than no cache. JDK1.4.1
        return objToListFields.getClass().getDeclaredFields();
    }

    /**
     * 将MAP，转换为指定的对象
     *
     * @param clazz 类
     * @param valMap map集合
     * @param <T> 泛型
     * @return 泛型
     * @throws Exception 异常
     */
    @SuppressWarnings("unchecked")
    public static <T> T mapToPOInstance(Class clazz, Map valMap) throws Exception {
        if (clazz == null) {
            return null;
        }
        if (valMap == null) {
            return (T) clazz.newInstance();
        }
        Iterator<String> it = valMap.keySet().iterator();
        Map<String, Object> copyMap = new HashMap<String, Object>();
        while (it.hasNext()) {
            String key = it.next();
            String newKey = key.toLowerCase();
            copyMap.put(newKey, valMap.get(key));
        }
        Field[] fields = clazz.getDeclaredFields();
        Object obj = null;
        obj = clazz.newInstance();
        for (Field field : fields) {
            String fieldName = field.getName();
            Object valObj = copyMap.get(fieldName.toLowerCase());
            if (valObj != null && !"".equals(valObj.toString())) {
                Class fieldClass = field.getType();
                if (fieldClass.getName().startsWith("java.")) {
                    if (fieldClass == Date.class) {
                        java.text.DateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd");
                        valObj = df.parse(valObj.toString());
                    }
                    BeanUtils.setProperty(obj, fieldName, valObj);
                }
            }
        }
        return (T) obj;
    }
}
