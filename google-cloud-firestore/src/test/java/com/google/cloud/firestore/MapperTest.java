/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.firestore;

import static com.google.cloud.firestore.LocalFirestoreHelper.fromSingleQuotedString;
import static com.google.cloud.firestore.LocalFirestoreHelper.mapAnyType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.Exclude;
import com.google.cloud.firestore.annotation.PropertyName;
import com.google.cloud.firestore.annotation.ThrowOnExtraProperties;
import com.google.cloud.firestore.spi.v1.FirestoreRpc;
import com.google.common.collect.ImmutableList;
import com.google.firestore.v1.DatabaseRootName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

@SuppressWarnings({"unused", "WeakerAccess", "SpellCheckingInspection"})
@RunWith(MockitoJUnitRunner.class)
public class MapperTest {

  @Spy
  private FirestoreImpl firestoreMock =
      new FirestoreImpl(
          FirestoreOptions.newBuilder().setProjectId("test-project").build(),
          Mockito.mock(FirestoreRpc.class));

  private static final double EPSILON = 0.0003;

  private static class StringBean {
    private String value;

    public String getValue() {
      return value;
    }
  }

  private static class DoubleBean {
    private double value;

    public double getValue() {
      return value;
    }
  }

  private static class BigDecimalBean {
    private BigDecimal value;

    public BigDecimal getValue() {
      return value;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      BigDecimalBean bean = (BigDecimalBean) o;
      return Objects.equals(value, bean.value);
    }

    @Override
    public int hashCode() {
      return Objects.hash(value);
    }
  }

  private static class FloatBean {
    private float value;

    public float getValue() {
      return value;
    }
  }

  private static class LongBean {
    private long value;

    public long getValue() {
      return value;
    }
  }

  private static class IntBean {
    private int value;

    public int getValue() {
      return value;
    }
  }

  private static class BooleanBean {
    private boolean value;

    public boolean isValue() {
      return value;
    }
  }

  private static class ShortBean {
    private short value;

    public short getValue() {
      return value;
    }
  }

  private static class ByteBean {
    private byte value;

    public byte getValue() {
      return value;
    }
  }

  private static class CharBean {
    private char value;

    public char getValue() {
      return value;
    }
  }

  private static class IntArrayBean {
    private int[] values;

    public int[] getValues() {
      return values;
    }
  }

  private static class StringArrayBean {
    private String[] values;

    public String[] getValues() {
      return values;
    }
  }

  private static class XMLAndURLBean {
    private String XMLAndURL1;
    public String XMLAndURL2;

    public String getXMLAndURL1() {
      return XMLAndURL1;
    }

    public void setXMLAndURL1(String value) {
      XMLAndURL1 = value;
    }
  }

  private static class SetterBean {
    private String value;

    public String getValue() {
      return value;
    }

    public void setValue(String value) {
      this.value = "setter:" + value;
    }
  }

  private static class PrivateSetterBean {
    public String value;

    private void setValue(String value) {
      this.value = "setter:" + value;
    }
  }

  private static class GetterBean {
    private String value;

    public String getValue() {
      return "getter:" + this.value;
    }
  }

  @ThrowOnExtraProperties
  private static class GetterBeanNoField {
    public String getValue() {
      return "getter:value";
    }
  }

  private static class GetterPublicFieldBean {
    public String value;

    public String getValue() {
      return "getter:" + value;
    }
  }

  private static class GetterPublicFieldBeanCaseSensitive {
    public String valueCase;

    public String getValueCASE() {
      return "getter:" + valueCase;
    }
  }

  private static class CaseSensitiveGetterBean1 {
    private String value;

    public String getVALUE() {
      return value;
    }
  }

  private static class CaseSensitiveGetterBean2 {
    private String value;

    public String getvalue() {
      return value;
    }
  }

  private static class CaseSensitiveGetterBean3 {
    private String value;

    public String getVAlue() {
      return value;
    }
  }

  private static class CaseSensitiveGetterBean4 {
    private String value;

    public String getvaLUE() {
      return value;
    }
  }

  private static class CaseSensitiveSetterBean1 {
    private String value;

    public String getValue() {
      return value;
    }

    public void setValue(String value) {
      this.value = "setter:" + value;
    }

    public void setVAlue(String value) {
      this.value = "wrong setter!";
    }
  }

  private static class CaseSensitiveSetterBean2 {
    private String value;

    public String getValue() {
      return value;
    }

    public void setValue(String value) {
      this.value = "setter:" + value;
    }

    public void setvalue(String value) {
      this.value = "wrong setter!";
    }
  }

  private static class CaseSensitiveSetterBean3 {
    private String value;

    public String getValue() {
      return value;
    }

    public void setvalue(String value) {
      this.value = "setter:" + value;
    }
  }

  private static class CaseSensitiveSetterBean4 {
    private String value;

    public String getValue() {
      return value;
    }

    public void setVALUE(String value) {
      this.value = "setter:" + value;
    }
  }

  private static class CaseSensitiveSetterBean5 {
    private String value;

    public String getValue() {
      return value;
    }

    public void SETVALUE(String value) {
      this.value = "wrong setter!";
    }
  }

  private static class CaseSensitiveSetterBean6 {
    private String value;

    public String getValue() {
      return value;
    }

    public void setVaLUE(String value) {
      this.value = "setter:" + value;
    }
  }

  @SuppressWarnings("ConstantField")
  private static class CaseSensitiveFieldBean1 {
    public String VALUE;
  }

  private static class CaseSensitiveFieldBean2 {
    public String value;
  }

  private static class CaseSensitiveFieldBean3 {
    public String Value;
  }

  private static class CaseSensitiveFieldBean4 {
    public String valUE;
  }

  private static class WrongSetterBean {
    private String value;

    public String getValue() {
      return value;
    }

    public void setValue() {
      this.value = "wrong setter!";
    }

    public void setValue(String one, String two) {
      this.value = "wrong setter!";
    }
  }

  private static class WrongTypeBean {
    private Integer value;

    public String getValue() {
      return "" + value;
    }
  }

  private static class NestedBean {
    private StringBean bean;

    public StringBean getBean() {
      return bean;
    }
  }

  private static class ObjectBean {
    private Object value;

    public Object getValue() {
      return value;
    }
  }

  private static class GenericBean<B> {
    private B value;

    public B getValue() {
      return value;
    }
  }

  private static class DoubleGenericBean<A, B> {
    private A valueA;
    private B valueB;

    public A getValueA() {
      return valueA;
    }

    public B getValueB() {
      return valueB;
    }
  }

  private static class ListBean {
    private List<String> values;

    public List<String> getValues() {
      return values;
    }
  }

  private static class SetBean {
    private Set<String> values;

    public Set<String> getValues() {
      return values;
    }
  }

  private static class CollectionBean {
    private Collection<String> values;

    public Collection<String> getValues() {
      return values;
    }
  }

  private static class MapBean {
    private Map<String, String> values;

    public Map<String, String> getValues() {
      return values;
    }
  }

  /**
   * This form is not terribly useful in Java, but Kotlin Maps are immutable and are rewritten into
   * this form (b/67470108 has more details).
   */
  private static class UpperBoundedMapBean {
    private Map<String, ? extends Instant> values;

    public Map<String, ? extends Instant> getValues() {
      return values;
    }
  }

  private static class MultiBoundedMapBean<T extends Instant & Serializable> {
    private Map<String, T> values;

    public Map<String, T> getValues() {
      return values;
    }
  }

  private static class MultiBoundedMapHolderBean {
    private MultiBoundedMapBean<Instant> map;

    public MultiBoundedMapBean<Instant> getMap() {
      return map;
    }
  }

  private static class UnboundedMapBean {
    private Map<String, ?> values;

    public Map<String, ?> getValues() {
      return values;
    }
  }

  private static class UnboundedTypeVariableMapBean<T> {
    private Map<String, T> values;

    public Map<String, T> getValues() {
      return values;
    }
  }

  private static class UnboundedTypeVariableMapHolderBean {
    private UnboundedTypeVariableMapBean<String> map;

    public UnboundedTypeVariableMapBean<String> getMap() {
      return map;
    }
  }

  private static class NestedListBean {
    private List<StringBean> values;

    public List<StringBean> getValues() {
      return values;
    }
  }

  private static class NestedMapBean {
    private Map<String, StringBean> values;

    public Map<String, StringBean> getValues() {
      return values;
    }
  }

  private static class IllegalKeyMapBean {
    private Map<Integer, StringBean> values;

    public Map<Integer, StringBean> getValues() {
      return values;
    }
  }

  private static class PublicFieldBean {
    public String value;
  }

  @ThrowOnExtraProperties
  private static class ThrowOnUnknownPropertiesBean {
    public String value;
  }

  @ThrowOnExtraProperties
  private static class PackageFieldBean {
    String value;
  }

  @ThrowOnExtraProperties
  @SuppressWarnings("unused") // Unused, but required for the test
  private static class PrivateFieldBean {
    private String value;
  }

  private static class PackageGetterBean {
    private String packageValue;
    private String publicValue;

    String getPackageValue() {
      return packageValue;
    }

    public String getPublicValue() {
      return publicValue;
    }
  }

  private static class ExcludedBean {
    @Exclude public String excludedField = "no-value";

    private String excludedGetter = "no-value";

    private String includedGetter = "no-value";

    @Exclude
    public String getExcludedGetter() {
      return excludedGetter;
    }

    public String getIncludedGetter() {
      return includedGetter;
    }
  }

  private static class ExcludedSetterBean {
    private String value;

    public String getValue() {
      return value;
    }

    @Exclude
    public void setValue(String value) {
      this.value = "wrong setter";
    }
  }

