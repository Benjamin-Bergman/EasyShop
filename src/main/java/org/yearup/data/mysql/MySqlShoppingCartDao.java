package org.yearup.data.mysql;

import org.springframework.security.crypto.bcrypt.*;
import org.springframework.stereotype.*;
import org.yearup.data.*;
import org.yearup.models.*;

import javax.sql.*;
import java.sql.*;
import java.util.*;

@Component
public class MySqlShoppingCartDao extends MySqlDaoBase implements ShoppingCartDao {
    public MySqlShoppingCartDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public ShoppingCart getByUserId(int userId) {
        var query = """
            SELECT s.quantity, p.product_id, p.name, p.price, p.category_id,
                p.description, p.color, p.image_url, p.stock, p.featured
            FROM shopping_cart s NATURAL JOIN products p
            WHERE s.user_id = ?""";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query);) {

            statement.setInt(1, userId);

            HashMap<Integer, ShoppingCartItem> contents;
            try (ResultSet row = statement.executeQuery()) {

                contents = new HashMap<Integer, ShoppingCartItem>();
                while (row.next()) {
                    var quantity = row.getInt(1);
                    var productId = row.getInt(2);
                    var name = row.getString(3);
                    var price = row.getBigDecimal(4);
                    var categoryId = row.getInt(5);
                    var description = row.getString(6);
                    var color = row.getString(7);
                    var url = row.getString(8);
                    var stock = row.getInt(9);
                    var featured = row.getBoolean(10);

                    var product = new Product();
                    product.setCategoryId(categoryId);
                    product.setColor(color);
                    product.setDescription(description);
                    product.setName(name);
                    product.setFeatured(featured);
                    product.setImageUrl(url);
                    product.setPrice(price);
                    product.setStock(stock);

                    var item = new ShoppingCartItem();
                    item.setQuantity(quantity);
                    item.setProduct(product);

                    contents.put(productId, item);
                }
            }

            ShoppingCart cart = new ShoppingCart();
            cart.setItems(contents);
            return cart;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addItem(int userId, int itemId) {
        String query = "SELECT quantity FROM shopping_cart WHERE user_id = ? AND product_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, userId);
            ps.setInt(2, itemId);

            try (var result = ps.executeQuery()) {
                if (result.next())
                    try (PreparedStatement ps2 = connection.prepareStatement("UPDATE shopping_cart SET quantity = ? WHERE user_id = ? AND product_id = ?")) {
                        ps2.setInt(1, result.getInt(1) + 1);
                        ps2.setInt(2, userId);
                        ps2.setInt(3, itemId);
                        ps2.executeUpdate();
                    }
                else
                    try (PreparedStatement ps2 = connection.prepareStatement("INSERT INTO shopping_cart (user_id, product_id, quantity) VALUES (?, ?, 1)")) {
                        ps2.setInt(1, userId);
                        ps2.setInt(2, itemId);
                        ps2.executeUpdate();
                    }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateItem(int userId, int itemId, int quantity) {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement("UPDATE shopping_cart SET quantity = ? WHERE user_id = ? AND product_id = ?")) {
            ps.setInt(1, quantity);
            ps.setInt(2, userId);
            ps.setInt(3, itemId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void clear(int userId) {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement("DELETE FROM shopping_cart WHERE user_id = ?")) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
