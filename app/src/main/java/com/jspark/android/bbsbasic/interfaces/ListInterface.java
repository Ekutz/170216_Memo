package com.jspark.android.bbsbasic.interfaces;

import java.sql.SQLException;

/**
 * Created by jsPark on 2017. 2. 14..
 */

public interface ListInterface {
    public void goDetail();
    public void goDetail(long position) throws SQLException;
    public void delete(int position) throws SQLException;
}
