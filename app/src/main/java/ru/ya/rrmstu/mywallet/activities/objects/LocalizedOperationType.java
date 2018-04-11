package ru.ya.rrmstu.mywallet.activities.objects;

import android.content.Context;

import ru.ya.rrmstu.mywallet.R;
import ru.ya.rrmstu.mywallet.core.enums.OperationType;

// обертак для стандартного OperationType, которая умеет получать нунжный перевод
public class LocalizedOperationType {

    private OperationType operationType;
    private Context context;

    public LocalizedOperationType(OperationType operationType, Context context) {
        this.operationType = operationType;
        this.context = context;
    }


    @Override
    public String toString() {

        switch (operationType){
            case INCOME:
                return context.getResources().getString(R.string.income);

            case OUTCOME:
                return context.getResources().getString(R.string.outcome);

            case TRANSFER:
                return context.getResources().getString(R.string.transfer);

            case CONVERT:
                return context.getResources().getString(R.string.convert);

        }

        return null;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocalizedOperationType that = (LocalizedOperationType) o;

        return operationType == that.operationType;

    }

    @Override
    public int hashCode() {
        return operationType.hashCode();
    }
}
