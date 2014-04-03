package org.bober.avaya_monitoring.model.helper;

import org.springframework.jdbc.core.BeanPropertyRowMapper;

import java.beans.PropertyDescriptor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Extends BeanPropertyRowMapper to allow for boolean fields can be
 * mapped to 'true,'false' type column during sql request.
 *
 * Using stock BeanPropertyRowMapper would throw a SQLException.
 */
public class ExtendedBeanPropertyRowMapper<T> extends BeanPropertyRowMapper<T> {

    //Contains valid true values
    public static final Set<String> TRUE_SET =
            new HashSet<String>(Arrays.asList("y", "yes", "true"));

    public ExtendedBeanPropertyRowMapper(Class<T> class1) {
        super(class1);
    }

    @Override
    /**
     * Override <code>getColumnValue</code> to add ability to map 'Y','N'
     * type columns to boolean properties.
     *
     * @param rs is the ResultSet holding the data
     * @param index is the column index
     * @param pd the bean property that each result object is expected to match
     * (or <code>null</code> if none specified)
     * @return the Object value
     * @throws java.sql.SQLException in case of extraction failure
     * @see org.springframework.jdbc.core.BeanPropertyRowMapper#getColumnValue(java.sql.ResultSet, int, java.beans.PropertyDescriptor)
     */
    protected Object getColumnValue(ResultSet rs, int index, PropertyDescriptor pd) throws SQLException {
        Class<?> requiredType = pd.getPropertyType();
        if (boolean.class.equals(requiredType) || Boolean.class.equals(requiredType)) {
            String stringValue = rs.getString(index);
            if(stringValue!=null && !stringValue.equals("") &&
                    TRUE_SET.contains(stringValue.toLowerCase())){
                return true;
            }
            else return false;
        }
        return super.getColumnValue(rs, index, pd);
    }
}