package ru.ya.rrmstu.mywallet.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import ru.ya.rrmstu.mywallet.R;
import ru.ya.rrmstu.mywallet.core.enums.OperationType;
import ru.ya.rrmstu.mywallet.core.interfaces.Source;

// отвеачает за добавление и редактирование категории
public class EditSourceActivity<T extends Source> extends AppCompatActivity {// можно использовать более конктрентый тип Source - т.к. этот активити работает только с Source


    public final static String NODE_OBJECT = "ru.ya.rrmstu.mywallet.activities.EditSourceActivity.NodeObject";
    public final static int REQUEST_NODE_EDIT = 101;
    public final static int REQUEST_NODE_ADD = 102;
    public final static int REQUEST_NODE_ADD_CHILD = 103;


    private Toolbar toolbar;
    private EditText etName;
    private TextView tvNodeName;
    private ImageView imgSave;
    private ImageView imgClose;
    private Spinner spSourceType;
    private ArrayAdapter<OperationType> spAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_source);

        setupAnimation();

        toolbar = (Toolbar) findViewById(R.id.toolbar_edit_source);
        etName = (EditText) findViewById(R.id.et_source_name);
        tvNodeName = (TextView) findViewById(R.id.tv_node_name);
        imgSave = (ImageView) findViewById(R.id.img_node_save);
        imgClose = (ImageView) findViewById(R.id.img_node_close);
        spSourceType = (Spinner) findViewById(R.id.sp_source_type);


        spAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, OperationType.getList().subList(0, 2));// нам нужны только доход и расход для категорий

        spSourceType.setAdapter(spAdapter);

        setSupportActionBar(toolbar);

        final T node = (T) getIntent().getSerializableExtra(NODE_OBJECT); // получаем переданный объект для редактирования


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
                if (!newName.equals(node.getName())
                    //TODO также проверять иконку
                        ) {
                    node.setName(newName);
                    node.setOperationType((OperationType) spSourceType.getSelectedItem());

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

    private void finishWithTransition() {
        ActivityCompat.finishAfterTransition(EditSourceActivity.this);
    }

    private void setupAnimation() {
        // при открытии активити
        Slide slide = new Slide(Gravity.BOTTOM);
        slide.setDuration(300);
        getWindow().setEnterTransition(slide);

        // при закрытии активити
        Slide slide2 = new Slide(Gravity.TOP);
        slide2.setDuration(500);
        getWindow().setExitTransition(slide2);
    }
}