  private static class PropertyNameBean {

    @PropertyName("my_key")
    public String key;

    private String value;

    @PropertyName("my_value")
    public String getValue() {
      return value;
    }

    @PropertyName("my_value")
    public void setValue(String value) {
      this.value = value;
    }
  }

  private static class PublicPrivateFieldBean {
    public String value1;
    String value2;
    private String value3;
  }

  private static class TwoSetterBean {
    private String value;

    public String getValue() {
      return value;
    }

    public void setValue(String value) {
      this.value = "string:" + value;
    }

    public void setValue(Integer value) {
      this.value = "int:" + value;
    }
  }

  private static class TwoGetterBean {
    private String value;

    public String getValue() {
      return value;
    }

    public String getVALUE() {
      return value;
    }
  }

  private static class GetterArgumentsBean {
    private String value;

    public String getValue1() {
      return value + "1";
    }

    public void getValue2() {}

    public String getValue3(boolean flag) {
      return value + "3";
    }

    public String getValue4() {
      return value + "4";
    }
  }

  @SuppressWarnings({"ConstantField", "NonAsciiCharacters"})
  private static class UnicodeBean {
    private String 漢字;

    public String get漢字() {
      return 漢字;
    }
  }

  private static class PublicConstructorBean {
    private String value;

    public PublicConstructorBean() {}

    public String getValue() {
      return value;
    }
  }

  private static class PrivateConstructorBean {
    private String value;

    private PrivateConstructorBean() {}

    public String getValue() {
      return value;
    }
  }

  private static class PackageConstructorBean {
    private String value;

    PackageConstructorBean() {}

    public String getValue() {
      return value;
    }
  }

  private static class ArgConstructorBean {
    private String value;

    public ArgConstructorBean(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }
  }

  private static class MultipleConstructorBean {
    private String value;

    public MultipleConstructorBean(String value) {
      this.value = "wrong-value";
    }

    public MultipleConstructorBean() {}

    public String getValue() {
      return value;
    }
  }

  private static class StaticFieldBean {
    public static String value1 = "static-value";
    public String value2;
  }

  private static class StaticMethodBean {
    private static String value1 = "static-value";
    public String value2;

    public static String getValue1() {
      return StaticMethodBean.value1;
    }

    public static void setValue1(String value1) {
      StaticMethodBean.value1 = value1;
    }
  }

  private enum SimpleEnum {
    Foo,
    Bar
  }

  private enum ComplexEnum {
    One("one"),
    Two("two"),

    @PropertyName("Three")
    THREE("three");

    private final String value;

    ComplexEnum(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }
  }

  private enum PathologicalEnum {
    @PropertyName("Two")
    One,

    @PropertyName("One")
    Two
  }

  private static class EnumBean {
    public SimpleEnum enumField;

    private SimpleEnum enumValue;

    public ComplexEnum complexEnum;

    public ComplexEnum enumUsingPropertyName;

    public PathologicalEnum pathologicalEnum;

    public SimpleEnum getEnumValue() {
      return enumValue;
    }

    public void setEnumValue(SimpleEnum enumValue) {
      this.enumValue = enumValue;
    }
  }

  private static class BaseBean {
    // Public field on base class
    public String baseValue;

    // Value that is accessed through overridden methods in subclasses
    public String overrideValue;

    // Field that is package private in base class
    String packageBaseValue;

    // Private field that is used in getter/setter in base class
    private String baseMethodValue;

    // Private field that has field with same name in subclasses
    private String classPrivateValue;

    public String getClassPrivateValue() {
      return classPrivateValue;
    }

    public String getBaseMethod() {
      return baseMethodValue;
    }

    public String getPackageBaseValue() {
      return packageBaseValue;
    }

    public void setBaseMethod(String value) {
      baseMethodValue = value;
    }
  }

  private static class InheritedBean extends BaseBean {
    public String inheritedValue;

    private String inheritedMethodValue;

    private String classPrivateValue;

    @Override
    public String getClassPrivateValue() {
      return classPrivateValue;
    }

    public String getInheritedMethod() {
      return inheritedMethodValue;
    }

    public void setInheritedMethod(String value) {
      inheritedMethodValue = value;
    }

    public String getOverrideValue() {
      return overrideValue + "-inherited";
    }

    public void setOverrideValue(String value) {
      overrideValue = value + "-inherited";
    }
  }

  private static final class FinalBean extends InheritedBean {
    public String finalValue;

    private String finalMethodValue;

    private String classPrivateValue;

    @Override
    public String getClassPrivateValue() {
      return classPrivateValue;
    }

    public String getFinalMethod() {
      return finalMethodValue;
    }

    public void setFinalMethod(String value) {
      finalMethodValue = value;
    }

    @Override
    public String getOverrideValue() {
      return overrideValue + "-final";
    }

    @Override
    public void setOverrideValue(String value) {
      overrideValue = value + "-final";
    }
  }

  // Conflicting setters are not supported. When inheriting from a base class we require all
  // setters be an override of a base class
  private static class ConflictingSetterBean {
    public int value;

    // package private so override can be public
    void setValue(int value) {
      this.value = value;
    }
  }

  private static class ConflictingSetterSubBean extends ConflictingSetterBean {
    public void setValue(String value) {
      this.value = -1;
    }
  }

  private static class ConflictingSetterSubBean2 extends ConflictingSetterBean {
    public void setValue(Integer value) {
      this.value = -1;
    }
  }

  private static class NonConflictingSetterSubBean extends ConflictingSetterBean {
    @Override
    public void setValue(int value) {
      this.value = value * -1;
    }
  }

  private static class GenericSetterBaseBean<T> {
    public T value;

    void setValue(T value) {
      this.value = value;
    }
  }

  private static class ConflictingGenericSetterSubBean<T> extends GenericSetterBaseBean<T> {
    public void setValue(String value) {
      // wrong setter
    }
  }

  private static class NonConflictingGenericSetterSubBean extends GenericSetterBaseBean<String> {
    @Override
    public void setValue(String value) {
      this.value = "subsetter:" + value;
    }
  }

  private static <T> T deserialize(String jsonString, Class<T> clazz) {
    return deserialize(jsonString, clazz, /*docRef=*/ null);
  }

  private static <T> T deserialize(String jsonString, Class<T> clazz, DocumentReference docRef) {
    Map<String, Object> json = fromSingleQuotedString(jsonString);
    return CustomClassMapper.convertToCustomClass(json, clazz, docRef);
  }

  private static Object serialize(Object object) {
    return CustomClassMapper.convertToPlainJavaTypes(object);
  }

  private static void assertJson(String expected, Object actual) {
    assertEquals(fromSingleQuotedString(expected), actual);
  }

  private static void assertExceptionContains(String partialMessage, Runnable run) {
    try {
      run.run();
      fail("Expected exception not thrown");
    } catch (RuntimeException e) {
      assertTrue(e.getMessage().contains(partialMessage));
    }
  }

  private static <T> T convertToCustomClass(
      Object object, Class<T> clazz, DocumentReference docRef) {
    return CustomClassMapper.convertToCustomClass(object, clazz, docRef);
  }

  private static <T> T convertToCustomClass(Object object, Class<T> clazz) {
    return CustomClassMapper.convertToCustomClass(object, clazz, null);
  }

  @Test
  public void primitiveDeserializeString() {
    StringBean bean = deserialize("{'value': 'foo'}", StringBean.class);
    assertEquals("foo", bean.value);

    // Double
    try {
      deserialize("{'value': 1.1}", StringBean.class);
      fail("Should throw");
    } catch (RuntimeException e) { // ignore
    }

    // Int
    try {
      deserialize("{'value': 1}", StringBean.class);
      fail("Should throw");
    } catch (RuntimeException e) { // ignore
    }

    // Long
    try {
      deserialize("{'value': 1234567890123}", StringBean.class);
      fail("Should throw");
    } catch (RuntimeException e) { // ignore
    }

    // Boolean
    try {
      deserialize("{'value': true}", StringBean.class);
      fail("Should throw");
    } catch (RuntimeException e) { // ignore
    }
  }

  @Test
  public void primitiveDeserializeBoolean() {
    BooleanBean beanBoolean = deserialize("{'value': true}", BooleanBean.class);
    assertEquals(true, beanBoolean.value);

    // Double
    try {
      deserialize("{'value': 1.1}", BooleanBean.class);
      fail("Should throw");
    } catch (RuntimeException e) { // ignore
    }

    // Long
    try {
      deserialize("{'value': 1234567890123}", BooleanBean.class);
      fail("Should throw");
    } catch (RuntimeException e) { // ignore
    }

    // Int
    try {
      deserialize("{'value': 1}", BooleanBean.class);
      fail("Should throw");
    } catch (RuntimeException e) { // ignore
    }

    // String
    try {
      deserialize("{'value': 'foo'}", BooleanBean.class);
      fail("Should throw");
    } catch (RuntimeException e) { // ignore
    }
  }

  @Test
  public void primitiveDeserializeDouble() {
    DoubleBean beanDouble = deserialize("{'value': 1.1}", DoubleBean.class);
    assertEquals(1.1, beanDouble.value, EPSILON);

    // Int
    DoubleBean beanInt = deserialize("{'value': 1}", DoubleBean.class);
    assertEquals(1, beanInt.value, EPSILON);
    // Long
    DoubleBean beanLong = deserialize("{'value': 1234567890123}", DoubleBean.class);
    assertEquals(1234567890123L, beanLong.value, EPSILON);

    // Boolean
    try {
      deserialize("{'value': true}", DoubleBean.class);
      fail("Should throw");
    } catch (RuntimeException e) { // ignore
    }

    // String
    try {
      deserialize("{'value': 'foo'}", DoubleBean.class);
      fail("Should throw");
    } catch (RuntimeException e) { // ignore
    }
  }

