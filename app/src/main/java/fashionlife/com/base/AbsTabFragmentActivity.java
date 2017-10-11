package fashionlife.com.base;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import fashionlife.com.manager.FLFragmentManager;
import fashionlife.com.manager.FragmentRecord;
import fashionlife.com.util.Tool;


/**
 * Created by lovexujh on 2017/10/9
 */

public abstract class AbsTabFragmentActivity extends BaseActivity {

    private ArrayList<AbsBaseFragment> mFragmentList;
    protected int mFragmentIndex = -1;
    protected String mFragmentName = "";
    private Map<String, Integer> mFragmentNameMap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFragment();
    }

    private void initFragment() {
        if (getLayoutId() <= 0) {
            throw new IllegalArgumentException("error layout id !");
        }
        mFragmentList = new ArrayList<>();
        mFragmentNameMap = new HashMap<>();
        for (int i = 0; i < getFragments().length; i++) {
            FragmentRecord fragmentRecord = FLFragmentManager.getInstance().getFragmentRecord(getFragments()[i]);
            if (fragmentRecord == null) {
                break;
            }
            Class<? extends AbsBaseFragment> fragmentClz = fragmentRecord.getFragmentClz();
            if (fragmentClz == null) {
                break;
            }
            try {
                mFragmentList.add(fragmentClz.newInstance());
            } catch (InstantiationException e) {
                e.printStackTrace();
                break;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                break;
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
            mFragmentNameMap.put(getFragments()[i], i);
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        for (int i = 0; i < mFragmentList.size(); i++) {
            transaction.add(getContainerViewId(), mFragmentList.get(i), getFragments()[i]).hide(mFragmentList.get(i));
        }
        if (!isFinishing()) {
            transaction.commitAllowingStateLoss();
        }
    }

    protected abstract int getContainerViewId();

    public abstract String[] getFragments();

    public int showFragment(String fragmentName) {
//        if (page != mFragmentIndex && page < mFragmentList.size()) {
//            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//            for (int i = 0; i < mFragmentList.size(); i++) {
//                if (mFragmentList.get(i) != null) {
//                    transaction.hide(mFragmentList.get(i));
//                }
//            }
//            transaction.show(mFragmentList.get(page));
//            if (!isFinishing()) {
//                transaction.commitAllowingStateLoss();
//                mFragmentIndex = page;
//                return true;
//            }
//        } else {
//            return true;
//        }
        if (Tool.isEmpty(fragmentName) || Tool.isEmpty(mFragmentNameMap.get(fragmentName)) || mFragmentNameMap.get(fragmentName) > mFragmentList.size()) {
            return -1;
        }
        int page = mFragmentNameMap.get(fragmentName);
        if (page != mFragmentIndex && page < mFragmentList.size()) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            for (int i = 0; i < mFragmentList.size(); i++) {
                if (mFragmentList.get(i) != null) {
                    transaction.hide(mFragmentList.get(i));
                }
            }
            transaction.show(mFragmentList.get(page));
            if (!isFinishing()) {
                transaction.commitAllowingStateLoss();
                mFragmentIndex = page;
                mFragmentName = fragmentName;
                return page;
            }
        } else {
            return page;
        }
        return page;
    }

}
