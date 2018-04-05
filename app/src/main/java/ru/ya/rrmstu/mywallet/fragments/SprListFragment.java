package ru.ya.rrmstu.mywallet.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ru.ya.rrmstu.core.database.Initializer;
import ru.ya.rrmstu.core.interfaces.Source;
import ru.ya.rrmstu.core.interfaces.TreeNode;
import ru.ya.rrmstu.mywallet.R;
import ru.ya.rrmstu.mywallet.adapters.TreeNodeAdapter;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class SprListFragment extends Fragment {

    private OnListFragmentInteractionListener clickListener;// в нашем случае - активити является слушателем события нажатия пункта меню

    private TreeNodeAdapter treeNodeAdapter; // адаптер для RecyclerView

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SprListFragment() {
    }// фрагмент рекомендуется создавать пустым конструктором


    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
//    public static SprListFragment newInstance(int columnCount) {
//        SprListFragment fragment = new SprListFragment();
//        Bundle args = new Bundle();
//        args.putInt(ARG_COLUMN_COUNT, columnCount);
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.spr_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));// выбираем стандартный тип показа - как список
            recyclerView.setAdapter(treeNodeAdapter);
        }
        return view;
    }


    public void updateData(List<? extends TreeNode> list) {
        treeNodeAdapter.updateData(list);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // если активити, где используется фрагмент, не реализовывает интерфейс - уведомляем исключением
        // таким образом - разработчик принуждается к тому, чтобы активити, где размещен фрагмент, реализовывал этот интерфейс
        if (context instanceof OnListFragmentInteractionListener) {
            clickListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }

        treeNodeAdapter = new TreeNodeAdapter<Source>(Initializer.getSourceSync().getAll(), clickListener, getContext()); // при первой загрузке - показать все записи

    }

    @Override
    public void onDetach() {
        super.onDetach();
        clickListener = null;// при смене или удалении фрагмента из активити - зануляем слушатель
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener { // интерфейс для слушателя события при нажатии записи списка
        void onListFragmentInteraction(TreeNode selectedNode);
    }
}
