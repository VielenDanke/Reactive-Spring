package kz.danke.react.spring.controller.v1;

import kz.danke.react.spring.constants.ItemConstants;
import kz.danke.react.spring.document.ItemCapped;
import kz.danke.react.spring.repository.ItemReactiveCappedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping(ItemConstants.ITEM_CAPPED_REST_MAPPING)
public class ItemStreamController {

    private final ItemReactiveCappedRepository itemReactiveCappedRepository;

    @Autowired
    public ItemStreamController(ItemReactiveCappedRepository itemReactiveCappedRepository) {
        this.itemReactiveCappedRepository = itemReactiveCappedRepository;
    }

    @GetMapping(produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<ItemCapped> getItemsStream() {
        return itemReactiveCappedRepository.findItemsBy();
    }
}
