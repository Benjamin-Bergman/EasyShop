package org.yearup.data;

import org.yearup.models.*;

public interface ShoppingCartDao {
    ShoppingCart getByUserId(int userId);

    void addItem(int userId, int itemId);

    void updateItem(int userId, int itemId, int quantity);

    void clear(int userId);
}
