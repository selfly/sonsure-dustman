/*
 * Copyright 2002-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sonsure.dustman.springjdbc.persist;

import com.sonsure.dustman.jdbc.config.JdbcContext;
import com.sonsure.dustman.jdbc.mapping.MappingHandler;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.*;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.beans.PropertyDescriptor;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

/**
 * 重写了spring的{@link BeanPropertyRowMapper}.
 * 在原来的基础上添加了自定义映射以及注解的支持
 *
 * @author Thomas Risberg
 * @author Juergen Hoeller
 * @since 2.5
 */
public class JdbcRowMapper<T> implements RowMapper<T> {

    /**
     * Logger available to subclasses
     */
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final JdbcContext jdbcContext;

    /**
     * The class we are mapping to
     */
    private final Class<T> mappedClass;

    /**
     * Map of the fields we provide mapping for
     */
    private final Map<String, PropertyDescriptor> mappedFields;

    /**
     * Set of bean properties we provide mapping for
     */
    private final Set<String> mappedProperties;

    /**
     * Whether we're strictly validating
     * -- GETTER --
     * Return whether we're strictly validating that all bean properties have been
     * mapped from corresponding database fields.
     */
    @Getter
    @Setter
    private boolean checkFullyPopulated = false;

    /**
     * Whether we're defaulting primitives when mapping a null value
     */
    @Getter
    @Setter
    private boolean primitivesDefaultedForNullValue = false;

    /**
     * Create a new {@code BeanPropertyRowMapper}, accepting unpopulated
     * properties in the target bean.
     * <p>Consider using the {@link #newInstance} factory method instead,
     * which allows for specifying the mapped type once only.
     *
     * @param jdbcContext the jdbc context
     * @param mappedClass the class that each row should be mapped to
     */
    public JdbcRowMapper(JdbcContext jdbcContext, Class<T> mappedClass) {
        this.jdbcContext = jdbcContext;
        this.mappedClass = mappedClass;
        this.mappedFields = new HashMap<>(32);
        this.mappedProperties = new HashSet<>(32);
        initialize();
    }

    /**
     * Initialize the mapping metadata for the given class.
     */
    protected void initialize() {
        PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(mappedClass);
        for (PropertyDescriptor pd : pds) {
            if (pd.getWriteMethod() != null) {
                this.mappedFields.put(lowerCaseName(pd.getName()), pd);
                this.mappedProperties.add(pd.getName());
            }
        }
    }

    /**
     * Convert the given name to lower case.
     * By default, conversions will happen within the US locale.
     *
     * @param name the original name
     * @return the converted name
     * @since 4.2
     */
    protected String lowerCaseName(String name) {
        return name.toLowerCase(Locale.US);
    }

    /**
     * Extract the values for all columns in the current row.
     * <p>Utilizes public setters and result set metadata.
     *
     * @see ResultSetMetaData
     */
    @Override
    public T mapRow(ResultSet rs, int rowNumber) throws SQLException {
        Assert.state(this.mappedClass != null, "Mapped class was not specified");
        T mappedObject = BeanUtils.instantiateClass(this.mappedClass);
        BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(mappedObject);

        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();
        Set<String> populatedProperties = (isCheckFullyPopulated() ? new HashSet<>() : null);
        final MappingHandler mappingHandler = this.jdbcContext.getMappingHandler();
        for (int index = 1; index <= columnCount; index++) {
            String column = JdbcUtils.lookupColumnName(rsmd, index);
            String field = column.replace(" ", "");
            field = lowerCaseName(mappingHandler.getField(this.mappedClass, field));
            PropertyDescriptor pd = this.mappedFields.get(field);
            if (pd != null) {
                try {
                    Object value = getColumnValue(rs, index, pd);
                    if (rowNumber == 0 && logger.isDebugEnabled()) {
                        logger.debug("Mapping column '{}' to property '{}' of type [{}]", column, pd.getName(), ClassUtils.getQualifiedName(pd.getPropertyType()));
                    }
                    try {
                        bw.setPropertyValue(pd.getName(), value);
                    } catch (TypeMismatchException ex) {
                        if (value == null && this.primitivesDefaultedForNullValue) {
                            if (logger.isDebugEnabled()) {
                                logger.debug("Intercepted TypeMismatchException for row {} and column '{}' with null value when setting property '{}' of type [{}] on object: {}", rowNumber, column, pd.getName(), ClassUtils.getQualifiedName(pd.getPropertyType()), mappedObject, ex);
                            }
                        } else {
                            throw ex;
                        }
                    }
                    if (populatedProperties != null) {
                        populatedProperties.add(pd.getName());
                    }
                } catch (NotWritablePropertyException ex) {
                    throw new DataRetrievalFailureException("Unable to map column '" + column + "' to property '"
                            + pd.getName() + "'", ex);
                }
            } else {
                // No PropertyDescriptor found
                if (rowNumber == 0 && logger.isDebugEnabled()) {
                    logger.debug("No property found for column '{}' mapped to field '{}'", column, field);
                }
            }
        }

        if (populatedProperties != null && !populatedProperties.equals(this.mappedProperties)) {
            throw new InvalidDataAccessApiUsageException("Given ResultSet does not contain all fields "
                    + "necessary to populate object of class ["
                    + this.mappedClass.getName() + "]: " + this.mappedProperties);
        }

        return mappedObject;
    }

    /**
     * Retrieve a JDBC object value for the specified column.
     * <p>The default implementation calls
     * {@link JdbcUtils#getResultSetValue(ResultSet, int, Class)}.
     * Subclasses may override this to check specific value types upfront,
     * or to post-process values return from {@code getResultSetValue}.
     *
     * @param rs    is the ResultSet holding the data
     * @param index is the column index
     * @param pd    the bean property that each result object is expected to match
     *              (or {@code null} if none specified)
     * @return the Object value
     * @throws SQLException in case of extraction failure
     * @see org.springframework.jdbc.support.JdbcUtils#getResultSetValue(ResultSet, int, Class)
     */
    protected Object getColumnValue(ResultSet rs, int index, PropertyDescriptor pd) throws SQLException {
        return JdbcUtils.getResultSetValue(rs, index, pd.getPropertyType());
    }

    /**
     * Static factory method to create a new {@code BeanPropertyRowMapper}
     * (with the mapped class specified only once).
     *
     * @param <T>         the type parameter
     * @param jdbcContext the jdbc context
     * @param mappedClass the class that each row should be mapped to
     * @return the jdbc row mapper
     */
    public static <T> JdbcRowMapper<T> newInstance(JdbcContext jdbcContext, Class<T> mappedClass) {
        return new JdbcRowMapper<>(jdbcContext, mappedClass);
    }

}