  @Test
  public void primitiveDeserializeBigDecimal() {
    BigDecimalBean beanBigdecimal = deserialize("{'value': 123}", BigDecimalBean.class);
    assertEquals(BigDecimal.valueOf(123), beanBigdecimal.value);

    beanBigdecimal = deserialize("{'value': '123'}", BigDecimalBean.class);
    assertEquals(BigDecimal.valueOf(123), beanBigdecimal.value);

    // Int
    BigDecimalBean beanInt = deserialize("{'value': 1}", BigDecimalBean.class);
    assertEquals(BigDecimal.valueOf(1), beanInt.value);

    // Long
    BigDecimalBean beanLong = deserialize("{'value': 1234567890123}", BigDecimalBean.class);
    assertEquals(BigDecimal.valueOf(1234567890123L), beanLong.value);

    // Double
    BigDecimalBean beanDouble = deserialize("{'value': 1.1}", BigDecimalBean.class);
    assertEquals(BigDecimal.valueOf(1.1), beanDouble.value);

    // Boolean
    try {
      deserialize("{'value': true}", BigDecimalBean.class);
      fail("Should throw");
    } catch (RuntimeException e) { // ignore
    }

    // String
    try {
      deserialize("{'value': 'foo'}", BigDecimalBean.class);
      fail("Should throw");
    } catch (RuntimeException e) { // ignore
    }
  }

  @Test
  public void primitiveDeserializeFloat() {
    FloatBean beanFloat = deserialize("{'value': 1.1}", FloatBean.class);
    assertEquals(1.1, beanFloat.value, EPSILON);

    // Int
    FloatBean beanInt = deserialize("{'value': 1}", FloatBean.class);
    assertEquals(1, beanInt.value, EPSILON);
    // Long
    FloatBean beanLong = deserialize("{'value': 1234567890123}", FloatBean.class);
    assertEquals((float) 1234567890123L, beanLong.value, EPSILON);

    // Boolean
    try {
      deserialize("{'value': true}", FloatBean.class);
      fail("Should throw");
    } catch (RuntimeException e) { // ignore
    }

    // String
    try {
      deserialize("{'value': 'foo'}", FloatBean.class);
      fail("Should throw");
    } catch (RuntimeException e) { // ignore
    }
  }

  @Test
  public void primitiveDeserializeInt() {
    IntBean beanInt = deserialize("{'value': 1}", IntBean.class);
    assertEquals(1, beanInt.value);

    // Double
    IntBean beanDouble = deserialize("{'value': 1.1}", IntBean.class);
    assertEquals(1, beanDouble.value);

    // Large doubles
    try {
      deserialize("{'value': 1e10}", IntBean.class);
      fail("Should throw");
    } catch (RuntimeException e) { // ignore
    }

    // Long
    try {
      deserialize("{'value': 1234567890123}", IntBean.class);
      fail("Should throw");
    } catch (RuntimeException e) { // ignore
    }

    // Boolean
    try {
      deserialize("{'value': true}", IntBean.class);
      fail("Should throw");
    } catch (RuntimeException e) { // ignore
    }

    // String
    try {
      deserialize("{'value': 'foo'}", IntBean.class);
      fail("Should throw");
    } catch (RuntimeException e) { // ignore
    }
  }

  @Test
  public void primitiveDeserializeLong() {
    LongBean beanLong = deserialize("{'value': 1234567890123}", LongBean.class);
    assertEquals(1234567890123L, beanLong.value);

    // Int
    LongBean beanInt = deserialize("{'value': 1}", LongBean.class);
    assertEquals(1, beanInt.value);

    // Double
    LongBean beanDouble = deserialize("{'value': 1.1}", LongBean.class);
    assertEquals(1, beanDouble.value);

    // Large doubles
    try {
      deserialize("{'value': 1e300}", LongBean.class);
      fail("Should throw");
    } catch (RuntimeException e) { // ignore
    }

    // Boolean
    try {
      deserialize("{'value': true}", LongBean.class);
      fail("Should throw");
    } catch (RuntimeException e) { // ignore
    }

    // String
    try {
      deserialize("{'value': 'foo'}", LongBean.class);
      fail("Should throw");
    } catch (RuntimeException e) { // ignore
    }
  }

  @Test
  public void primitiveDeserializeWrongTypeMap() {
    assertExceptionContains(
        "Failed to convert value of type java.util.LinkedHashMap to String "
            + "(found in field 'value')",
        new Runnable() {
          @Override
          public void run() {
            deserialize("{'value': {'foo': 'bar'}}", StringBean.class);
          }
        });
  }

  @Test
  public void primitiveDeserializeWrongTypeList() {
    assertExceptionContains(
        "Failed to convert value of type java.util.ArrayList to String"
            + " (found in field 'value')",
        new Runnable() {
          @Override
          public void run() {
            deserialize("{'value': ['foo']}", StringBean.class);
          }
        });
  }

  @Test
  public void publicFieldDeserialize() {
    PublicFieldBean bean = deserialize("{'value': 'foo'}", PublicFieldBean.class);
    assertEquals("foo", bean.value);
  }

  @Test
  public void publicPrivateFieldDeserialize() {
    PublicPrivateFieldBean bean =
        deserialize(
            "{'value1': 'foo', 'value2': 'bar', 'value3': 'baz'}", PublicPrivateFieldBean.class);
    assertEquals("foo", bean.value1);
    assertEquals(null, bean.value2);
    assertEquals(null, bean.value3);
  }

  @Test
  public void packageFieldDeserialize() {
    assertExceptionContains(
        "No properties to serialize found on class "
            + "com.google.cloud.firestore.MapperTest$PackageFieldBean",
        new Runnable() {
          @Override
          public void run() {
            deserialize("{'value': 'foo'}", PackageFieldBean.class);
          }
        });
  }

  @Test
  public void privateFieldDeserialize() {
    assertExceptionContains(
        "No properties to serialize found on class "
            + "com.google.cloud.firestore.MapperTest$PrivateFieldBean",
        new Runnable() {
          @Override
          public void run() {
            deserialize("{'value': 'foo'}", PrivateFieldBean.class);
          }
        });
  }

  @Test
  public void packageGetterDeserialize() {
    PackageGetterBean bean =
        deserialize("{'publicValue': 'foo', 'packageValue': 'bar'}", PackageGetterBean.class);
    assertEquals("foo", bean.publicValue);
    assertNull(bean.packageValue);
  }

  @Test
  public void packageGetterSerialize() {
    PackageGetterBean bean = new PackageGetterBean();
    bean.packageValue = "foo";
    bean.publicValue = "bar";
    assertJson("{'publicValue': 'bar'}", serialize(bean));
  }

  @Test
  public void ignoreExtraProperties() {
    PublicFieldBean bean = deserialize("{'value': 'foo', 'unknown': 'bar'}", PublicFieldBean.class);
    assertEquals("foo", bean.value);
  }

  @Test
  public void throwOnUnknownProperties() {
    assertExceptionContains(
        "No setter/field for unknown found on class "
            + "com.google.cloud.firestore.MapperTest$ThrowOnUnknownPropertiesBean",
        new Runnable() {
          @Override
          public void run() {
            deserialize("{'value': 'foo', 'unknown': 'bar'}", ThrowOnUnknownPropertiesBean.class);
          }
        });
  }

  @Test
  public void twoSetterBean() {
    assertExceptionContains(
        "Class com.google.cloud.firestore.MapperTest$TwoSetterBean has multiple setter "
            + "overloads",
        new Runnable() {
          @Override
          public void run() {
            deserialize("{'value': 'foo'}", TwoSetterBean.class);
          }
        });
  }

  @Test
  public void XMLAndURLBean() {
    XMLAndURLBean bean =
        deserialize("{'xmlandURL1': 'foo', 'XMLAndURL2': 'bar'}", XMLAndURLBean.class);
    assertEquals("foo", bean.XMLAndURL1);
    assertEquals("bar", bean.XMLAndURL2);
  }

  /** Based on https://github.com/firebase/firebase-android-sdk/issues/252. */
  private static class AllCapsDefaultHandlingBean {
    private String UUID;

    public String getUUID() {
      return UUID;
    }

    public void setUUID(String value) {
      UUID = value;
    }
  }

  @Test
  public void allCapsGetterSerializesToLowercaseByDefault() {
    AllCapsDefaultHandlingBean bean = new AllCapsDefaultHandlingBean();
    bean.setUUID("value");
    assertJson("{'uuid': 'value'}", serialize(bean));
    AllCapsDefaultHandlingBean deserialized =
        deserialize("{'uuid': 'value'}", AllCapsDefaultHandlingBean.class);
    assertEquals("value", deserialized.getUUID());
  }

  private static class AllCapsWithPropertyName {
    private String UUID;

    @PropertyName("UUID")
    public String getUUID() {
      return UUID;
    }

    @PropertyName("UUID")
    public void setUUID(String value) {
      UUID = value;
    }
  }

  @Test
  public void allCapsWithPropertyNameSerializesToUppercase() {
    AllCapsWithPropertyName bean = new AllCapsWithPropertyName();
    bean.setUUID("value");
    assertJson("{'UUID': 'value'}", serialize(bean));
    AllCapsWithPropertyName deserialized =
        deserialize("{'UUID': 'value'}", AllCapsWithPropertyName.class);
    assertEquals("value", deserialized.getUUID());
  }

