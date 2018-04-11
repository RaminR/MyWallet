package ru.ya.rrmstu.mywallet.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.malinskiy.superrecyclerview.swipe.BaseSwipeAdapter;
import com.malinskiy.superrecyclerview.swipe.SwipeLayout;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.BaseItemAnimator;
import ru.ya.rrmstu.mywallet.R;
import ru.ya.rrmstu.mywallet.activities.EditSourceActivity;
import ru.ya.rrmstu.mywallet.core.database.Initializer;
import ru.ya.rrmstu.mywallet.core.impls.DefaultSource;
import ru.ya.rrmstu.mywallet.core.interfaces.Source;
import ru.ya.rrmstu.mywallet.core.interfaces.TreeNode;
import ru.ya.rrmstu.mywallet.fragments.SprListFragment;
import ru.ya.rrmstu.mywallet.utils.IconUtils;


/**
 * {@link RecyclerView.Adapter} that can display a {@link TreeNode} and makes a call to the
 * specified {@link ru.ya.rrmstu.mywallet.fragments.SprListFragment.OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class TreeNodeAdapter<T extends TreeNode> extends BaseSwipeAdapter<TreeNodeAdapter.ViewHolder> {


    private static final String TAG = TreeNodeAdapter.class.getName();

    private RecyclerView recyclerView; // нужен для установки анимации

    private List<T> adapterList = new ArrayList<>(); // текущая обновляемая коллекция адаптера - заполняется только нужными объектами для показа в списке

    private final SprListFragment.OnListFragmentInteractionListener listener;// хранит слушателя события нажатия пункта
    private Context context;
    private int currentEditPosition;

    private Snackbar snackbar;

    public static BaseItemAnimator animatorChilds; // анимация при открытии дочерних элементов (справа налево)
    public static BaseItemAnimator animatorParents; // анимация при открытии родительских элементов (слево направо)


    public TreeNodeAdapter(List<T> list, SprListFragment.OnListFragmentInteractionListener listener, Context context) {

        adapterList.clear();
        adapterList.addAll(list);

        this.listener = listener;
        this.context = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) { // вызывается один раз при создании

        snackbar = Snackbar.make(parent, R.string.deleted, Snackbar.LENGTH_LONG); // создаем один раз - для возможности отменить удаление

        recyclerView = (RecyclerView) parent; // нужен для установки анимации

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.spr_node, parent, false);


        createAnimations();

        return new ViewHolder(view);
    }


    // создание нужных объетов анимации, которые будут использоваться в зависимости от того, куда переходим (родительские или дочерние списки)
    private void createAnimations() {

        animatorParents = new BaseItemAnimator() {

            @Override
            protected void animateRemoveImpl(final RecyclerView.ViewHolder holder) {
                ViewCompat.animate(holder.itemView)
                        .translationX(holder.itemView.getRootView().getWidth())
                        .setDuration(getRemoveDuration())
                        .setInterpolator(mInterpolator)
                        .setListener(new DefaultRemoveVpaListener(holder))
                        .setStartDelay(getRemoveDelay(holder))
                        .start();
            }

            @Override
            protected void preAnimateAddImpl(RecyclerView.ViewHolder holder) {
                ViewCompat.setTranslationX(holder.itemView, -holder.itemView.getRootView().getWidth());
            }

            @Override
            protected void animateAddImpl(final RecyclerView.ViewHolder holder) {
                ViewCompat.animate(holder.itemView)
                        .translationX(0)
                        .setDuration(getAddDuration())
                        .setInterpolator(mInterpolator)
                        .setListener(new DefaultAddVpaListener(holder))
                        .setStartDelay(getAddDelay(holder))
                        .start();
            }
        };


        animatorChilds = new BaseItemAnimator() {


            @Override
            protected void animateRemoveImpl(final RecyclerView.ViewHolder holder) {

                ViewCompat.animate(holder.itemView)
                        .translationX(-holder.itemView.getRootView().getWidth())
                        .setDuration(getRemoveDuration())
                        .setInterpolator(mInterpolator)
                        .setListener(new DefaultRemoveVpaListener(holder))
                        .setStartDelay(getRemoveDelay(holder))
                        .start();

            }

            @Override
            protected void preAnimateAddImpl(RecyclerView.ViewHolder holder) {
                ViewCompat.setTranslationX(holder.itemView, holder.itemView.getRootView().getWidth());
            }

            @Override
            protected void animateAddImpl(final RecyclerView.ViewHolder holder) {
                ViewCompat.animate(holder.itemView)
                        .translationX(0)
                        .setDuration(getAddDuration())
                        .setInterpolator(mInterpolator)
                        .setListener(new DefaultAddVpaListener(holder))
                        .setStartDelay(getAddDelay(holder))
                        .start();
            }


        };


    }

    @Override
    public void onBindViewHolder(final TreeNodeAdapter.ViewHolder holder, final int position) {// вызывается для каждой записи списка


        final T node = adapterList.get(position);// определяем выбранный пункт
        holder.tvSprName.setText(node.getName());

        // показать кол-во дочерних элементов, если они есть
        if (node.hasChilds()) {
            holder.tvChildCount.setText(String.valueOf(node.getChilds().size()));
            holder.tvChildCount.setBackgroundColor(ContextCompat.getColor(context, R.color.colorSecondaryText));
            holder.imgSwipeDeleteNode.setVisibility(View.GONE);
        } else {
            holder.tvChildCount.setText("");
            holder.tvChildCount.setBackground(null);// чтобы не рисовал пустую закрашенную область
            holder.imgSwipeDeleteNode.setVisibility(View.VISIBLE);
        }

        // если пользователем не установлена иконка - показываем иконку по-умолчанию
        if (node.getIconName() == null || IconUtils.iconsMap.get(node.getIconName()) == null) {
            holder.imgNodeIcon.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_empty, null));
        } else {
            holder.imgNodeIcon.setImageDrawable(IconUtils.iconsMap.get(node.getIconName()));

        }


        // для каждого пункта формируем слушатель для popup меню
        initPopup(holder.btnPopup, context, node, position);

        final SwipeLayout swipeLayout = holder.swipeLayout;
        swipeLayout.setDragEdge(SwipeLayout.DragEdge.Right);
        swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);

        holder.imgSwipeDeleteNode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteDialog(node, position);
                swipeLayout.close();
            }
        });

        holder.imgSwipeEditNode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editNode(position, node);
                swipeLayout.close();
            }
        });

        holder.imgSwipeAddChildNode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addChildNode(node);
                swipeLayout.close();
            }
        });


        // обработка события при нажатии на любую запись списка
        holder.layoutMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swipeLayout.close();
                T treeNode = adapterList.get(position);// определяем объект

                // если был присвоен слушатель
                if (listener != null) {
                    // уведомляем слушателя, что был нажат пункт из списка
                    listener.onClickNode(treeNode);// передаем, какой именно объект был нажат
                }


                if (treeNode.hasChilds()) {// если есть дочерние значения
                    updateData((List<T>) treeNode.getChilds(), animatorChilds);// показать дочек
                } else {
                    editNode(position, node);// если нет дочерних - открываем пункт для редактирования
                }

            }
        });


    }


    @Override
    public int getItemCount() {
        return adapterList.size();
    }


    // метод обновления данных может вызываться из фрагмента, где используется данный адаптер
    public void updateData(final List<T> list, RecyclerView.ItemAnimator animator) {

        if (snackbar.isShown()) {
            hideSnackBar();
        }

        // ставит требумую анимацию для перехода (в зависимости от того, какие элементы открываем, дочерние или родительские)
        recyclerView.setItemAnimator(animator);


        int range = adapterList.size();
        adapterList.clear();
        notifyItemRangeRemoved(0, range);


        adapterList.addAll(list);
        notifyItemRangeInserted(0, adapterList.size());

    }


    public class ViewHolder extends BaseSwipeAdapter.BaseSwipeableViewHolder {

        public final TextView tvSprName;
        public final TextView tvChildCount;
        public final ViewGroup layoutMain;
        public final ImageView btnPopup;
        public final ImageView imgSwipeDeleteNode;
        public final ImageView imgSwipeAddChildNode;
        public final ImageView imgSwipeEditNode;
        public final ImageView imgNodeIcon;

        public ViewHolder(View view) {
            super(view);

            // чтобы для каждого компонента не выполнять findViewById - сохраняем ссылки в константы
            tvSprName = (TextView) view.findViewById(R.id.tv_node_name);
            layoutMain = (RelativeLayout) view.findViewById(R.id.spr_main_layout);
            tvChildCount = (TextView) view.findViewById(R.id.tv_node_child_count);
            btnPopup = (ImageView) view.findViewById(R.id.img_node_popup);
            imgSwipeDeleteNode = (ImageView) view.findViewById(R.id.img_swipe_delete_node);
            imgSwipeAddChildNode = (ImageView) view.findViewById(R.id.img_swipe_add_child_node);
            imgSwipeEditNode = (ImageView) view.findViewById(R.id.img_swipe_edit_node);
            imgNodeIcon = (ImageView) view.findViewById(R.id.img_node_icon);

        }

    }

    private void initPopup(final ImageView btnPopup, final Context context, final T node, final int position) {


        btnPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                hideSnackBar();

                // при нажатии на каждый пункт - формируем отдельный объект контекстного меню
                PopupMenu dropDownMenu = new PopupMenu(context, btnPopup); // btnPopup - чтобы знал кто источник нажатия, для правильного расположения меню
                dropDownMenu.getMenuInflater().inflate(R.menu.spr_popup_menu, dropDownMenu.getMenu());
                dropDownMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        int id = menuItem.getItemId();

                        // считываем, какой пункт нажали по его id
                        if (id == R.id.item_child_add) {

                            addChildNode(node);

                        } else if (id == R.id.item_edit) {
                            editNode(position, node);

                        } else if (id == R.id.item_delete) {// если нажали пункт удаления

                            showDeleteDialog(node, position);

                        }
                        return true;
                    }


                });

                // для записей, где есть дочерние элементы - делать пункт Удалить неактивным
                if (node.hasChilds()) {
                    // TODO исправить антипаттерн magic number
                    dropDownMenu.getMenu().getItem(2).setEnabled(false);
                }

                dropDownMenu.show();
            }
        });

    }

    private void editNode(int position, T node) {
        currentEditPosition = position;
        runActivity(node, EditSourceActivity.REQUEST_NODE_EDIT);
    }

    private void addChildNode(T node) {
        Source source = new DefaultSource();
        source.setOperationType(((Source) node).getOperationType());

        listener.onPopupShow(node);
        runActivity((T) source, EditSourceActivity.REQUEST_NODE_ADD_CHILD);
    }

    // закрыть snackbar, если в данный момент открыт
    private void hideSnackBar() {
        if (snackbar.isShown()) {
            snackbar.dismiss();
        }

    }


    private void showDeleteDialog(final T node, final int position) {
        new AlertDialog.Builder(context)
                .setTitle(R.string.confirm)
                .setMessage(R.string.confirm_delete)
                .setIcon(android.R.drawable.ic_dialog_alert)

                // слушатель для кнопки ОК
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {

                        // сначала удаляем запись просто из текушей коллекции адаптера (без удалении из базы) - даем сначала пользователю шанс отменить
                        adapterList.remove(node);
                        notifyItemRemoved(position);


                        // показываем Snackbar, чтобы дать пользователю возможность отменить действие
                        snackbar.setAction(R.string.undo, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {// если нажали на ссылку ОТМЕНИТЬ

                                adapterList.add(position, node);// возвращаем обратно
                                notifyItemInserted(position);

                            }
                        }).setCallback(new Snackbar.Callback() {// для того, чтобы могли отловить момент, когда SnackBar исчезнет (т.е. ползователь не успел отменить удаление)

                            @Override
                            public void onDismissed(Snackbar snackbar, int event) {

                                if (event != DISMISS_EVENT_ACTION) {// если не была нажата ссылка отмены
                                    deleteNode((Source) node); // удаляем из-базы и коллекции, обновляем список
                                }

                            }
                        }).show();


                    }


                })

                // слушатель для кнопки отмены
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();// если нажали отмену - просто закрываем диалоговое окно

                    }
                }).show();


    }

    // открываем активити для создания или редактирования элемента (параметр requestCode)
    private void runActivity(T node, int requestCode) {
        hideSnackBar();
        Intent intent = new Intent(context, EditSourceActivity.class); // какой акивити хотим вызвать
        intent.putExtra(EditSourceActivity.NODE_OBJECT, node); // помещаем выбранный объект node для передачи в активити
        ((Activity) context).startActivityForResult(intent, requestCode,
                ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context).toBundle()); // устанавливаем анимацию перехода
    }


    // удаляет записи и обновляет список
    private boolean deleteNode(Source node) {
        try {

            // ранее уже удалили запись из списка - поэтому не вызываем  notifyItemRemoved(position), а просто удаляем из БД
            Initializer.getSourceSync().delete(node);

            notifyDataSetChanged(); // этот метод также нужен, если пользователь нажмет отмену удаления и успеет перейти в другой список

            return true;

        } catch (SQLException e) {
            Log.e(TAG, e.getMessage());

            // если вышла ошибка целостности данных
            if (e.getMessage().contains("SQLiteConstraintException")) {
                Toast.makeText(context, R.string.has_operations, Toast.LENGTH_SHORT).show();
            }

        }

        return false;
    }

    // вставка нового корневого элемента
    public void insertRootNode(T node) {
        try {
            Source source = (Source) node;
            Initializer.getSourceSync().add(source);// сначала вставляем в БД и общую коллекцию
            adapterList.add(node); // потом - в текущую временную коллекцию адаптера
            notifyItemInserted(adapterList.indexOf(node));// вставка в последнюю позицию
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    // вставка дочернего элемента
    public void insertChildNode(TreeNode node) {
        try {
            Source source = (Source) node;
            Initializer.getSourceSync().add(source);

            List<T> list = (List<T>) node.getParent().getChilds(); // загрузить все дочерние элементы родителя, для которого добавили элемент

            updateData(list, animatorChilds);  // показываем список дочерних элементов, среди которых и новый добавленный

            listener.onClickNode(node.getParent()); // уведомляем слушателя, что перешли в дочерний список

        } catch (SQLException e) {
            Log.e(TAG, e.getMessage());
        }
    }


    // обновление значения
    public void updateNode(T node) {
        try {
            Initializer.getSourceSync().update((Source) node);
            adapterList.set(currentEditPosition, node);
            notifyItemChanged(currentEditPosition);
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage());
        }

    }
}