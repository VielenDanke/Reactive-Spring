package kz.danke.react.spring.service.impl;

import kz.danke.react.spring.document.Item;
import kz.danke.react.spring.repository.ItemReactiveRepository;
import kz.danke.react.spring.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemReactiveRepository itemReactiveRepository;

    @Override
    public Flux<Item> findAll() {
        return itemReactiveRepository.findAll();
    }

    @Override
    public Mono<Item> findById(String id) {
        return itemReactiveRepository.findById(id);
    }

    @Override
    public Mono<Item> save(Item item) {
        return itemReactiveRepository.save(item);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return itemReactiveRepository.deleteById(id);
    }

    @Override
    public Mono<Item> updateItem(final String id, final Item item) {
        return itemReactiveRepository.findById(id)
                .flatMap(byId -> {
                   byId.setDescription(item.getDescription());
                   byId.setPrice(item.getPrice());

                   return itemReactiveRepository.save(byId);
                });
    }
}
