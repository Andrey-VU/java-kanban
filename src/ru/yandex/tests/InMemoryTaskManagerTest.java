package ru.yandex.tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.tmanager.Managers;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest {

    @BeforeEach
    public void beforeEach() throws IOException {
        manager = Managers.getDefault();
    }

}