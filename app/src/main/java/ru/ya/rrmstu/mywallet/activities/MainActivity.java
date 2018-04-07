package ru.ya.rrmstu.mywallet.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ru.ya.rrmstu.mywallet.R;
import ru.ya.rrmstu.mywallet.core.database.Initializer;
import ru.ya.rrmstu.mywallet.core.enums.OperationType;
import ru.ya.rrmstu.mywallet.core.interfaces.TreeNode;
import ru.ya.rrmstu.mywallet.fragments.SprListFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SprListFragment.OnListFragmentInteractionListener {


    private ImageView iconBack;
    private Toolbar toolbar;
    private TextView toolbarTitle;

    private TabLayout tabLayout;

    private TreeNode selectedNode;

    private SprListFragment sprListFragment;

    private List<? extends TreeNode> list;// хранит корневые элементы списка

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initToolbar();

        //initFloatingButton();

        initNavigationDrawer(toolbar);

        initFragment();

        initTabs();

        list = Initializer.getSourceSync().getAll();


    }


    private void initTabs() {
        tabLayout = (TabLayout) findViewById(R.id.tabs);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                // при переключении табов - сбросить название и убрать стрелку возврата к родительским элементам
                iconBack.setVisibility(View.INVISIBLE);
                toolbarTitle.setText(R.string.sources);

                switch (tab.getPosition()) {
                    case 0:// все
                        list = Initializer.getSourceSync().getAll();
                        break;
                    case 1:// доход
                        list = Initializer.getSourceSync().getList(OperationType.INCOME);
                        break;
                    case 2: // расход
                        list = Initializer.getSourceSync().getList(OperationType.OUTCOME);
                        break;
                }

                sprListFragment.updateData(list);


            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }

        });
    }

    private void initFragment() {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        sprListFragment = new SprListFragment();
        fragmentTransaction.replace(R.id.spr_list_fragment, sprListFragment);
        fragmentTransaction.commit();
    }


    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        iconBack = (ImageView) findViewById(R.id.ic_back);

        iconBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedNode.getParent() == null) {// показать корневые элементы
                    sprListFragment.updateData(list);
                    toolbarTitle.setText(R.string.sources);
                    iconBack.setVisibility(View.INVISIBLE);
                } else {// показать родительские элементы
                    sprListFragment.updateData(selectedNode.getParent().getChilds());
                    selectedNode = selectedNode.getParent();
                    toolbarTitle.setText(selectedNode.getName());

                }

            }
        });

    }

    private void initFloatingButton() {
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    private void initNavigationDrawer(Toolbar toolbar) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_operations) {

        } else if (id == R.id.nav_sources) {

        } else if (id == R.id.nav_storages) {

        } else if (id == R.id.nav_reports) {

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_help) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onListFragmentInteraction(TreeNode selectedNode) {
        this.selectedNode = selectedNode;
        if (selectedNode.hasChilds()) {
            toolbarTitle.setText(selectedNode.getName());// показывает выбранную категорию
            iconBack.setVisibility(View.VISIBLE);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EditSourceActivity.REQUEST_NODE_EDIT) {// кто был инициатором вызова
            if (resultCode == RESULT_OK) { // какой результат вернулся
                sprListFragment.updateRow((TreeNode) data.getSerializableExtra(EditSourceActivity.NODE_OBJECT));// отправляем на обновление измененный объект
            }
        }
    }
}
