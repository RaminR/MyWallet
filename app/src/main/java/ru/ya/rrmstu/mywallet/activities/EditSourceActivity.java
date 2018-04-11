package ru.ya.rrmstu.mywallet.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ru.ya.rrmstu.mywallet.R;
import ru.ya.rrmstu.mywallet.activities.objects.LocalizedOperationType;
import ru.ya.rrmstu.mywallet.core.enums.OperationType;
import ru.ya.rrmstu.mywallet.core.interfaces.Source;
import ru.ya.rrmstu.mywallet.utils.IconUtils;

// отвеачает за добавление и редактирование категории
public class EditSourceActivity<T extends Source> extends AbstractAnimationActivity {// можно использовать более конктрентый тип Source - т.к. этот активити работает только с Source


    public final static String NODE_OBJECT = "ru.ya.rrmstu.mywallet.activities.EditSourceActivity.NodeObject";

    public final static int REQUEST_NODE_EDIT = 101;
    public final static int REQUEST_NODE_ADD = 102;
    public final static int REQUEST_NODE_ADD_CHILD = 103;


    private Toolbar toolbar;
    private EditText etName;
    private TextView tvNodeName;
    private ImageView imgSave;
    private ImageView imgClose;
    private ImageView imgSourceIcon;
    private Spinner spSourceType;
    private ArrayAdapter<LocalizedOperationType> spAdapter;

    private T node;

    private String newIconName;


    static {

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_source);


        toolbar = (Toolbar) findViewById(R.id.toolbar_edit_source);
        etName = (EditText) findViewById(R.id.et_source_name);
        tvNodeName = (TextView) findViewById(R.id.tv_node_name);
        imgSave = (ImageView) findViewById(R.id.img_node_save);
        imgClose = (ImageView) findViewById(R.id.img_node_close);
        imgSourceIcon = (ImageView) findViewById(R.id.img_source_icon);
        spSourceType = (Spinner) findViewById(R.id.sp_source_type);


        List<LocalizedOperationType> listTypes = new ArrayList<>(2);
        listTypes.add(new LocalizedOperationType(OperationType.INCOME, this));
        listTypes.add(new LocalizedOperationType(OperationType.OUTCOME, this));


        spAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, listTypes);// нам нужны только доход и расход для категорий

        spSourceType.setAdapter(spAdapter);

        setSupportActionBar(toolbar);

        node = (T) getIntent().getSerializableExtra(NODE_OBJECT); // получаем переданный объект для редактирования

        if (node.getIconName() == null || IconUtils.iconsMap.get(node.getIconName()) == null) {
            imgSourceIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_empty, null));
        } else {
            imgSourceIcon.setImageDrawable(IconUtils.iconsMap.get(node.getIconName()));
        }

        imgSourceIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditSourceActivity.this, SelectIconActivity.class); // какой акивити хотим вызвать
                startActivityForResult(intent, SelectIconActivity.REQUEST_SELECT_ICON,
                        ActivityOptionsCompat.makeSceneTransitionAnimation(EditSourceActivity.this).toBundle()); // устанавливаем анимацию перехода

            }
        });


        // в зависимости от типа действия (создание или редактирование) - меняем заголовок
        if (node.getName() != null) {
            tvNodeName.setText(R.string.editing);
            etName.setText(node.getName());
        } else {
            tvNodeName.setText(R.string.adding);
            etName.setText("");

        }


        if (node.getOperationType() != null) {// при редактировании объекта - это поле будет заполнено
            spSourceType.setEnabled(false);
            spSourceType.setSelection(OperationType.getList().indexOf(node.getOperationType()));
        } else {
            spSourceType.setEnabled(true);
        }


        // слушатель события при сохранении
        imgSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String newName = etName.getText().toString();

                // не давать сохранять пустое значение
                if (newName.trim().length() == 0) {
                    Toast.makeText(EditSourceActivity.this, R.string.enter_value, Toast.LENGTH_SHORT).show();
                    return;
                }

                // чтобы лишний раз не сохранять - проверяем, были ли изменены данные, если нет, то при сохранении просто закрываем активити
                if (!newName.equals(node.getName()) || !newIconName.equals(node.getIconName())// если название или иконка изменились
                    //TODO также проверять иконку
                        ) {
                    node.setName(newName);
                    node.setOperationType(((LocalizedOperationType) spSourceType.getSelectedItem()).getOperationType());
                    node.setIconName(newIconName);

                    Intent intent = new Intent();
                    intent.putExtra(NODE_OBJECT, node);// сюда попадает уже отредактированный объект, который нужно сохранить в БД
                    setResult(RESULT_OK, intent);

                }


                finishWithTransition();// закрыть активити


            }
        });


        // слушатель события при отмене
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishWithTransition();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == SelectIconActivity.REQUEST_SELECT_ICON) {
                newIconName = data.getStringExtra(SelectIconActivity.ICON_NAME); // получаем переданное имя выбранной иконки

                if (!newIconName.equals(node.getIconName())) { // если иконка изменилась - обновляем отображение
                    imgSourceIcon.setImageDrawable(IconUtils.iconsMap.get(newIconName));// получаем новую иконку из карты иконок
                }
            }
        }

    }
}
