package tests;

import managers.taskmanager.InMemoryTaskManager;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    public InMemoryTaskManager createManager(){
        return new InMemoryTaskManager();
    }
}