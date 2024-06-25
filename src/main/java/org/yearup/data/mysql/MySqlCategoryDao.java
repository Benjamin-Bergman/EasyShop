package org.yearup.data.mysql;

import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.server.*;
import org.yearup.data.CategoryDao;
import org.yearup.models.Category;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

@Component
public class MySqlCategoryDao extends MySqlDaoBase implements CategoryDao {
    public MySqlCategoryDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public List<Category> getAllCategories() {
        try (var con = getConnection();
             var st = con.prepareStatement("SELECT * FROM categories");
             var res = st.executeQuery()) {
            var cats = new ArrayList<Category>();
            while (res.next())
                cats.add(new Category(
                    res.getInt("category_id"),
                    res.getString("name"),
                    res.getString("description")
                ));
            return cats;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Category getById(int categoryId) {
        try (var con = getConnection();
             var st = con.prepareStatement("SELECT * FROM categories WHERE category_id = ?")) {
            st.setInt(1, categoryId);
            try (var res = st.executeQuery()) {
                if (res.next())
                    return new Category(
                        res.getInt("category_id"),
                        res.getString("name"),
                        res.getString("description")
                    );
            }
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Category create(Category category) {
        try (var con = getConnection();
             var st = con.prepareStatement("INSERT INTO categories (name, description) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            st.setString(1, category.getName());
            st.setString(2, category.getDescription());
            var rowsAffected = st.executeUpdate();

            // Retrieve the generated keys
            if (rowsAffected > 0)
                try (ResultSet generatedKeys = st.getGeneratedKeys()) {
                    if (generatedKeys.next())
                        return getById(generatedKeys.getInt(1));
                }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(int categoryId, Category category) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("UPDATE categories SET name = ?, description = ? WHERE category_id = ?;");) {
            statement.setString(1, category.getName());
            statement.setString(2, category.getDescription());
            statement.setInt(3, categoryId);

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(int categoryId) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM categories WHERE category_id = ?;");) {
            statement.setInt(1, categoryId);

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
