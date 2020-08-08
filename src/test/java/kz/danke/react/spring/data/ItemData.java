package kz.danke.react.spring.data;

import kz.danke.react.spring.document.Item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ItemData {

    public static final List<Item> ITEM_LIST;
    public static final String FIFTH_ITEM_ID = "ABC";

    static {
        ITEM_LIST = new ArrayList<>();

        Item first = new Item(null, "First item", 127.1);
        Item second = new Item(null, "Second item", 68.4);
        Item third = new Item(null, "Third item", 91.1);
        Item fourth = new Item(null, "Fourth item", 34.7);
        Item fifth = new Item(FIFTH_ITEM_ID, "Fifth item", 56.5);

        ITEM_LIST.addAll(Arrays.asList(first, second, third, fourth, fifth));
    }
}
