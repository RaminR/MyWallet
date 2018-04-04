package ru.ya.rrmstu.mywallet.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import ru.ya.rrmstu.core.interfaces.TreeNode;
import ru.ya.rrmstu.mywallet.R;
import ru.ya.rrmstu.mywallet.fragments.SprListFragment;
import ru.ya.rrmstu.mywallet.fragments.SprListFragment.OnListFragmentInteractionListener;


/**
 * {@link RecyclerView.Adapter} that can display a {@link TreeNode} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class TreeNodeAdapter extends RecyclerView.Adapter<TreeNodeAdapter.ViewHolder> {

    private List<? extends TreeNode> list;
    private final SprListFragment.OnListFragmentInteractionListener clickListener;

    public TreeNodeAdapter(List<? extends TreeNode> items, SprListFragment.OnListFragmentInteractionListener clickListener) {
        list = items;
        this.clickListener = clickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.spr_node, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        TreeNode node = list.get(position);

        holder.tvSprName.setText(node.getName());

        if (node.getChilds() != null && !node.getChilds().isEmpty()) {
            holder.tvChildCount.setText(String.valueOf(node.getChilds().size()));
        } else {
            holder.tvChildCount.setText("");
        }


        holder.layoutMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TreeNode treeNode = list.get(position);
                if (null != clickListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    clickListener.onListFragmentInteraction(treeNode);
                }


                if (treeNode.hasChilds()) {// если есть дочерние значения
                    updateData(treeNode.getChilds());
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public void updateData(List<? extends TreeNode> list) {
        this.list = list;
        notifyDataSetChanged(); // сигнализируем адаптеру, что данные изменились, чтобы он обновился
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView tvSprName;
        public final TextView tvChildCount;
        public final ViewGroup layoutMain;

        public ViewHolder(View view) {
            super(view);
            tvSprName = (TextView) view.findViewById(R.id.spr_name);
            layoutMain = (RelativeLayout) view.findViewById(R.id.spr_main_layout);
            tvChildCount = (TextView) view.findViewById(R.id.spr_child_count);

        }

//        @Override
//        public String toString() {
//            return super.toString() + " '" + mContentView.getText() + "'";
//        }
    }
}
