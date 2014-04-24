package org.bober.avaya_monitoring.model.helper;

import java.util.List;

/**
 * Class consist few math methods
 */
public class MathHelper {

    /**
     * Return sum of all list entries
     * @return sum of all list entries
     */
    public static Integer getListSum(List<Integer> list){
        Integer result = null;

        if (list.size()>0){
            result = 0;
            for (Integer value : list) {
                result += value;
            }
        }

        return result;
    }

}
