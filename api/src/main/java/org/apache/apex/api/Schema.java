package org.apache.apex.api;

import org.apache.commons.lang.ClassUtils;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by dtadmin on 08-Feb-17.
 */
public class Schema implements UserDefinedControlTuple
{
  public String name;
  public String fqcn;
  public List<FieldInfo> fieldList = new ArrayList<>();
  public Class beanClass;
  public byte[] beanClassBytes;

  public Schema addField(String fieldName, Class fieldType)
  {
    fieldList.add(new FieldInfo(fieldName, Type.getFromJavaType(fieldType)));
    return this;
  }

  public static class FieldInfo
  {
    String columnName;
    Type type;

    public FieldInfo(String columnName, Type type)
    {
      this.columnName = columnName;
      this.type = type;
    }

    public String getColumnName()
    {
      return columnName;
    }

    public Type getType()
    {
      return type;
    }
  }

  public enum Type
  {
    BOOLEAN(Boolean.class), SHORT(Short.class), INTEGER(Integer.class), LONG(Long.class),
    FLOAT(Float.class), DOUBLE(Double.class), STRING(String.class), OBJECT(Object.class),
    DATE(Date.class), TIME(Time.class);

    private Class javaType;

    Type(Class javaType)
    {
      this.javaType = javaType;
    }

    public static Type getFromJavaType(Class type)
    {
      for (Type supportType : Type.values()) {
        if (supportType.getJavaType() == ClassUtils.primitiveToWrapper(type)) {
          return supportType;
        }
      }

      return OBJECT;
    }

    public Class getJavaType()
    {
      return javaType;
    }
  }
}
