package com.learnreactivespring.learnreactivespring.controller.v1;

import com.learnreactivespring.learnreactivespring.constants.ItemConstants;
import com.learnreactivespring.learnreactivespring.document.CappedItem;
import com.learnreactivespring.learnreactivespring.repository.CappedItemReactiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class ItemStreamController {

    @Autowired
    private CappedItemReactiveRepository cappedItemReactiveRepository;

    @GetMapping(value = ItemConstants.ITEM_STREAM_END_POINT_V1, produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<CappedItem> getItemStream() {
        return cappedItemReactiveRepository.findItemsBy();
    }
}
