package ru.supernacho.overtime.view;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import ru.supernacho.overtime.App;
import ru.supernacho.overtime.R;
import ru.supernacho.overtime.presenter.TabsPresenter;
import ru.supernacho.overtime.utils.view.CompanyInfo;
import ru.supernacho.overtime.view.adapters.FragmentAdapter;
import ru.supernacho.overtime.view.custom.KeyboardStateListener;
import ru.supernacho.overtime.view.custom.SoftKeyboardCoordinatorLayout;
import ru.supernacho.overtime.view.fragments.FragmentTag;
import ru.supernacho.overtime.view.fragments.LogsFragment;
import ru.supernacho.overtime.view.fragments.ManagerFragment;
import ru.supernacho.overtime.view.fragments.TimerFragment;
import timber.log.Timber;

public class TabsActivity extends MvpAppCompatActivity implements TabsView {

    private FragmentAdapter fragmentsPagerAdapter;
    private Fragment timerFragment;
    private Fragment logsFragment;
    private Fragment managerFragment;
    private String userId;
    private boolean isAdmin;
    private String companyId;
    private SoftKeyboardCoordinatorLayout softKeyboardLayout;
    private MenuItem manageEmployeeMenuItem;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.vp_container)
    ViewPager viewPager;
    @BindView(R.id.tabs)
    TabLayout tabLayout;

    @InjectPresenter
    TabsPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        softKeyboardLayout = new SoftKeyboardCoordinatorLayout(this);
        setContentView(softKeyboardLayout);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        init();

    }

    public void checkUserIsAdmin() {
        presenter.userIsAdmin();
    }


    @ProvidePresenter
    public TabsPresenter providePresenter(){
        TabsPresenter presenter = new TabsPresenter(AndroidSchedulers.mainThread());
        App.getInstance().getAppComponent().inject(presenter);
        return presenter;
    }

    private void init() {
        initFragments();
        initPagerAdapter();
        initViewPager();
        updatePageAdapter();
    }

    private void initPagerAdapter() {
        fragmentsPagerAdapter = new FragmentAdapter(getSupportFragmentManager());
        fragmentsPagerAdapter.addFragment(timerFragment);
        fragmentsPagerAdapter.addFragment(logsFragment);
        addManagerTab();
    }

    private void addManagerTab() {
        if (isAdmin) {
            if (tabLayout.getTabCount() < 3) {
                tabLayout.addTab(tabLayout.newTab().setText("Manager"));
                fragmentsPagerAdapter.addFragment(managerFragment);
            }
        } else if(tabLayout.getTabCount() > 2){
            tabLayout.removeTabAt(2);
            fragmentsPagerAdapter.removeTabPage(2);
        }
    }

    private void updatePageAdapter(){
        fragmentsPagerAdapter.startUpdate(viewPager);
        if (!timerFragment.isAdded()) timerFragment = (TimerFragment) fragmentsPagerAdapter.instantiateItem(viewPager, 0);
        if (!logsFragment.isAdded()) logsFragment = (LogsFragment) fragmentsPagerAdapter.instantiateItem(viewPager,1);
        if (!managerFragment.isAdded() && isAdmin) managerFragment = (ManagerFragment) fragmentsPagerAdapter.instantiateItem(viewPager,2);
        fragmentsPagerAdapter.finishUpdate(viewPager);
    }

    private void initFragments() {
        timerFragment = new TimerFragment();
        softKeyboardLayout.setKeyboardStateListener((KeyboardStateListener) timerFragment);
        logsFragment = LogsFragment.newInstance();
        managerFragment = new ManagerFragment();
    }

    private void initViewPager() {
        viewPager.setAdapter(fragmentsPagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
        Objects.requireNonNull(tabLayout.getTabAt(0)).select();
    }

    public void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        Objects.requireNonNull(inputMethodManager).hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(), 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkUserIsAdmin();
    }

    @Override
    public void setUserName(String userName) {
        toolbar.setTitle(userName);
    }

    @Override
    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
        addManagerTab();
        manageEmployeeMenuItem.setVisible(isAdmin);
        Timber.d("ADMIN: %s", isAdmin);
    }

    @Override
    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    @Override
    public void logoutDone() {
        Snackbar.make(toolbar, "Logout done", Snackbar.LENGTH_SHORT).show();
        Intent logoutIntent = new Intent(this, LoginActivity.class);
        startActivity(logoutIntent);
        this.finish();
    }

    @Override
    public void logoutFailed() {
        Snackbar.make(toolbar, "Logout failed? 8-0", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        if (tabLayout.getTabAt(1) != null && Objects.requireNonNull(tabLayout.getTabAt(1)).isSelected()){
            for (Fragment fragment : logsFragment.getChildFragmentManager().getFragments()) {
                if (Objects.requireNonNull(fragment.getTag()).equals(FragmentTag.WORKER_CHART)) {
                    ((LogsFragment)logsFragment).startDateChooser();
                } else {
                    super.onBackPressed();
                }
            }
        } else if (tabLayout.getTabAt(2)!= null && Objects.requireNonNull(tabLayout.getTabAt(2)).isSelected()){
            for (Fragment fragment : managerFragment.getChildFragmentManager().getFragments()) {
                if (Objects.requireNonNull(fragment.getTag()).equals(FragmentTag.EMPL_CHART)){
                    ((ManagerFragment)managerFragment).openDateFragment(userId);
                } else if (fragment.getTag().equals(FragmentTag.EMP_DATE_CHOOSER)){
                    ((ManagerFragment)managerFragment).callEmployeesChooser();
                } else if (fragment.getTag().equals(FragmentTag.ALL_EMPLOYEES_STAT)){
                    // TODO: 19.07.2018 fix back button for pie chart 
//                    ((ManagerFragment)managerFragment).callEmployeesChooser();
                    ((ManagerFragment)managerFragment).openDateFragment(userId);
                }
                else {
                    super.onBackPressed();
                }
            }
        } else {
            super.onBackPressed();
        }
    }

    public void setUserId(String userId){
        this.userId = userId;
    }

    public String getCompanyId() {
        return companyId;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tabs, menu);
        manageEmployeeMenuItem = menu.findItem(R.id.action_employee_management);
        manageEmployeeMenuItem.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_info_company:
                CompanyInfo.viewCurrent(this);
                return true;
            case R.id.action_employee_management:
                startActivity(new Intent(this, ManageEmployeeActivity.class));
                return true;
            case R.id.action_choose_company:
                startActivity(new Intent(this, ChooseCompanyActivity.class));
                return true;
            case R.id.action_reg_company:
                startActivity(new Intent(this, CompanyRegistrationActivity.class));
                return true;
            case R.id.action_settings:
                return true;
            case R.id.action_logout:
                presenter.logout();
                return true;
                default:
                    return super.onOptionsItemSelected(item);
        }
    }
}
