package ru.ya.rrmstu.mywallet.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.malinskiy.superrecyclerview.SuperRecyclerView;

import ru.ya.rrmstu.mywallet.R;
import ru.ya.rrmstu.mywallet.adapters.SelectIconAdapter;
import ru.ya.rrmstu.mywallet.utils.IconUtils;

public class IconListFragment extends Fragment {

    private static final int COLUMN_COUNT = 5; // количество столбцов для отображения в GridLayoutManager
    private SelectIconListener selectIconListener;// в нашем случае - активити является слушателем события нажатия пункта меню
    private SelectIconAdapter selectIconAdapter; // адаптер для RecyclerView

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public IconListFragment() {}// фрагмент рекомендуется создавать пустым конструктором


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.icon_list, container, false);

        if (view instanceof SuperRecyclerView) {
            Context context = view.getContext();
            SuperRecyclerView recyclerView = (SuperRecyclerView) view;
            recyclerView.setLayoutManager(new GridLayoutManager(context, COLUMN_COUNT));// выбираем тип показа как таблица (сеткой)

            recyclerView.setAdapter(selectIconAdapter);
        }
        return view;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // если активити, где используется фрагмент, не реализовывает интерфейс - уведомляем исключением
        // таким образом - разработчик принуждается к тому, чтобы активити, где размещен фрагмент, реализовывал этот интерфейс
        if (context instanceof SelectIconListener) {
            selectIconListener = (SelectIconListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement SelectIconListener");
        }


        selectIconAdapter = new SelectIconAdapter(getContext(), IconUtils.iconsList, selectIconListener);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        selectIconListener = null;// при смене или удалении фрагмента из активити - зануляем слушатель
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
    public interface SelectIconListener { // интерфейс для слушателя события при выборе иконки
        void onIconSelected(String iconName);
    }
}

