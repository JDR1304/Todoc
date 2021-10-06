package com.cleanup.todoc;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.persistence.room.Room;
import android.support.test.InstrumentationRegistry;

import com.cleanup.todoc.database.ToDocDatabase;
import com.cleanup.todoc.model.Project;
import com.cleanup.todoc.model.Task;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.sql.Timestamp;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class TaskDaoTest {

    // FOR DATA
    private ToDocDatabase database;

    //Rule allows to define the way the tests will be executed
    @Rule
    //InstantTaskExecutorRule allows to execute the tests in a synchronized way,not with a thread in background
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    //initDb for creating an instance of our database
    //inMemoryDatabaseBuilder allows to create a dataBase directly in memory
    public void initDb() throws Exception {
        this.database = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(),
                ToDocDatabase.class)
                .allowMainThreadQueries()
                .build();
    }

    @After
    public void closeDb() throws Exception {
        database.close();
    }

    private static Project project1 = new Project(1,"Projet 1",0xFFEADAD1);
    private static Task newTask1= new Task(1,1,"Nouvelle tache", System.currentTimeMillis());
    private static Task newTask2= new Task(2,1,"Nouvelle tache", System.currentTimeMillis());
    private static Task newTask3= new Task(3,1,"Nouvelle tache", System.currentTimeMillis());

    @Test
    public void insertAndGetProject() throws InterruptedException {
        this.database.projectDao().createProject(project1);
        // TEST
        Project project = LiveDataTestUtils.getValue(this.database.projectDao().getProject(1));
        assertTrue(project.getName().equals(project1.getName()) && project.getId() == project1.getId());
    }
    @Test
    public void getTasksWhenNoTaskInserted() throws InterruptedException {
        // TEST
        List<Task> tasks = LiveDataTestUtils.getValue(this.database.taskDao().getTasks());
        assertTrue(tasks.isEmpty());
    }

    @Test
    public void insertAndGetTasks() throws InterruptedException {
        this.database.projectDao().createProject(project1);
        this.database.taskDao().insertTask(newTask1);
        this.database.taskDao().insertTask(newTask2);
        this.database.taskDao().insertTask(newTask3);

        // TEST
        List<Task> tasks = LiveDataTestUtils.getValue(this.database.taskDao().getTasks());
        assertTrue(tasks.size() == 3);
    }

    @Test
    public void insertAndUpdateTask() throws InterruptedException {
        this.database.projectDao().createProject(project1);
        this.database.taskDao().insertTask(newTask1);
        Task taskAdded = LiveDataTestUtils.getValue(this.database.taskDao().getTasks()).get(0);
        taskAdded.setSelected(true);
        this.database.taskDao().updateTask(taskAdded);

        //TEST
        List<Task> tasks = LiveDataTestUtils.getValue(this.database.taskDao().getTasks());
        assertTrue(tasks.size() == 1 && tasks.get(0).getSelected());
    }

    @Test
    public void insertAndDeleteTask() throws InterruptedException {
        this.database.projectDao().createProject(project1);
        this.database.taskDao().insertTask(newTask1);
        Task taskAdded = LiveDataTestUtils.getValue(this.database.taskDao().getTasks()).get(0);
        this.database.taskDao().deleteTask(taskAdded.getId());

        //TEST
        List<Task> tasks = LiveDataTestUtils.getValue(this.database.taskDao().getTasks());
        assertTrue(tasks.isEmpty());
    }




}
