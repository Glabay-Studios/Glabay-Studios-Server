package io.xeros.model.items;

import io.xeros.util.ItemConstants;

public class NamedItem {

    private int id;
    private String name;
    private int amount;

    public NamedItem() {
    }

    public NamedItem(String name, int amount) {
        this.id = -1;
        this.name = name;
        this.amount = amount;
    }

    public GameItem toGameItem(ItemConstants itemConstants) {
        return new GameItem(getId(itemConstants), amount);
    }

    public ImmutableItem toImmutableItem(ItemConstants itemConstants) {
        return new ImmutableItem(getId(itemConstants), amount);
    }

    public int getId(ItemConstants itemConstants) {
        return id != 0 ? id : itemConstants.get(name);
    }

    public String getName() {
        return name;
    }

    public int getAmount() {
        return amount;
    }

    private int getId() {
        return id;
    }

    private void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
