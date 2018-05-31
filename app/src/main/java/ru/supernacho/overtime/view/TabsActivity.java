package ru.supernacho.overtime.view;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import ru.supernacho.overtime.R;
import ru.supernacho.overtime.presenter.TabsPresenter;
import ru.supernacho.overtime.view.adapters.FragmentAdapter;
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
        setContentView(R.layout.activity_tabs);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        init();

    }

    @ProvidePresenter
    public TabsPresenter providePresenter(){
        return new TabsPresenter(AndroidSchedulers.mainThread());
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
        fragmentsPagerAdapter.addFragment(managerFragment);
    }

    private void updatePageAdapter(){
        fragmentsPagerAdapter.startUpdate(viewPager);
        if (!timerFragment.isAdded()) timerFragment = (TimerFragment) fragmentsPagerAdapter.instantiateItem(viewPager, 0);
        if (!logsFragment.isAdded()) logsFragment = (LogsFragment) fragmentsPagerAdapter.instantiateItem(viewPager,1);
        if (!managerFragment.isAdded()) managerFragment = (ManagerFragment) fragmentsPagerAdapter.instantiateItem(viewPager,2);
        fragmentsPagerAdapter.finishUpdate(viewPager);
    }

    private void initFragments() {
        timerFragment = new TimerFragment();
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
    public void setUserName(String userName) {
        toolbar.setTitle(userName);
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
        if (Objects.requireNonNull(tabLayout.getTabAt(1)).isSelected()){
            for (Fragment fragment : logsFragment.getChildFragmentManager().getFragments()) {
                if (Objects.requireNonNull(fragment.getTag()).equals(FragmentTag.WORKER_CHART)) {
                    ((LogsFragment)logsFragment).startDateChooser();
                } else {
                    super.onBackPressed();
                }
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tabs, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_logout) {
            presenter.logout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
