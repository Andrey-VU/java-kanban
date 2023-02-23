package ru.yandex.tmanager;

public class HttpTaskManager extends FileBackedTasksManager {

}


    /*
     реализация аналогична файловому менеджеру, но пишем и читаем не из файла,
     а через KVTaskClient - вызываем его методы save/load
     */

    // Ответственность:
    // 1. сохранять данные на KVServer и восстанавливаться с данных с сервера
    // по аналогии с FileBacked (приватный метод save)

    // 2. Вызывать методы базовой реализации TaskManager
    // KVTaskClient - слой между HttpTaskManager и KVServer для работы с KVServer

    // Ответственность
    // 1. Создание http запросов к KVServer
    // KVServer - сервер для хранения состояния менеджеров (по аналогии с файлом)
    // Ответственность
    // 1. Регистрация клиентов
    // 2. Сохранение и выгрузка данных

// HttpTaskServer -> HttpTaskManager -> KVTaskClient -> KVServer