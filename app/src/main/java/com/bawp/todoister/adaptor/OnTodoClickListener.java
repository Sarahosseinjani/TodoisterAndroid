package com.bawp.todoister.adaptor;

import com.bawp.todoister.model.Task;

public interface OnTodoClickListener {
    void onTodoClick( Task task);
    void onTodoRadioButtonClicked( Task task);
}