  @Test
  public void setterIsCalledWhenPresent() {
    SetterBean bean = deserialize("{'value': 'foo'}", SetterBean.class);
    assertEquals("setter:foo", bean.value);
  }

  @Test
  public void privateSetterIsCalledWhenPresent() {
    PrivateSetterBean bean = deserialize("{'value': 'foo'}", PrivateSetterBean.class);
    assertEquals("setter:foo", bean.value);
  }

  @Test
  public void setterIsCaseSensitive1() {
    assertExceptionContains(
        "Class com.google.cloud.firestore.MapperTest$CaseSensitiveSetterBean1 has "
            + "multiple setter overloads",
        new Runnable() {
          @Override
          public void run() {
            deserialize("{'value': 'foo'}", CaseSensitiveSetterBean1.class);
          }
        });
  }

  @Test
  public void setterIsCaseSensitive2() {
    assertExceptionContains(
        "Class com.google.cloud.firestore.MapperTest$CaseSensitiveSetterBean2 has "
            + "multiple setter overloads",
        new Runnable() {
          @Override
          public void run() {
            deserialize("{'value': 'foo'}", CaseSensitiveSetterBean2.class);
          }
        });
  }

  @Test
  public void caseSensitiveSetterIsCalledWhenPresent1() {
    CaseSensitiveSetterBean3 bean = deserialize("{'value': 'foo'}", CaseSensitiveSetterBean3.class);
    assertEquals("setter:foo", bean.value);
  }

  @Test
  public void caseSensitiveSetterIsCalledWhenPresent2() {
    CaseSensitiveSetterBean4 bean = deserialize("{'value': 'foo'}", CaseSensitiveSetterBean4.class);
    assertEquals("setter:foo", bean.value);
  }

  @Test
  public void caseSensitiveSetterIsCalledWhenPresent3() {
    CaseSensitiveSetterBean5 bean = deserialize("{'value': 'foo'}", CaseSensitiveSetterBean5.class);
    assertEquals("foo", bean.value);
  }

  @Test
  public void caseSensitiveSetterMustHaveSameCaseAsSetter() {
    assertExceptionContains(
        "Found setter on com.google.cloud.firestore.MapperTest$CaseSensitiveSetterBean6 "
            + "with invalid case-sensitive name: setVaLUE",
        new Runnable() {
          @Override
          public void run() {
            deserialize("{'value': 'foo'}", CaseSensitiveSetterBean6.class);
          }
        });
  }

  @Test
  public void wrongSetterIsNotCalledWhenPresent() {
    WrongSetterBean bean = deserialize("{'value': 'foo'}", WrongSetterBean.class);
    assertEquals("foo", bean.value);
  }

  @Test
  public void nestedParsingWorks() {
    NestedBean bean = deserialize("{'bean': {'value': 'foo'}}", NestedBean.class);
    assertEquals("foo", bean.bean.value);
  }

  @Test
  public void beansCanContainLists() {
    ListBean bean = deserialize("{'values': ['foo', 'bar']}", ListBean.class);
    assertEquals(Arrays.asList("foo", "bar"), bean.values);
  }

  @Test
  public void beansCanContainMaps() {
    MapBean bean = deserialize("{'values': {'foo': 'bar'}}", MapBean.class);
    Map<String, Object> expected = fromSingleQuotedString("{'foo': 'bar'}");
    assertEquals(expected, bean.values);
  }

  @Test
  public void beansCanContainUpperBoundedMaps() {
    Instant date = Instant.ofEpochMilli(1491847082123L);
    Map<String, Object> source = mapAnyType("values", mapAnyType("foo", date));
    UpperBoundedMapBean bean = convertToCustomClass(source, UpperBoundedMapBean.class);
    Map<String, Object> expected = mapAnyType("foo", date);
    assertEquals(expected, bean.values);
  }

  @Test
  public void beansCanContainMultiBoundedMaps() {
    Instant date = Instant.ofEpochMilli(1491847082123L);
    Map<String, Object> source = mapAnyType("map", mapAnyType("values", mapAnyType("foo", date)));
    MultiBoundedMapHolderBean bean = convertToCustomClass(source, MultiBoundedMapHolderBean.class);

    Map<String, Object> expected = mapAnyType("foo", date);
    assertEquals(expected, bean.map.values);
  }

  @Test
  public void beansCanContainUnboundedMaps() {
    UnboundedMapBean bean = deserialize("{'values': {'foo': 'bar'}}", UnboundedMapBean.class);
    Map<String, Object> expected = mapAnyType("foo", "bar");
    assertEquals(expected, bean.values);
  }

  @Test
  public void beansCanContainUnboundedTypeVariableMaps() {
    Map<String, Object> source = mapAnyType("map", mapAnyType("values", mapAnyType("foo", "bar")));
    UnboundedTypeVariableMapHolderBean bean =
        convertToCustomClass(source, UnboundedTypeVariableMapHolderBean.class);

    Map<String, Object> expected = mapAnyType("foo", "bar");
    assertEquals(expected, bean.map.values);
  }

  @Test
  public void beansCanContainNestedUnboundedMaps() {
    UnboundedMapBean bean =
        deserialize("{'values': {'foo': {'bar': 'baz'}}}", UnboundedMapBean.class);
    Map<String, Object> expected = mapAnyType("foo", mapAnyType("bar", "baz"));
    assertEquals(expected, bean.values);
  }

  @Test
  public void beansCanContainBeanLists() {
    NestedListBean bean = deserialize("{'values': [{'value': 'foo'}]}", NestedListBean.class);
    assertEquals(1, bean.values.size());
    assertEquals("foo", bean.values.get(0).value);
  }

  @Test
  public void beansCanContainBeanMaps() {
    NestedMapBean bean = deserialize("{'values': {'key': {'value': 'foo'}}}", NestedMapBean.class);
    assertEquals(1, bean.values.size());
    assertEquals("foo", bean.values.get("key").value);
  }

  @Test
  public void beanMapsMustHaveStringKeys() {
    assertExceptionContains(
        "Only Maps with string keys are supported, but found Map with key type class "
            + "java.lang.Integer (found in field 'values')",
        new Runnable() {
          @Override
          public void run() {
            deserialize("{'values': {'1': 'bar'}}", IllegalKeyMapBean.class);
          }
        });
  }

  @Test
  public void serializeStringBean() {
    StringBean bean = new StringBean();
    bean.value = "foo";
    assertJson("{'value': 'foo'}", serialize(bean));
  }

  @Test
  public void serializeDoubleBean() {
    DoubleBean bean = new DoubleBean();
    bean.value = 1.1;
    assertJson("{'value': 1.1}", serialize(bean));
  }

  @Test
  public void serializeIntBean() {
    IntBean bean = new IntBean();
    bean.value = 1;
    assertJson("{'value': 1}", serialize(bean));
  }

  @Test
  public void serializeLongBean() {
    LongBean bean = new LongBean();
    bean.value = 1234567890123L;
    assertJson("{'value': 1234567890123}", serialize(bean));
  }

  @Test
  public void serializeBigDecimalBean() {
    BigDecimalBean bean = new BigDecimalBean();
    bean.value = BigDecimal.valueOf(1.1);
    assertEquals(mapAnyType("value", "1.1"), serialize(bean));
  }

  @Test
  public void bigDecimalRoundTrip() {
    BigDecimal doubleMaxPlusOne = BigDecimal.valueOf(Double.MAX_VALUE).add(BigDecimal.ONE);
    BigDecimalBean a = new BigDecimalBean();
    a.value = doubleMaxPlusOne;
    Map<String, Object> serialized = (Map<String, Object>) serialize(a);
    BigDecimalBean b = convertToCustomClass(serialized, BigDecimalBean.class);
    assertEquals(a, b);
  }

  @Test
  public void serializeBooleanBean() {
    BooleanBean bean = new BooleanBean();
    bean.value = true;
    assertJson("{'value': true}", serialize(bean));
  }

  @Test
  public void serializeFloatBean() {
    FloatBean bean = new FloatBean();
    bean.value = 0.5f;

    // We don't use assertJson as it converts all floating point numbers to Double.
    assertEquals(mapAnyType("value", 0.5f), serialize(bean));
  }

  @Test
  public void serializePublicFieldBean() {
    PublicFieldBean bean = new PublicFieldBean();
    bean.value = "foo";
    assertJson("{'value': 'foo'}", serialize(bean));
  }

  @Test
  public void serializePrivateFieldBean() {
    final PrivateFieldBean bean = new PrivateFieldBean();
    bean.value = "foo";
    assertExceptionContains(
        "No properties to serialize found on class "
            + "com.google.cloud.firestore.MapperTest$PrivateFieldBean",
        new Runnable() {
          @Override
          public void run() {
            serialize(bean);
          }
        });
  }

  @Test
  public void serializePackageFieldBean() {
    final PackageFieldBean bean = new PackageFieldBean();
    bean.value = "foo";
    assertExceptionContains(
        "No properties to serialize found on class "
            + "com.google.cloud.firestore.MapperTest$PackageFieldBean",
        new Runnable() {
          @Override
          public void run() {
            serialize(bean);
          }
        });
  }

  @Test
  public void serializePublicPrivateFieldBean() {
    PublicPrivateFieldBean bean = new PublicPrivateFieldBean();
    bean.value1 = "foo";
    bean.value2 = "bar";
    bean.value3 = "baz";
    assertJson("{'value1': 'foo'}", serialize(bean));
  }

  @Test
  public void getterOverridesField() {
    GetterBean bean = new GetterBean();
    bean.value = "foo";
    assertJson("{'value': 'getter:foo'}", serialize(bean));
  }

