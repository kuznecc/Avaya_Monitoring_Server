package org.bober.avaya_monitoring.model.dao;


import org.bober.avaya_monitoring.model.entity.CheckResult;

import java.util.Date;
import java.util.List;

/**
 * Common interface for all CheckResult tables dao
 */
public interface iCheckResultDao extends iAbstractDao<CheckResult> {
    List<CheckResult> get(Date startDate, Date endDate);
}
