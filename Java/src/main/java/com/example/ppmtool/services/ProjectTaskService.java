package com.example.ppmtool.services;

import com.example.ppmtool.domain.Backlog;
import com.example.ppmtool.domain.Project;
import com.example.ppmtool.domain.ProjectTask;
import com.example.ppmtool.exceptions.ProjectIdException;
import com.example.ppmtool.exceptions.ProjectNotFoundException;
import com.example.ppmtool.repositories.BacklogRepository;
import com.example.ppmtool.repositories.ProjectRepository;
import com.example.ppmtool.repositories.ProjectTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectTaskService {

    @Autowired
    private BacklogRepository backlogRepository;

    @Autowired
    private ProjectTaskRepository projectTaskRepository;

    @Autowired
    private ProjectRepository projectRepository;

    public ProjectTask addProjectTask(String projectIdentifier, ProjectTask projectTask) {


        Backlog backlog = backlogRepository.findByProjectIdentifier(projectIdentifier);
        if(backlog == null) throw new ProjectNotFoundException("Project not found");
        projectTask.setBacklog(backlog);
        Integer BacklogSequence = backlog.getPTSequence();
        BacklogSequence++;
        backlog.setPTSequence(BacklogSequence);
        projectTask.setProjectSequence(projectIdentifier+"-"+BacklogSequence);
        projectTask.setProjectIdentifier(projectIdentifier);

        if(projectTask.getPriority()==null || projectTask.getPriority()==0) {
            projectTask.setPriority(3);
        }
        if(projectTask.getStatus()=="" || projectTask.getStatus()==null) {
            projectTask.setStatus("TO_DO");
        }
        return projectTaskRepository.save(projectTask);
    }

    public Iterable<ProjectTask> findBacklogById(String id) {
        if(projectRepository.findByProjectIdentifier(id)==null)
            throw new ProjectNotFoundException("Project with ID: '"+id+"' does not exist");
        return  projectTaskRepository.findByProjectIdentifierOrderByPriority(id);
    }

    public ProjectTask findPTByProjectSequence(String backlog_id, String pt_id) {

        Backlog backlog = backlogRepository.findByProjectIdentifier(backlog_id);
        if(backlog==null) throw new ProjectNotFoundException("Project with ID: '"+backlog_id+"' does not exist");

        ProjectTask projectTask = projectTaskRepository.findByProjectSequence(pt_id);
        if(projectTask==null) throw new ProjectNotFoundException("Project Task "+pt_id+"' not found");

        if(!projectTask.getProjectIdentifier().equals(backlog_id))
            throw new ProjectNotFoundException("Project Tak '"+pt_id+"' does not exist in project: '"+backlog_id);
        return  projectTask;
    }

    public ProjectTask updateByProjectSequence(ProjectTask updatedTask, String backlog_id) {

        Backlog backlog = backlogRepository.findByProjectIdentifier(backlog_id);
        if(backlog==null) throw new ProjectNotFoundException("Project with ID: '"+backlog_id+"' does not exist");

        return projectTaskRepository.save(updatedTask);
    }

    public void deletePTByProjectSequence(String backlog_id,String pt_id) {
        ProjectTask projectTask = findPTByProjectSequence(backlog_id,pt_id);

        projectTaskRepository.delete(projectTask);
    }
}
