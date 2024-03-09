package net.proselyte.springsecuritydemo.rest;

import net.proselyte.springsecuritydemo.model.Developer;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController//помечает класс так, что он отвечает за HTTP запросы
@RequestMapping("/api/v1/developers")//создает URL для обработки запроса
public class DeveloperRestControllerV1 {

    private List<Developer> developers = Stream.of(
            new Developer(1L,"Ivan","Ivanov"),
            new Developer(2L,"Sergey","Sergeev"),
            new Developer(3L,"Petr","Petrov")
    ).collect(Collectors.toList());//создаем Лист работников с помощью Stream

    @GetMapping
    @PreAuthorize("hasAuthority('developers:read')")//определяем права доступа к методу.
    // В данном случае имеют доступ все у кого есть разрешение developers:read
    public List<Developer> getAll(){
        return developers;
    }

    @GetMapping("/{id}")//дополнительно в URL указываем ID
    //c помощью @PathVariable передаем этот ID  в параметр метода
    @PreAuthorize("hasAuthority('developers:read')")
    public Developer getById(@PathVariable Long id){
        return developers.stream().filter(developer -> developer.getId().equals(id))
                .findFirst().orElse(null);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('developers:write')")
    public Developer create(@RequestBody Developer developer){
        this.developers.add(developer);
        return developer;
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('developers:write')")
    public void deleteById(@PathVariable Long id){
        this.developers.removeIf(developer -> developer.getId().equals(id));
    }
}
