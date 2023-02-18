package ru.yandex.exceptions;

public class IntersectionException extends RuntimeException {

    public IntersectionException() {
    }

    public IntersectionException(String message) {
        super(message);
    }
}

// RuntimeException - (от англ. runtime — «выполнение, исполнение»).
// К ним относятся выход за пределы массива или неверно переданные данные в метод,
// ошибки при арифметических операциях (например, деление на ноль) и обращения к неинициализированным объектам

