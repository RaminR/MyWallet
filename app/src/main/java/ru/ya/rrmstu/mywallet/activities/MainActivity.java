package ru.ya.rrmstu.mywallet.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityOptionsCompat;
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
import ru.ya.rrmstu.mywallet.core.impls.DefaultSource;
import ru.ya.rrmstu.mywallet.core.interfaces.Source;
import ru.ya.rrmstu.mywallet.core.interfaces.TreeNode;
import ru.ya.rrmstu.mywallet.fragments.SprListFragment;

import static ru.ya.rrmstu.mywallet.activities.EditSourceActivity.NODE_OBJECT;
import static ru.ya.rrmstu.mywallet.activities.EditSourceActivity.REQUEST_NODE_ADD;
import static ru.ya.rrmstu.mywallet.activities.EditSourceActivity.REQUEST_NODE_EDIT;
import static ru.ya.rrmstu.mywallet.activities.EditSourceActivity.REQUEST_NODE_ADD_CHILD;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SprListFragment.OnListFragmentInteractionListener {


    private ImageView iconBack;
    private ImageView iconAdd;
    private Toolbar toolbar;
    private TextView toolbarTitle;

    private TabLayout tabLayout;

    private TreeNode selectedParentNode;

    private OperationType defaultType;// для автоматического проставления типа при создании нового элемента

    private SprListFragment sprListFragment;

    private List<? extends TreeNode> currentList;// текущий список для отображения

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initToolbar();

        //initFloatingButton();

        initNavigationDrawer(toolbar);

        initFragment();

        initTabs();

        currentList = Initializer.getSourceSync().getAll();


    }


    private void initTabs() {
        tabLayout = (TabLayout) findViewById(R.id.tabs);

        tabLayout.getTabAt(0).setText(R.string.tab_all);
        tabLayout.getTabAt(1).setText(R.string.tab_income);
        tabLayout.getTabAt(2).setText(R.string.tab_outcome);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                // при переключении табов - сбросить название и убрать стрелку возврата к родительским элементам
                iconBack.setVisibility(View.INVISIBLE);
                toolbarTitle.setText(R.string.sources);

                switch (tab.getPosition()) {
                    case 0:// все
                        currentList = Initializer.getSourceSync().getAll();
                        defaultType = null;
                        break;
                    case 1:// доход
                        currentList = Initializer.getSourceSync().getList(OperationType.INCOME);
                        defaultType = OperationType.INCOME;
                        break;
                    case 2: // расход
                        currentList = Initializer.getSourceSync().getList(OperationType.OUTCOME);
                        defaultType = OperationType.OUTCOME;
                        break;
                }

                sprListFragment.showNodes(currentList);
                selectedParentNode = null; // сбрасываем, в данный момент никакой родительский элемент не выбран

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
        iconBack = (ImageView) findViewById(R.id.ic_back_node);
        iconAdd = (ImageView) findViewById(R.id.ic_add_node);


        // при нажатии на кнопку перехода к родительским элементам справочника
        iconBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (selectedParentNode.getParent() == null) {// показать корневые элементы
                    sprListFragment.showNodes(currentList);
                    toolbarTitle.setText(R.string.sources);
                    iconBack.setVisibility(View.INVISIBLE);
                    selectedParentNode = null; // указывает, что никакой node не выбран в данный момент
                    defaultType = null;
                } else {// показать родительские элементы
                    sprListFragment.showNodes(selectedParentNode.getParent().getChilds());
                    selectedParentNode = selectedParentNode.getParent(); // в переменной selectedParentNode всегда должна быть родительская категория, в которой мы находимся в данный момент
                    toolbarTitle.setText(selectedParentNode.getName());
                }


            }
        });


        // при нажатии на кнопку добавления элемента
        iconAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Source source = new DefaultSource();

                // если пользователь выбрал таб и создает новый элемент - сразу прописываем тип
                if (defaultType != null) {
                    source.setOperationType(defaultType);
                }


                Intent intent = new Intent(MainActivity.this, EditSourceActivity.class); // какой акивити хоти вызвать
                intent.putExtra(NODE_OBJECT, source); // помещаем выбранный объект node для передачи в активити
                startActivityForResult(intent, REQUEST_NODE_ADD, ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this).toBundle()); // REQUEST_NODE_EDIT - индикатор, кто является инициатором); // REQUEST_NODE_ADD - индикатор, кто является инициатором

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

            if (iconBack.getVisibility() == View.VISIBLE) { // если кнопка видна - значит находимся в дочернем списке
                iconBack.callOnClick(); // кнопкой назад сможем переходит на уровень выше, к родительским элементам
            } else {
                super.onBackPressed();// иначе выполняем стандартную реализации кнопки Back
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
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
    public void onClickNode(TreeNode selectedParentNode) {// при каждом нажатии на элемент списка - срабатывает этот слушатель событий - записывает выбранный node

        if (selectedParentNode.hasChilds()) {
            this.selectedParentNode = selectedParentNode;// в selectedParentNode хранится ссылка на выбранную родительскую категорию
            defaultType = ((Source) selectedParentNode).getOperationType();// сохраняем тип, чтобы при создании нового элемента - автоматически его прописывать
            toolbarTitle.setText(selectedParentNode.getName());// показывает выбранную категорию
            iconBack.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPopupShow(TreeNode selectedParentNode) {
        this.selectedParentNode = selectedParentNode;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        TreeNode node = null;

        if (resultCode == RESULT_OK) {

            switch (requestCode) {
                case REQUEST_NODE_EDIT:
                    sprListFragment.updateNode((TreeNode) data.getSerializableExtra(NODE_OBJECT));// отправляем на обновление измененный объект
                    break;

                case REQUEST_NODE_ADD:
                    node = (TreeNode) data.getSerializableExtra(NODE_OBJECT);

                    if (selectedParentNode != null) {// если создаем дочерний элемент, а не корневой
                        node.setParent(selectedParentNode);// setParent нужно выполнять, когда объект уже вернулся из активити
                    }

                    sprListFragment.insertRootNode(node);// отправляем на добавление новый объект
                    break;

                case REQUEST_NODE_ADD_CHILD:

                    node = (TreeNode) data.getSerializableExtra(NODE_OBJECT);
                    node.setParent(selectedParentNode);

                    sprListFragment.insertChildNode(node);// отправляем на добавление новый объект
                    break;

            }
        }
    }
}