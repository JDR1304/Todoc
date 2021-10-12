package com.cleanup.todoc.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.cleanup.todoc.model.Project;
import com.cleanup.todoc.model.Task;
import com.cleanup.todoc.repositories.ProjectDataRepository;
import com.cleanup.todoc.repositories.TaskDataRepository;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

public class TaskViewModel extends ViewModel {

    // REPOSITORIES
    private final TaskDataRepository taskDataSource;
    private final ProjectDataRepository projectDataSource;
    private final Executor executor;


    /**
     * The sort method to be used to display tasks
     */
    @NonNull
    private SortMethod sortMethod = SortMethod.OLD_FIRST;

    // DATA
    @Nullable
    private LiveData<Project> currentProject;
    private LiveData<List<Task>> tasks;
    private LiveData<List<Project>> projects;

    private List<Project> listOfProject;

    public TaskViewModel(TaskDataRepository taskDataSource, ProjectDataRepository projectDataSource, Executor executor) {
        this.taskDataSource = taskDataSource;
        this.projectDataSource = projectDataSource;
        this.executor = executor;
    }

    // -------------
    // FOR PROJECT
    // -------------

    public LiveData<Project> getProject(long projectId) { return this.currentProject;  }

    public LiveData<List<Project>> getProjects() {
        projects = projectDataSource.getProjects();
        return projects;
    }

    // -------------
    // FOR TASK
    // -------------

    public LiveData<List<Task>> getTasks() {
        tasks =taskDataSource.getTasks();
        return tasks;
    }

    public void createTask(Task task) {
        executor.execute(() -> {
            taskDataSource.createTask(task);
        });
    }

    public void deleteTask(long taskId) {
        executor.execute(() -> {
            taskDataSource.deleteTask(taskId);
        });
    }


    /**
     * Updates the list of tasks in the UI
     */
    public void sortTasks(List <Task> tasks) {

        switch (sortMethod) {
            case ALPHABETICAL:
                Collections.sort(tasks, new Task.TaskAZComparator());
                break;
            case ALPHABETICAL_INVERTED:
                Collections.sort(tasks, new Task.TaskZAComparator());
                break;
            case RECENT_FIRST:
                Collections.sort(tasks, new Task.TaskRecentComparator());
                break;
            case OLD_FIRST:
                Collections.sort(tasks, new Task.TaskOldComparator());
                break;

        }
    }
    @NonNull
    public SortMethod getSortMethod() {
        return sortMethod;
    }

    public void setSortMethod(SortMethod sortMethod){
        this.sortMethod = sortMethod;
        }

    // Method which pick up the projects list from activity to ViewModel
    public void updateProjectsList(List<Project> listOfProjects) {
        this.listOfProject = listOfProjects;
    }
    // Method which supplies the projects list
    public List<Project> getListOfProject(){
        return listOfProject;
    }

    /**
     * List of all possible sort methods for task
     */
    public enum SortMethod {
        /**
         * Sort alphabetical by name
         */
        ALPHABETICAL,
        /**
         * Inverted sort alphabetical by name
         */
        ALPHABETICAL_INVERTED,
        /**
         * Lastly created first
         */
        RECENT_FIRST,
        /**
         * First created first
         */
        OLD_FIRST,
        /**
         * No sort
         */
        NONE
    }
}
