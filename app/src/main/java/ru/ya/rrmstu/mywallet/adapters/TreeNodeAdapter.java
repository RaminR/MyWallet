package ru.ya.rrmstu.mywallet.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
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

import java.sql.SQLException;
import java.util.List;

import ru.ya.rrmstu.mywallet.R;
import ru.ya.rrmstu.mywallet.activities.EditSourceActivity;
import ru.ya.rrmstu.mywallet.core.database.Initializer;
import ru.ya.rrmstu.mywallet.core.impls.DefaultSource;
import ru.ya.rrmstu.mywallet.core.interfaces.Source;
import ru.ya.rrmstu.mywallet.core.interfaces.TreeNode;
import ru.ya.rrmstu.mywallet.fragments.SprListFragment;


/**
 * {@link RecyclerView.Adapter} that can display a {@link TreeNode} and makes a call to the
 * specified {@link ru.ya.rrmstu.mywallet.fragments.SprListFragment.OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class TreeNodeAdapter<T extends TreeNode> extends RecyclerView.Adapter<TreeNodeAdapter.ViewHolder> {

    private static final String TAG = TreeNodeAdapter.class.getName();

    private List<T> list;
    private final SprListFragment.OnListFragmentInteractionListener listener;// хранит слушателя события нажатия пункта
    private Context context;
    private int currentEditPosition;

    private Snackbar snackbar; // для возможности отменить удаление


    public TreeNodeAdapter(List<T> list, SprListFragment.OnListFragmentInteractionListener listener, Context context) {
        this.list = list;
        this.listener = listener;
        this.context = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) { // вызывается один раз при создании
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.spr_node, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TreeNodeAdapter.ViewHolder holder, final int position) {// вызывается для каждой записи списка


        final T node = list.get(position);// определяем выбранный пункт
        holder.tvSprName.setText(node.getName());

        // показать кол-во дочерних элементов, если они есть
        if (node.hasChilds()) {
            holder.tvChildCount.setText(String.valueOf(node.getChilds().size()));
            holder.tvChildCount.setBackgroundColor(ContextCompat.getColor(context, R.color.colorSecondaryText));
        } else {
            holder.tvChildCount.setText("");
            holder.tvChildCount.setBackground(null);// чтобы не рисовал пустую закрашенную область
        }

        // для каждого пункта формируем слушатель
        initPopup(holder.btnPopup, context, node, position);


        // обработка события при нажатии на любую запись списка
        holder.layoutMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                T treeNode = list.get(position);// определяем объект

                // если был присвоен слушатель
                if (listener != null) {
                    // уведомляем слушателя, что был нажат пункт из списка
                    listener.onClickNode(treeNode);// передаем, какой именно объект был нажат
                }


                if (treeNode.hasChilds()) {// если есть дочерние значения
                    updateData((List<T>) treeNode.getChilds());// показать дочек
                } else {
                    runEditActivity(context, node, position);// если нет дочерних - открываем пункт для редактирования
                }

            }
        });

    }


    @Override
    public int getItemCount() {
        return list.size();
    }


    // метод обновления данных может вызываться из фрагмента, где используется данный адаптер
    public void updateData(List<T> list) {
        this.list = list;
        notifyDataSetChanged(); // сигнализируем адаптеру, что данные изменились, чтобы он обновился
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView tvSprName;
        public final TextView tvChildCount;
        public final ViewGroup layoutMain;
        public final ImageView btnPopup;

        public ViewHolder(View view) {
            super(view);

            // чтобы для каждого компонента не выполнять findViewById - сохраняем ссылки в константы
            tvSprName = (TextView) view.findViewById(R.id.tv_node_name);
            layoutMain = (RelativeLayout) view.findViewById(R.id.spr_main_layout);
            tvChildCount = (TextView) view.findViewById(R.id.tv_node_child_count);
            btnPopup = (ImageView) view.findViewById(R.id.img_node_popup);

        }

    }

    private void initPopup(final ImageView btnPopup, final Context context, final T node, final int position) {


        btnPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // при нажатии на каждый пункт - формируем отдельный объект контекстного меню
                PopupMenu dropDownMenu = new PopupMenu(context, btnPopup); // btnPopup - чтобы знал кто источник нажатия, для правильного расположения меню
                dropDownMenu.getMenuInflater().inflate(R.menu.spr_popup_menu, dropDownMenu.getMenu());
                dropDownMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        int id = menuItem.getItemId();

                        // считываем, какой пункт нажали по его id
                        if (id == R.id.item_child_add) {

                            Source source = new DefaultSource();
                            source.setOperationType(((Source) node).getOperationType());

                            listener.onPopupShow(node);
                            runAddChildActivity(context, (T) source, position);

                        } else if (id == R.id.item_edit) {
                            runEditActivity(context, node, position);

                        } else if (id == R.id.item_delete) {// если нажали пункт удаления

                            new AlertDialog.Builder(context)
                                    .setTitle(R.string.confirm)
                                    .setMessage(R.string.confirm_delete)
                                    .setIcon(android.R.drawable.ic_dialog_alert)

                                    // слушатель для кнопки ОК
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int whichButton) {

                                            // удаляем запись из коллекции (пока без удалении из базы)
                                            list.remove(node);
                                            notifyItemRemoved(position);


                                            // при удалении - сначала даем пользователю отменить действие с помощью SnackBar
                                            snackbar = Snackbar.make(btnPopup, R.string.deleted, Snackbar.LENGTH_LONG);

                                            snackbar.setAction(R.string.undo, new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {// если нажали на отмену удаления

                                                    if (node.hasParent()) { // если это был дочерний элемент
                                                        node.getParent().getChilds().add(position, node);// возвращаем обратно дочерний элемент
                                                    } else { // это был корневой элемент
                                                        list.add(position, node);// возвращаем
                                                    }

                                                    notifyDataSetChanged();

                                                }
                                            }).setCallback(new Snackbar.Callback() {// для того, чтобы могли отловить момент, когда SnackBar исчезнет (т.е. ползователь не успел отменить удаление)

                                                @Override
                                                public void onDismissed(Snackbar snackbar, int event) {

                                                    if (event != DISMISS_EVENT_ACTION) {// если не была нажата ссылка отмены
                                                        deleteNode((Source) node, context); // удаляем из-базы и коллекции, обновляем список
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

    private void runAddChildActivity(Context context, T node, int position) {

        Intent intent = new Intent(context, EditSourceActivity.class); // какой акивити хоти вызвать
        intent.putExtra(EditSourceActivity.NODE_OBJECT, node); // помещаем выбранный объект node для передачи в активити
        ((Activity) context).startActivityForResult(intent, EditSourceActivity.REQUEST_NODE_ADD_CHILD); // REQUEST_NODE_ADD - индикатор, кто является инициатором

    }

    // вызвать активити для редактирования справочного значения и вернуть результат в основной активити
    private void runEditActivity(Context context, T node, int position) {

        Intent intent = new Intent(context, EditSourceActivity.class); // какой акивити хоти вызвать
        intent.putExtra(EditSourceActivity.NODE_OBJECT, node); // помещаем выбранный объект node для передачи в активити
        ((Activity) context).startActivityForResult(intent, EditSourceActivity.REQUEST_NODE_EDIT); // REQUEST_NODE_EDIT - индикатор, кто является инициатором

        currentEditPosition = position;
    }


    // удаляет записи и обновляет список
    private void deleteNode(Source node, Context context) {
        try {
            Initializer.getSourceSync().delete(node);
            notifyDataSetChanged();// обновляем список

        } catch (SQLException e) {
            Log.e(TAG, e.getMessage());

            // если вышла ошибка целостности данных
            if (e.getMessage().contains("SQLiteConstraintException")) {
                Toast.makeText(context, R.string.has_operations, Toast.LENGTH_SHORT).show();
            }

        }
    }

    public void insertNode(TreeNode node) {
        try {
            Source source = (Source) node;
            Initializer.getSourceSync().add(source);
            notifyDataSetChanged();
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public void insertChild(TreeNode node) {
        try {
            Source source = (Source) node;
            Initializer.getSourceSync().add(source);
            list = (List<T>) node.getParent().getChilds(); // показываем список дочерних элементов, где только что добавили элемент
            listener.onClickNode(node.getParent());
            notifyDataSetChanged();
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public void updateNode(T node) {
        try {
            Initializer.getSourceSync().update((Source) node);
            notifyItemChanged(currentEditPosition);
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage());
        }
    }
}