  @Test
  public void serializeGetterBeanWithNoBackingField() {
    GetterBeanNoField bean = new GetterBeanNoField();
    assertJson("{'value': 'getter:value'}", serialize(bean));
  }

  @Test
  public void deserializeGetterBeanWithNoBackingFieldThrows() {
    assertExceptionContains(
        "No setter/field",
        new Runnable() {
          @Override
          public void run() {
            deserialize("{'value': 'foo'}", GetterBeanNoField.class);
          }
        });
  }

  @Test
  public void getterOverridesPublicField() {
    GetterPublicFieldBean bean = new GetterPublicFieldBean();
    bean.value = "foo";
    assertJson("{'value': 'getter:foo'}", serialize(bean));
  }

  @Test
  public void getterAndPublicFieldsConflictOnCaseSensitivity() {
    final GetterPublicFieldBeanCaseSensitive bean = new GetterPublicFieldBeanCaseSensitive();
    bean.valueCase = "foo";
    assertExceptionContains(
        "Found two getters or fields with conflicting case sensitivity for property: valuecase",
        new Runnable() {
          @Override
          public void run() {
            serialize(bean);
          }
        });
  }

  @Test
  public void caseSensitiveGetterBean1() {
    CaseSensitiveGetterBean1 bean = new CaseSensitiveGetterBean1();
    bean.value = "foo";
    assertJson("{'value': 'foo'}", serialize(bean));
  }

  @Test
  public void caseSensitiveGetterBean2() {
    CaseSensitiveGetterBean2 bean = new CaseSensitiveGetterBean2();
    bean.value = "foo";
    assertJson("{'value': 'foo'}", serialize(bean));
  }

  @Test
  public void caseSensitiveGetterBean3() {
    CaseSensitiveGetterBean3 bean = new CaseSensitiveGetterBean3();
    bean.value = "foo";
    assertJson("{'value': 'foo'}", serialize(bean));
  }

  @Test
  public void caseSensitiveGetterBean4() {
    CaseSensitiveGetterBean4 bean = new CaseSensitiveGetterBean4();
    bean.value = "foo";
    assertJson("{'vaLUE': 'foo'}", serialize(bean));
  }

  @Test
  public void nestedSerializingWorks() {
    NestedBean bean = new NestedBean();
    bean.bean = new StringBean();
    bean.bean.value = "foo";
    assertJson("{'bean': {'value': 'foo'}}", serialize(bean));
  }

  @Test
  public void serializingListsWorks() {
    ListBean bean = new ListBean();
    bean.values = Arrays.asList("foo", "bar");
    assertJson("{'values': ['foo', 'bar']}", serialize(bean));
  }

  @Test
  public void serializingMapsWorks() {
    MapBean bean = new MapBean();
    bean.values = new HashMap<String, String>();
    bean.values.put("foo", "bar");
    assertJson("{'values': {'foo': 'bar'}}", serialize(bean));
  }

  @Test
  public void serializingUpperBoundedMapsWorks() {
    Instant date = Instant.ofEpochMilli(1491847082123L);
    UpperBoundedMapBean bean = new UpperBoundedMapBean();
    HashMap<String, Instant> values = new HashMap<>();
    values.put("foo", date);

    bean.values = values;
    Map<String, Object> expected =
        mapAnyType("values", mapAnyType("foo", Instant.ofEpochMilli(date.toEpochMilli())));
    assertEquals(expected, serialize(bean));
  }

  @Test
  public void serializingMultiBoundedObjectsWorks() {
    Instant date = Instant.ofEpochMilli(1491847082123L);
    MultiBoundedMapHolderBean holder = new MultiBoundedMapHolderBean();

    HashMap<String, Instant> values = new HashMap<>();
    values.put("foo", date);

    holder.map = new MultiBoundedMapBean<>();
    holder.map.values = values;

    Map<String, Object> expected =
        mapAnyType("map", mapAnyType("values", mapAnyType("foo", Instant.ofEpochMilli(date.toEpochMilli()))));
    assertEquals(expected, serialize(holder));
  }

  @Test
  public void serializeListOfBeansWorks() {
    StringBean stringBean = new StringBean();
    stringBean.value = "foo";

    NestedListBean bean = new NestedListBean();
    bean.values = new ArrayList<>();
    bean.values.add(stringBean);

    assertJson("{'values': [{'value': 'foo'}]}", serialize(bean));
  }

  @Test
  public void serializeMapOfBeansWorks() {
    StringBean stringBean = new StringBean();
    stringBean.value = "foo";

    NestedMapBean bean = new NestedMapBean();
    bean.values = new HashMap<>();
    bean.values.put("key", stringBean);

    assertJson("{'values': {'key': {'value': 'foo'}}}", serialize(bean));
  }

  @Test
  public void beanMapsMustHaveStringKeysForSerializing() {
    StringBean stringBean = new StringBean();
    stringBean.value = "foo";

    final IllegalKeyMapBean bean = new IllegalKeyMapBean();
    bean.values = new HashMap<>();
    bean.values.put(1, stringBean);

    assertExceptionContains(
        "Maps with non-string keys are not supported (found in field 'values')",
        new Runnable() {
          @Override
          public void run() {
            serialize(bean);
          }
        });
  }

  @Test
  public void twoGettersThrows() {
    final TwoGetterBean bean = new TwoGetterBean();
    bean.value = "foo";
    assertExceptionContains(
        "Found conflicting getters",
        new Runnable() {
          @Override
          public void run() {
            serialize(bean);
          }
        });
  }

  @Test
  public void serializeUPPERCASE() {
    XMLAndURLBean bean = new XMLAndURLBean();
    bean.XMLAndURL1 = "foo";
    bean.XMLAndURL2 = "bar";
    assertJson("{'xmlandURL1': 'foo', 'XMLAndURL2': 'bar'}", serialize(bean));
  }

  @Test
  public void onlySerializesGetterWithCorrectArguments() {
    GetterArgumentsBean bean = new GetterArgumentsBean();
    bean.value = "foo";
    assertJson("{'value1': 'foo1', 'value4': 'foo4'}", serialize(bean));
  }

  @Test
  public void roundTripCaseSensitiveFieldBean1() {
    CaseSensitiveFieldBean1 bean = new CaseSensitiveFieldBean1();
    bean.VALUE = "foo";
    assertJson("{'VALUE': 'foo'}", serialize(bean));
    CaseSensitiveFieldBean1 deserialized =
        deserialize("{'VALUE': 'foo'}", CaseSensitiveFieldBean1.class);
    assertEquals("foo", deserialized.VALUE);
  }

  @Test
  public void roundTripCaseSensitiveFieldBean2() {
    CaseSensitiveFieldBean2 bean = new CaseSensitiveFieldBean2();
    bean.value = "foo";
    assertJson("{'value': 'foo'}", serialize(bean));
    CaseSensitiveFieldBean2 deserialized =
        deserialize("{'value': 'foo'}", CaseSensitiveFieldBean2.class);
    assertEquals("foo", deserialized.value);
  }

  @Test
  public void roundTripCaseSensitiveFieldBean3() {
    CaseSensitiveFieldBean3 bean = new CaseSensitiveFieldBean3();
    bean.Value = "foo";
    assertJson("{'Value': 'foo'}", serialize(bean));
    CaseSensitiveFieldBean3 deserialized =
        deserialize("{'Value': 'foo'}", CaseSensitiveFieldBean3.class);
    assertEquals("foo", deserialized.Value);
  }

  @Test
  public void roundTripCaseSensitiveFieldBean4() {
    CaseSensitiveFieldBean4 bean = new CaseSensitiveFieldBean4();
    bean.valUE = "foo";
    assertJson("{'valUE': 'foo'}", serialize(bean));
    CaseSensitiveFieldBean4 deserialized =
        deserialize("{'valUE': 'foo'}", CaseSensitiveFieldBean4.class);
    assertEquals("foo", deserialized.valUE);
  }

  @Test
  public void roundTripUnicodeBean() {
    UnicodeBean bean = new UnicodeBean();
    bean.漢字 = "foo";
    assertJson("{'漢字': 'foo'}", serialize(bean));
    UnicodeBean deserialized = deserialize("{'漢字': 'foo'}", UnicodeBean.class);
    assertEquals("foo", deserialized.漢字);
  }

  @Test
  public void shortsCantBeSerialized() {
    final ShortBean bean = new ShortBean();
    bean.value = 1;
    assertExceptionContains(
        "Numbers of type Short are not supported, please use an int, long, float, double or BigDecimal (found in field 'value')",
        new Runnable() {
          @Override
          public void run() {
            serialize(bean);
          }
        });
  }

  @Test
  public void bytesCantBeSerialized() {
    final ByteBean bean = new ByteBean();
    bean.value = 1;
    assertExceptionContains(
        "Numbers of type Byte are not supported, please use an int, long, float, double or BigDecimal (found in field 'value')",
        new Runnable() {
          @Override
          public void run() {
            serialize(bean);
          }
        });
  }

  @Test
  public void charsCantBeSerialized() {
    final CharBean bean = new CharBean();
    bean.value = 1;
    assertExceptionContains(
        "Characters are not supported, please use Strings (found in field 'value')",
        new Runnable() {
          @Override
          public void run() {
            serialize(bean);
          }
        });
  }

  @Test
  public void intArraysCantBeSerialized() {
    final IntArrayBean bean = new IntArrayBean();
    bean.values = new int[] {1};
    assertExceptionContains(
        "Serializing Arrays is not supported, please use Lists instead "
            + "(found in field 'values')",
        new Runnable() {
          @Override
          public void run() {
            serialize(bean);
          }
        });
  }

