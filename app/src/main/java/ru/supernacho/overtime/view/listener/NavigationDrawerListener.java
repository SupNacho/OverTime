package ru.supernacho.overtime.view.listener;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;

import ru.supernacho.overtime.R;
import ru.supernacho.overtime.presenter.TabsPresenter;
import timber.log.Timber;

public class NavigationDrawerListener implements NavigationView.OnNavigationItemSelectedListener {
    private TabsPresenter presenter;
    private DrawerLayout drawerLayout;

    public NavigationDrawerListener(TabsPresenter presenter, DrawerLayout drawerLayout) {
        this.presenter = presenter;
        this.drawerLayout = drawerLayout;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_manage_employee :
                presenter.startEmployeeManager();
                break;
            case R.id.nav_choose_company:
                presenter.startCompanyChooser();
                break;
            case R.id.nav_register_new_company:
                presenter.startCompanyRegistration();
                break;
            case R.id.nav_about_company:
                presenter.openCompanyInfo();
                break;
            case R.id.nav_logout:
                presenter.logout();
                break;
                default:
                    Timber.d("no such nav drawer item");
                    break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
