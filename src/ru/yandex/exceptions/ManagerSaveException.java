package ru.yandex.exceptions;

import java.io.IOException;

public class ManagerSaveException extends RuntimeException {
    public ManagerSaveException() {
    }

    public ManagerSaveException(String message) {
        super(message);
    }

    public ManagerSaveException(String s, IOException exception) {
    }
}

/*
Мы ловим IOException, а кидаем managerSaveException, который наследует непроверяемое Exception(посмотрите какое
именно в теории😉), а для ловли IOException мы используем try with resources,
то есть после ловли должно быть throw new ManagerException
 */