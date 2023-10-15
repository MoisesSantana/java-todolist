package br.com.moisessantana.todolist.task;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.moisessantana.todolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    
    @Autowired
    private ITaskRepository taskRepository;

    @PostMapping("/")
    public ResponseEntity create(@RequestBody TaskModel taskModel, HttpServletRequest request) {
        var idUser = request.getAttribute("idUser");
        taskModel.setIdUser((UUID) idUser);

        boolean dateIsInvalid = Utils.isInvalidDateRange(taskModel.getStartAt(), taskModel.getEndAt());

        if (dateIsInvalid) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Verifique a data de início e fim da tarefa.");
        }

        var task = this.taskRepository.save(taskModel);
        return ResponseEntity.status(HttpStatus.OK).body(task);
    }

    @GetMapping("/")
    public ResponseEntity list(HttpServletRequest request) {
        var idUser = request.getAttribute("idUser");
        var tasks = this.taskRepository.findByIdUser((UUID) idUser);
        return ResponseEntity.status(HttpStatus.OK).body(tasks);
    }

    @PutMapping("/{id}")
    public ResponseEntity update(@RequestBody TaskModel taskModel, HttpServletRequest request, @PathVariable UUID id) {
        
        var task = this.taskRepository.findById(id).orElse(null);

        if (task == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tarefa não encontrada.");
        }

        var idUser = request.getAttribute("idUser");
        
        if (!task.getIdUser().equals(idUser)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Você não tem permissão para alterar esta tarefa.");
        }

        Utils.copyNonNullProperties(taskModel, task);

        
        var taskUpdated = this.taskRepository.save(task);
 
        boolean dateIsInvalid = Utils.isInvalidDateRange(taskUpdated.getStartAt(), taskUpdated.getEndAt());

        if (dateIsInvalid) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Verifique a data de início e fim da tarefa.");
        }
 
        return ResponseEntity.status(HttpStatus.OK).body(taskUpdated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(HttpServletRequest request, @PathVariable UUID id) {
        var task = this.taskRepository.findById(id).orElse(null);

        if (task == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tarefa não encontrada.");
        }

        var idUser = request.getAttribute("idUser");
        
        if (!task.getIdUser().equals(idUser)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Você não tem permissão para alterar esta tarefa.");
        }

        this.taskRepository.delete(task);
        return ResponseEntity.status(HttpStatus.OK).body("Tarefa excluída com sucesso.");
    }
}
