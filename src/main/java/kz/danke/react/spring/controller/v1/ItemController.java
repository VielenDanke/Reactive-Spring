package kz.danke.react.spring.controller.v1;

import kz.danke.react.spring.document.Item;
import kz.danke.react.spring.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static kz.danke.react.spring.constants.ItemConstants.ITEM_REST_MAPPING;

@RestController
@RequestMapping(ITEM_REST_MAPPING)
@Slf4j
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public Flux<Item> getAllItems() {
        return itemService.findAll();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Item>> getItemById(@PathVariable(name = "id") String id) {
        return itemService.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<Item>> saveItem(@RequestBody Item item) {
        return itemService.save(item)
                .map(it -> new ResponseEntity<>(it, HttpStatus.CREATED))
                .defaultIfEmpty(ResponseEntity.noContent().build());
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteItem(@PathVariable(name = "id") String id) {
        return itemService.deleteById(id);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Item>> updateItem(
            @PathVariable(name = "id") String id,
            @RequestBody Item item
    ) {
        return itemService.updateItem(id, item)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/exception/occurred")
    public Flux<Item> getRuntimeException() {
        return itemService.findAll()
                .concatWith(Mono.error(new RuntimeException("Runtime exception occurred")));
    }
}
