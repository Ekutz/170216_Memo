package com.jspark.android.bbsbasic;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.jspark.android.bbsbasic.data.DBHelper;
import com.jspark.android.bbsbasic.domain.Memo;
import com.jspark.android.bbsbasic.interfaces.DetailInterface;
import com.jspark.android.bbsbasic.interfaces.ListInterface;
import com.jspark.android.bbsbasic.permission.PermissionControl;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ListInterface, DetailInterface {

    public static final int REQ_PERM = 100;
    public static final int REQ_CAMERA = 101;
    public static final int REQ_GALLERY = 102;

    ListFragment list;
    DetailFragment detail;

    FrameLayout main;

    FragmentManager fm;

    List<Memo> datas;
    Dao<Memo, Long> memoDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setFragment();
        try {
            loadData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        list.setData(datas);
        checkPermission();
    }

    private void loadData() throws SQLException {
        DBHelper dbHelper = OpenHelperManager.getHelper(this, DBHelper.class);
        memoDao = dbHelper.getMemoDao();
        datas = memoDao.queryForAll();
    }


    private void setList() {
        getFm();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.activity_main, list);
        ft.commit();
    }

    @Override
    public void goDetail() {
        detail = DetailFragment.newInstance();
        getFm();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.activity_main, detail);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void goDetail(long position) throws SQLException {
        Bundle bundle = new Bundle();
        getFm();
        FragmentTransaction ft = fm.beginTransaction();
        bundle.putInt("id", memoDao.queryForId(position).getId());
        bundle.putString("img",memoDao.queryForId(position).getImg());
        bundle.putString("title",memoDao.queryForId(position).getTitle());
        bundle.putString("content",memoDao.queryForId(position).getMemo());
        bundle.putString("date", String.valueOf(memoDao.queryForId(position).getDate()));
        detail.setArguments(bundle);
        Log.w("title", memoDao.queryForId(position).getTitle());
        Log.w("content", memoDao.queryForId(position).getMemo());
        ft.add(R.id.activity_main, detail);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void delete(int position) throws SQLException {
        Long positionL = (long)position;
        memoDao.delete(memoDao.queryForId(positionL));

        loadData();
        list.setData(datas);
        list.refresh();
    }

    @Override
    public void backToList() {
        super.onBackPressed();
    }

    @Override
    public void saveToList(Memo memo) throws SQLException {
        DBHelper dbHelper = OpenHelperManager.getHelper(this, DBHelper.class);
        memoDao = dbHelper.getMemoDao();
        memoDao.create(memo);

        loadData();
        list.setData(datas);
        super.onBackPressed();
        list.refresh();
    }

    @Override
    public void update(long position, String imgUri, String strTitle, String strContent, Date newTime) throws SQLException {
        Memo memoTemp = memoDao.queryForId(position);
        memoTemp.setImg(imgUri);
        memoTemp.setTitle(strTitle);
        memoTemp.setMemo(strContent);
        memoTemp.setDate(newTime);
        memoDao.update(memoTemp);

        loadData();
        list.setData(datas);
        super.onBackPressed();
        list.refresh();
    }

    @Override
    public void delete(long position) throws SQLException {
        memoDao.delete(memoDao.queryForId(position));

        loadData();
        list.setData(datas);
        super.onBackPressed();
        list.refresh();
    }

    private void setFragment() {
        list = ListFragment.newInstance(1);
        detail = DetailFragment.newInstance();
        main = (FrameLayout)findViewById(R.id.activity_main);
    }

    private void checkPermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean permBool = PermissionControl.checkPermission(this, REQ_PERM);
            if(permBool) {
                setList();
            }
        } else {
            setList();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==REQ_PERM) {
            if(PermissionControl.onCheckResult(grantResults)) {
                setList();
            } else {
                Toast.makeText(this, "어플 재시작 후 권한 설정에 동의해주세요", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void getFm() {
        fm = getSupportFragmentManager();
    }

}
