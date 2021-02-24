package pl.javastart.lekcja26restcwiczenie1;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RequestMapping("/api/tasks") // wszystkie elementy zaczynają się od tego kawałka
@RestController
public class TaskRestController {
    // metody będą dostarczały całe obiekty

    //wstrzykuje reposytory

    private final TaskRepository taskRepository;

    public TaskRestController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    //domyślnie typ zwracany to json
    @GetMapping("")
    // zwracamy lieste, za pomocą metody get pobieramy wszystkie zasoby, api/tasks - odwołujemy się do wszystkich elementów
    public List<Task> findAll() {
        List<Task> tasks = taskRepository.findAll();
        return tasks;
    }

    //szukanie po id
    //domyślnie typ zwracany to json
    @GetMapping("/{id}")
    public ResponseEntity<Task> findById(@PathVariable Long id) { // zwracamy jednego taska

        Optional<Task> taskOptional = taskRepository.findById(id); // sprawdzamy czy findbyid
//        if (taskOptional.isPresent()) {
//            return ResponseEntity.ok(taskOptional.get()); // zwróci task i status odpowiedzi 200
//        }
//        return ResponseEntity.status(404).build(); //lub notFound

        //zamiast if //można też użyć referencji na metode
        return taskOptional
                .map(ResponseEntity::ok) // optional zmapowany na ok
                .orElseGet(() -> ResponseEntity.notFound().build()); // w przeciwnym przypadku jesli nie jest zmapowany zwroci kod 404 not found
        // not found = status(404) // bład po stronie klienta, zapytał o task który nie istnieje
    }

    //dodawanie pojedyńczego zadania
    @PostMapping("")
    public ResponseEntity<Task> addTask(@RequestBody Task task) {
        if (task.getId() != null) { // jeśli id nie jest nulem to zwracamy badRequest czyli bład 400, ktoś zle dodał użytkownika np zle dane
            return ResponseEntity.badRequest().build();
        }
        taskRepository.save(task); // task dostanie id i zostanie zwrócony
        return ResponseEntity.ok(task); // jeśli wszystko ok zwracamy taska z statusem ok
        // jesli chcesz wyslac dane to nie nadawaj id, my nadajemy id
    }

    // aktualizacja zadania
    @PutMapping("/{id}")
    public ResponseEntity<Task> addTask(@PathVariable Long id, @RequestBody Task task) {

        Optional<Task> taskOptional = taskRepository.findById(id);
        if (taskOptional.isPresent()) {
            Task taskInDb = taskOptional.get(); // jeśli zasób jest dostępny to pobierzemy tą wartosć to będziemy aktualizować
            taskInDb.setName(task.getName()); //ustawiamy imie
            taskInDb.setCreatedAt(task.getCreatedAt()); //ustawiamy date
            taskRepository.save(taskInDb); // zapisujemy zmiany
            return ResponseEntity.ok(task); // zwracamy zapisanego taska z statusem ok
        } else {
            return ResponseEntity.notFound().build(); // w przeciwnym przypadku nie znaleziono czyli 404
        }

        // metodą Patch możemy zaktualizować pojedyńcze obiekty z zasobu np name, jezeli w put podamy tylko jeden obiekt z zasobu t inne tez sie nadpiszą nullem dlatego trzeba podawać cały zasób a zmieniać tylko interesujacy nas obiekt
    }

    //usówanie
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        //jeżeli będziemy chcieli usunąć nie istniejący zasób to obsłużymy wyjątek
        try {
            taskRepository.deleteById(id);
        } catch (EmptyResultDataAccessException ignore) { //ignorujemy wyjątek
            //ignore
        }
    }
}