  @Test
  public void objectArraysCantBeSerialized() {
    final StringArrayBean bean = new StringArrayBean();
    bean.values = new String[] {"foo"};
    assertExceptionContains(
        "Serializing Arrays is not supported, please use Lists instead "
            + "(found in field 'values')",
        new Runnable() {
          @Override
          public void run() {
            serialize(bean);
          }
        });
  }

  @Test
  public void shortsCantBeDeserialized() {
    assertExceptionContains(
        "Deserializing values to short is not supported (found in field 'value')",
        new Runnable() {
          @Override
          public void run() {
            deserialize("{'value': 1}", ShortBean.class);
          }
        });
  }

  @Test
  public void bytesCantBeDeserialized() {
    assertExceptionContains(
        "Deserializing values to byte is not supported (found in field 'value')",
        new Runnable() {
          @Override
          public void run() {
            deserialize("{'value': 1}", ByteBean.class);
          }
        });
  }

  @Test
  public void charsCantBeDeserialized() {
    assertExceptionContains(
        "Deserializing values to char is not supported (found in field 'value')",
        new Runnable() {
          @Override
          public void run() {
            deserialize("{'value': '1'}", CharBean.class);
          }
        });
  }

  @Test
  public void intArraysCantBeDeserialized() {
    assertExceptionContains(
        "Converting to Arrays is not supported, please use Lists instead (found in field 'values')",
        new Runnable() {
          @Override
          public void run() {
            deserialize("{'values': [1]}", IntArrayBean.class);
          }
        });
  }

  @Test
  public void objectArraysCantBeDeserialized() {
    assertExceptionContains(
        "Could not deserialize object. Converting to Arrays is not supported, please use Lists "
            + "instead (found in field 'values')",
        new Runnable() {
          @Override
          public void run() {
            deserialize("{'values': ['foo']}", StringArrayBean.class);
          }
        });
  }

  @Test
  public void publicConstructorCanBeDeserialized() {
    PublicConstructorBean bean = deserialize("{'value': 'foo'}", PublicConstructorBean.class);
    assertEquals("foo", bean.value);
  }

  @Test
  public void privateConstructorCanBeDeserialized() {
    PrivateConstructorBean bean = deserialize("{'value': 'foo'}", PrivateConstructorBean.class);
    assertEquals("foo", bean.value);
  }

  @Test
  public void argConstructorCantBeDeserialized() {
    assertExceptionContains(
        "Class com.google.cloud.firestore.MapperTest$ArgConstructorBean does not define "
            + "a no-argument constructor.",
        new Runnable() {
          @Override
          public void run() {
            deserialize("{'value': 'foo'}", ArgConstructorBean.class);
          }
        });
  }

  @Test
  public void packageConstructorCanBeDeserialized() {
    PackageConstructorBean bean = deserialize("{'value': 'foo'}", PackageConstructorBean.class);
    assertEquals("foo", bean.value);
  }

  @Test
  public void multipleConstructorsCanBeDeserialized() {
    MultipleConstructorBean bean = deserialize("{'value': 'foo'}", MultipleConstructorBean.class);
    assertEquals("foo", bean.value);
  }

  @Test
  public void objectAcceptsAnyObject() {
    ObjectBean stringValue = deserialize("{'value': 'foo'}", ObjectBean.class);
    assertEquals("foo", stringValue.value);
    ObjectBean listValue = deserialize("{'value': ['foo']}", ObjectBean.class);
    assertEquals(Collections.singletonList("foo"), listValue.value);
    ObjectBean mapValue = deserialize("{'value': {'foo':'bar'}}", ObjectBean.class);
    assertEquals(fromSingleQuotedString("{'foo':'bar'}"), mapValue.value);
    String complex = "{'value': {'foo':['bar', ['baz'], {'bam': 'qux'}]}, 'other':{'a': ['b']}}";
    ObjectBean complexValue = deserialize(complex, ObjectBean.class);
    assertEquals(fromSingleQuotedString(complex).get("value"), complexValue.value);
  }

  @Test
  public void objectClassCanBePassedInAtTopLevel() {
    assertEquals("foo", convertToCustomClass("foo", Object.class));
    assertEquals(1, convertToCustomClass(1, Object.class));
    assertEquals(1L, convertToCustomClass(1L, Object.class));
    assertEquals(true, convertToCustomClass(true, Object.class));
    assertEquals(1.1, convertToCustomClass(1.1, Object.class));
    List<String> fooList = Collections.singletonList("foo");
    assertEquals(fooList, convertToCustomClass(fooList, Object.class));
    Map<String, String> fooMap = Collections.singletonMap("foo", "bar");
    assertEquals(fooMap, convertToCustomClass(fooMap, Object.class));
  }

  @Test
  public void primitiveClassesCanBePassedInTopLevel() {
    assertEquals("foo", convertToCustomClass("foo", String.class));
    assertEquals((Integer) 1, convertToCustomClass(1, Integer.class));
    assertEquals((Long) 1L, convertToCustomClass(1L, Long.class));
    assertEquals(true, convertToCustomClass(true, Boolean.class));
    assertEquals((Double) 1.1, convertToCustomClass(1.1, Double.class));
  }

  @Test
  public void passingInListTopLevelThrows() {
    assertExceptionContains(
        "Class java.util.List has generic type parameters, please use GenericTypeIndicator "
            + "instead",
        new Runnable() {
          @Override
          public void run() {
            convertToCustomClass(Collections.singletonList("foo"), List.class);
          }
        });
  }

  @Test
  public void passingInMapTopLevelThrows() {
    assertExceptionContains(
        "Class java.util.Map has generic type parameters, please use GenericTypeIndicator "
            + "instead",
        new Runnable() {
          @Override
          public void run() {
            convertToCustomClass(Collections.singletonMap("foo", "bar"), Map.class);
          }
        });
  }

  @Test
  public void passingInCharacterTopLevelThrows() {
    assertExceptionContains(
        "Deserializing values to Character is not supported",
        new Runnable() {
          @Override
          public void run() {
            convertToCustomClass('1', Character.class);
          }
        });
  }

  @Test
  public void passingInShortTopLevelThrows() {
    assertExceptionContains(
        "Deserializing values to Short is not supported",
        new Runnable() {
          @Override
          public void run() {
            convertToCustomClass(1, Short.class);
          }
        });
  }

  @Test
  public void passingInByteTopLevelThrows() {
    assertExceptionContains(
        "Deserializing values to Byte is not supported",
        new Runnable() {
          @Override
          public void run() {
            convertToCustomClass(1, Byte.class);
          }
        });
  }

  @Test
  public void passingInGenericBeanTopLevelThrows() {
    assertExceptionContains(
        "Class com.google.cloud.firestore.MapperTest$GenericBean has generic type "
            + "parameters, please use GenericTypeIndicator instead",
        new Runnable() {
          @Override
          public void run() {
            deserialize("{'value': 'foo'}", GenericBean.class);
          }
        });
  }

  @Test
  public void collectionsCanBeSerializedWhenList() {
    CollectionBean bean = new CollectionBean();
    bean.values = Collections.singletonList("foo");
    assertJson("{'values': ['foo']}", serialize(bean));
  }

  @Test
  public void collectionsCantBeSerializedWhenSet() {
    final CollectionBean bean = new CollectionBean();
    bean.values = Collections.singleton("foo");
    assertExceptionContains(
        "Serializing Collections is not supported, please use Lists instead "
            + "(found in field 'values')",
        new Runnable() {
          @Override
          public void run() {
            serialize(bean);
          }
        });
  }

  @Test
  public void collectionsCantBeDeserialized() {
    assertExceptionContains(
        "Collections are not supported, please use Lists instead (found in field 'values')",
        new Runnable() {
          @Override
          public void run() {
            deserialize("{'values': ['foo']}", CollectionBean.class);
          }
        });
  }

  @Test
  public void allowNullEverywhere() {
    assertNull(convertToCustomClass(null, Integer.class));
    assertNull(convertToCustomClass(null, String.class));
    assertNull(convertToCustomClass(null, Double.class));
    assertNull(convertToCustomClass(null, Long.class));
    assertNull(convertToCustomClass(null, Boolean.class));
    assertNull(convertToCustomClass(null, StringBean.class));
    assertNull(convertToCustomClass(null, Object.class));
  }

  @Test
  public void serializingGenericBeansSupported() {
    GenericBean<String> stringBean = new GenericBean<>();
    stringBean.value = "foo";
    assertJson("{'value': 'foo'}", serialize(stringBean));

    GenericBean<Map<String, String>> mapBean = new GenericBean<>();
    mapBean.value = Collections.singletonMap("foo", "bar");
    assertJson("{'value': {'foo': 'bar'}}", serialize(mapBean));

    GenericBean<List<String>> listBean = new GenericBean<>();
    listBean.value = Collections.singletonList("foo");
    assertJson("{'value': ['foo']}", serialize(listBean));

    GenericBean<GenericBean<String>> recursiveBean = new GenericBean<>();
    recursiveBean.value = new GenericBean<>();
    recursiveBean.value.value = "foo";
    assertJson("{'value': {'value': 'foo'}}", serialize(recursiveBean));

    DoubleGenericBean<String, Integer> doubleBean = new DoubleGenericBean<>();
    doubleBean.valueA = "foo";
    doubleBean.valueB = 1;
    assertJson("{'valueA': 'foo', 'valueB': 1}", serialize(doubleBean));
  }

  @Test
  public void deserializingWrongTypeThrows() {
    assertExceptionContains(
        "Failed to convert a value of type java.lang.String to int (found in field 'value')",
        new Runnable() {
          @Override
          public void run() {
            deserialize("{'value': 'foo'}", WrongTypeBean.class);
          }
        });
  }

