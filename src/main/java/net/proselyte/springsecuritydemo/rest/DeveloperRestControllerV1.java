package net.proselyte.springsecuritydemo.rest;

import net.proselyte.springsecuritydemo.model.Developer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public List<Developer> getAll(){
        return developers;
    }

    @GetMapping("/{id}")//дополнительно в URL указываем ID
    //c помощью @PathVariable передаем этот ID  в параметр метода
    public Developer getById(@PathVariable Long id){
        return developers.stream().filter(developer -> developer.getId().equals(id))
                .findFirst().orElse(null);
    }
}
