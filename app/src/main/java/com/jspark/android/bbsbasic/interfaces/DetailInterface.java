package com.jspark.android.bbsbasic.interfaces;

import com.jspark.android.bbsbasic.domain.Memo;

import java.sql.SQLException;
import java.util.Date;

/**
 * Created by jsPark on 2017. 2. 14..
 */

public interface DetailInterface {
    public void backToList();
    public void saveToList(Memo memo) throws SQLException;
    public void update(long position, String imgUri, String strTitle, String strContent, Date newTime) throws SQLException;
    public void delete(long position) throws SQLException;
}