  @Test
  public void serializingWrongTypeWorks() {
    WrongTypeBean bean = new WrongTypeBean();
    bean.value = 1;
    assertJson("{'value': '1'}", serialize(bean));
  }

  @Test
  public void excludedFieldsAreExcluded() {
    ExcludedBean bean = new ExcludedBean();
    assertJson("{'includedGetter': 'no-value'}", serialize(bean));
  }

  @Test
  public void excludedFieldsAreNotParsed() {
    ExcludedBean bean =
        deserialize(
            "{'includedGetter': 'foo', 'excludedField': 'bar', 'excludedGetter': 'qux'}",
            ExcludedBean.class);
    assertEquals("no-value", bean.excludedField);
    assertEquals("no-value", bean.excludedGetter);
    assertEquals("foo", bean.includedGetter);
  }

  @Test
  public void excludedSettersAreIgnored() {
    ExcludedSetterBean bean = deserialize("{'value': 'foo'}", ExcludedSetterBean.class);
    assertEquals("foo", bean.value);
  }

  @Test
  public void propertyNamesAreSerialized() {
    PropertyNameBean bean = new PropertyNameBean();
    bean.key = "foo";
    bean.setValue("bar");

    assertJson("{'my_key': 'foo', 'my_value': 'bar'}", serialize(bean));
  }

  @Test
  public void propertyNamesAreParsed() {
    PropertyNameBean bean =
        deserialize("{'my_key': 'foo', 'my_value': 'bar'}", PropertyNameBean.class);
    assertEquals("foo", bean.key);
    assertEquals("bar", bean.getValue());
  }

  @Test
  public void staticFieldsAreNotParsed() {
    StaticFieldBean bean = deserialize("{'value1': 'foo', 'value2': 'bar'}", StaticFieldBean.class);
    assertEquals("static-value", StaticFieldBean.value1);
    assertEquals("bar", bean.value2);
  }

  @Test
  public void staticFieldsAreNotSerialized() {
    StaticFieldBean bean = new StaticFieldBean();
    bean.value2 = "foo";
    assertJson("{'value2': 'foo'}", serialize(bean));
  }

  @Test
  public void staticSettersAreNotUsed() {
    StaticMethodBean bean =
        deserialize("{'value1': 'foo', 'value2': 'bar'}", StaticMethodBean.class);
    assertEquals("static-value", StaticMethodBean.value1);
    assertEquals("bar", bean.value2);
  }

  @Test
  public void staticMethodsAreNotSerialized() {
    StaticMethodBean bean = new StaticMethodBean();
    bean.value2 = "foo";
    assertJson("{'value2': 'foo'}", serialize(bean));
  }

  @Test
  public void enumsAreSerialized() {
    EnumBean bean = new EnumBean();
    bean.enumField = SimpleEnum.Bar;
    bean.complexEnum = ComplexEnum.One;
    bean.enumUsingPropertyName = ComplexEnum.THREE;
    bean.pathologicalEnum = PathologicalEnum.One;
    bean.setEnumValue(SimpleEnum.Foo);
    assertJson(
        "{'enumField': 'Bar', 'enumValue': 'Foo', 'complexEnum': 'One', 'enumUsingPropertyName': 'Three', 'pathologicalEnum': 'Two'}",
        serialize(bean));
  }

  @Test
  public void enumsAreParsed() {
    String json =
        "{'enumField': 'Bar', 'enumValue': 'Foo', 'complexEnum': 'One', 'enumUsingPropertyName': 'Three', 'pathologicalEnum': 'Two'}";
    EnumBean bean = deserialize(json, EnumBean.class);
    assertEquals(bean.enumField, SimpleEnum.Bar);
    assertEquals(bean.enumValue, SimpleEnum.Foo);
    assertEquals(bean.complexEnum, ComplexEnum.One);
    assertEquals(bean.enumUsingPropertyName, ComplexEnum.THREE);
    assertEquals(bean.pathologicalEnum, PathologicalEnum.One);
  }

  @Test
  public void enumsCanBeParsedToNull() {
    String json = "{'enumField': null}";
    EnumBean bean = deserialize(json, EnumBean.class);
    assertNull(bean.enumField);
    assertNull(bean.enumValue);
    assertNull(bean.complexEnum);
  }

  @Test
  public void throwsOnUnmatchedEnums() {
    final String json = "{'enumField': 'Unavailable', 'enumValue': 'Foo', 'complexEnum': 'One'}";
    assertExceptionContains(
        "Could not find enum value of com.google.cloud.firestore.MapperTest$SimpleEnum for "
            + "value \"Unavailable\" (found in field 'enumField')",
        new Runnable() {
          @Override
          public void run() {
            deserialize(json, EnumBean.class);
          }
        });
  }

  @Test
  public void inheritedFieldsAndGettersAreSerialized() {
    FinalBean bean = new FinalBean();
    bean.finalValue = "final-value";
    bean.inheritedValue = "inherited-value";
    bean.baseValue = "base-value";
    bean.overrideValue = "override-value";
    bean.classPrivateValue = "private-value";
    bean.packageBaseValue = "package-base-value";
    bean.setFinalMethod("final-method");
    bean.setInheritedMethod("inherited-method");
    bean.setBaseMethod("base-method");
    assertJson(
        "{'baseValue': 'base-value', "
            + "'baseMethod': 'base-method', "
            + "'classPrivateValue': 'private-value', "
            + "'finalMethod': 'final-method', "
            + "'finalValue': 'final-value', "
            + "'inheritedMethod': 'inherited-method', "
            + "'inheritedValue': 'inherited-value', "
            + "'overrideValue': 'override-value-final', "
            + "'packageBaseValue': 'package-base-value'}",
        serialize(bean));
  }

  @Test
  public void inheritedFieldsAndSettersAreParsed() {
    String bean =
        "{'baseValue': 'base-value', "
            + "'baseMethod': 'base-method', "
            + "'classPrivateValue': 'private-value', "
            + "'finalMethod': 'final-method', "
            + "'finalValue': 'final-value', "
            + "'inheritedMethod': 'inherited-method', "
            + "'inheritedValue': 'inherited-value', "
            + "'overrideValue': 'override-value', "
            + "'packageBaseValue': 'package-base-value'}";
    FinalBean finalBean = deserialize(bean, FinalBean.class);
    assertEquals("base-value", finalBean.baseValue);
    assertEquals("inherited-value", finalBean.inheritedValue);
    assertEquals("final-value", finalBean.finalValue);
    assertEquals("base-method", finalBean.getBaseMethod());
    assertEquals("inherited-method", finalBean.getInheritedMethod());
    assertEquals("final-method", finalBean.getFinalMethod());
    assertEquals("override-value-final", finalBean.overrideValue);
    assertEquals("private-value", finalBean.classPrivateValue);
    assertNull(((InheritedBean) finalBean).classPrivateValue);
    assertNull(((BaseBean) finalBean).classPrivateValue);

    InheritedBean inheritedBean = deserialize(bean, InheritedBean.class);
    assertEquals("base-value", inheritedBean.baseValue);
    assertEquals("inherited-value", inheritedBean.inheritedValue);
    assertEquals("base-method", inheritedBean.getBaseMethod());
    assertEquals("inherited-method", inheritedBean.getInheritedMethod());
    assertEquals("override-value-inherited", inheritedBean.overrideValue);
    assertEquals("private-value", inheritedBean.classPrivateValue);
    assertNull(((BaseBean) inheritedBean).classPrivateValue);

    BaseBean baseBean = deserialize(bean, BaseBean.class);
    assertEquals("base-value", baseBean.baseValue);
    assertEquals("base-method", baseBean.getBaseMethod());
    assertEquals("override-value", baseBean.overrideValue);
    assertEquals("private-value", baseBean.classPrivateValue);
  }

  @Test
  public void settersFromSubclassConflictsWithBaseClass() {
    final ConflictingSetterSubBean bean = new ConflictingSetterSubBean();
    bean.value = 1;
    assertExceptionContains(
        "Found conflicting setters with name: setValue (conflicts with setValue defined on "
            + "com.google.cloud.firestore.MapperTest$ConflictingSetterSubBean)",
        new Runnable() {
          @Override
          public void run() {
            serialize(bean);
          }
        });
  }

  @Test
  public void settersFromSubclassConflictsWithBaseClass2() {
    final ConflictingSetterSubBean2 bean = new ConflictingSetterSubBean2();
    bean.value = 1;
    assertExceptionContains(
        "Found conflicting setters with name: setValue (conflicts with setValue defined on "
            + "com.google.cloud.firestore.MapperTest$ConflictingSetterSubBean2)",
        new Runnable() {
          @Override
          public void run() {
            serialize(bean);
          }
        });
  }

  @Test
  public void settersCanOverridePrimitiveSettersSerializing() {
    NonConflictingSetterSubBean bean = new NonConflictingSetterSubBean();
    bean.value = 1;
    assertJson("{'value': 1}", serialize(bean));
  }

  @Test
  public void settersCanOverridePrimitiveSettersParsing() {
    NonConflictingSetterSubBean bean =
        deserialize("{'value': 2}", NonConflictingSetterSubBean.class);
    // sub-bean converts to negative value
    assertEquals(-2, bean.value);
  }

  @Test
  public void genericSettersFromSubclassConflictsWithBaseClass() {
    final ConflictingGenericSetterSubBean<String> bean = new ConflictingGenericSetterSubBean<>();
    bean.value = "hello";
    assertExceptionContains(
        "Found conflicting setters with name: setValue (conflicts with setValue defined on "
            + "com.google.cloud.firestore.MapperTest$ConflictingGenericSetterSubBean)",
        new Runnable() {
          @Override
          public void run() {
            serialize(bean);
          }
        });
  }

  // This should work, but generics and subclassing are tricky to get right. For now we will just
  // throw and we can add support for generics & subclassing if it becomes a high demand feature
  @Test
  public void settersCanOverrideGenericSettersParsingNot() {
    assertExceptionContains(
        "Class com.google.cloud.firestore.MapperTest$NonConflictingGenericSetterSubBean "
            + "has multiple setter overloads",
        new Runnable() {
          @Override
          public void run() {
            NonConflictingGenericSetterSubBean bean =
                deserialize("{'value': 'value'}", NonConflictingGenericSetterSubBean.class);
            assertEquals("subsetter:value", bean.value);
          }
        });
  }

  @Test
  public void serializingRecursiveBeanThrows() {
    final ObjectBean bean = new ObjectBean();
    bean.value = bean;
    // It's sufficient to verify it's a RuntimeException since StackOverflowException is not.
    assertExceptionContains(
        "Exceeded maximum depth of 500, which likely indicates there's an object cycle",
        new Runnable() {
          @Override
          public void run() {
            serialize(bean);
          }
        });
  }

  @Test
  public void serializationFailureIncludesPath() {
    ObjectBean innerBean = new ObjectBean();
    // Shorts aren't supported, so this should fail.
    innerBean.value = Collections.singletonMap("short", (short) 1);
    ObjectBean outerBean = new ObjectBean();
    outerBean.value = Collections.singletonMap("inner", innerBean);
    try {
      serialize(outerBean);
      fail("should have thrown");
    } catch (RuntimeException e) {
      assertEquals(
          "Could not serialize object. Numbers of type Short are not supported, please use an int, "
              + "long, float, double or BigDecimal (found in field 'value.inner.value.short')",
          e.getMessage());
    }
  }

  @Test
  public void deserializationFailureIncludesPath() {
    Object serialized = Collections.singletonMap("value", (short) 1);

    try {
      convertToCustomClass(serialized, ShortBean.class);
      fail("should have thrown");
    } catch (RuntimeException e) {
      assertEquals(
          "Could not deserialize object. Deserializing values to short is not supported "
              + "(found in field 'value')",
          e.getMessage());
    }
  }

  // Bean definitions with @DocumentId applied to wrong type.
  private static class FieldWithDocumentIdOnWrongTypeBean {
    @DocumentId public Integer intField;
  }

  private static class GetterWithDocumentIdOnWrongTypeBean {
    private int intField = 100;

    @DocumentId
    public int getIntField() {
      return intField;
    }
  }

  private static class PropertyWithDocumentIdOnWrongTypeBean {
    @PropertyName("intField")
    @DocumentId
    public int intField = 100;
  }

  @Test
  public void documentIdAnnotateWrongTypeThrows() {
    final String expectedErrorMessage = "instead of String or DocumentReference";
    assertExceptionContains(
        expectedErrorMessage,
        new Runnable() {
          @Override
          public void run() {
            serialize(new FieldWithDocumentIdOnWrongTypeBean());
          }
        });
    assertExceptionContains(
        expectedErrorMessage,
        new Runnable() {
          @Override
          public void run() {
            deserialize("{'intField': 1}", FieldWithDocumentIdOnWrongTypeBean.class);
          }
        });

    assertExceptionContains(
        expectedErrorMessage,
        new Runnable() {
          @Override
          public void run() {
            serialize(new GetterWithDocumentIdOnWrongTypeBean());
          }
        });
    assertExceptionContains(
        expectedErrorMessage,
        new Runnable() {
          @Override
          public void run() {
            deserialize("{'intField': 1}", GetterWithDocumentIdOnWrongTypeBean.class);
          }
        });

    assertExceptionContains(
        expectedErrorMessage,
        new Runnable() {
          @Override
          public void run() {
            serialize(new PropertyWithDocumentIdOnWrongTypeBean());
          }
        });
    assertExceptionContains(
        expectedErrorMessage,
        new Runnable() {
          @Override
          public void run() {
            deserialize("{'intField': 1}", PropertyWithDocumentIdOnWrongTypeBean.class);
          }
        });
  }

  private static class GetterWithoutBackingFieldOnDocumentIdBean {
    @DocumentId
    public String getDocId() {
      return "doc-id";
    }
  }

  @Test
  public void documentIdAnnotateReadOnlyThrows() {
    final String expectedErrorMessage = "but no field or public setter was found";
    // Serialization.
    final GetterWithoutBackingFieldOnDocumentIdBean bean =
        new GetterWithoutBackingFieldOnDocumentIdBean();
    assertExceptionContains(
        expectedErrorMessage,
        new Runnable() {
          @Override
          public void run() {
            serialize(bean);
          }
        });

    // Deserialization.
    assertExceptionContains(
        expectedErrorMessage,
        new Runnable() {
          @Override
          public void run() {
            deserialize("{'docId': 'id'}", GetterWithoutBackingFieldOnDocumentIdBean.class);
          }
        });
  }

  private static class DocumentIdOnStringField {
    @DocumentId public String docId = "doc-id";
  }

  private static class DocumentIdOnStringFieldAsProperty {
    @PropertyName("docIdProperty")
    @DocumentId
    public String docId = "doc-id";

    @PropertyName("anotherProperty")
    public int someOtherProperty = 0;
  }

  private static class DocumentIdOnDocRefGetter {
    private DocumentReference docRef;

    @DocumentId
    public DocumentReference getDocRef() {
      return docRef;
    }

    public void setDocRef(DocumentReference ref) {
      docRef = ref;
    }
  }

  private static class DocumentIdOnInheritedDocRefSetter extends DocumentIdOnDocRefGetter {

    private DocumentReference inheritedDocRef;

    @DocumentId
    public DocumentReference getInheritedDocRef() {
      return inheritedDocRef;
    }

    public void setInheritedDocRef(DocumentReference ref) {
      inheritedDocRef = ref;
    }
  }

  private static class DocumentIdOnNestedObjects {
    @PropertyName("nestedDocIdHolder")
    public DocumentIdOnStringField nestedDocIdHolder;
  }

  @Test
  public void documentIdsDeserialize() {
    DocumentReference ref =
        new DocumentReference(
            firestoreMock,
            ResourcePath.create(
                DatabaseRootName.of("test-project", "(default)"),
                ImmutableList.of("coll", "doc123")));

    assertEquals("doc123", deserialize("{}", DocumentIdOnStringField.class, ref).docId);

    DocumentIdOnStringFieldAsProperty target =
        deserialize("{'anotherProperty': 100}", DocumentIdOnStringFieldAsProperty.class, ref);
    assertEquals("doc123", target.docId);
    assertEquals(100, target.someOtherProperty);

    assertEquals(ref, deserialize("{}", DocumentIdOnDocRefGetter.class, ref).getDocRef());

    DocumentIdOnInheritedDocRefSetter target1 =
        deserialize("{}", DocumentIdOnInheritedDocRefSetter.class, ref);
    assertEquals(ref, target1.getInheritedDocRef());
    assertEquals(ref, target1.getDocRef());

    assertEquals(
        "doc123",
        deserialize("{'nestedDocIdHolder': {}}", DocumentIdOnNestedObjects.class, ref)
            .nestedDocIdHolder
            .docId);
  }

  @Test
  public void documentIdsRoundTrip() {
    // Implicitly verifies @DocumentId is ignored during serialization.

    final DocumentReference ref =
        new DocumentReference(
            firestoreMock,
            ResourcePath.create(
                DatabaseRootName.of("test-project", "(default)"),
                ImmutableList.of("coll", "doc123")));

    assertEquals(
        Collections.emptyMap(), serialize(deserialize("{}", DocumentIdOnStringField.class, ref)));

    assertEquals(
        Collections.singletonMap("anotherProperty", 100),
        serialize(
            deserialize("{'anotherProperty': 100}", DocumentIdOnStringFieldAsProperty.class, ref)));

    assertEquals(
        Collections.emptyMap(), serialize(deserialize("{}", DocumentIdOnDocRefGetter.class, ref)));

    assertEquals(
        Collections.emptyMap(),
        serialize(deserialize("{}", DocumentIdOnInheritedDocRefSetter.class, ref)));

    assertEquals(
        Collections.singletonMap("nestedDocIdHolder", Collections.emptyMap()),
        serialize(deserialize("{'nestedDocIdHolder': {}}", DocumentIdOnNestedObjects.class, ref)));
  }

  @Test
  public void documentIdsDeserializeConflictThrows() {
    final String expectedErrorMessage = "cannot apply @DocumentId on this property";
    final DocumentReference ref =
        new DocumentReference(
            firestoreMock,
            ResourcePath.create(
                DatabaseRootName.of("test-project", "(default)"),
                ImmutableList.of("coll", "doc123")));

    assertExceptionContains(
        expectedErrorMessage,
        new Runnable() {
          @Override
          public void run() {
            deserialize("{'docId': 'toBeOverwritten'}", DocumentIdOnStringField.class, ref);
          }
        });

    assertExceptionContains(
        expectedErrorMessage,
        new Runnable() {
          @Override
          public void run() {
            deserialize(
                "{'docIdProperty': 'toBeOverwritten', 'anotherProperty': 100}",
                DocumentIdOnStringFieldAsProperty.class,
                ref);
          }
        });

    assertExceptionContains(
        expectedErrorMessage,
        new Runnable() {
          @Override
          public void run() {
            deserialize(
                "{'nestedDocIdHolder': {'docId': 'toBeOverwritten'}}",
                DocumentIdOnNestedObjects.class,
                ref);
          }
        });
  }
}
