package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.CategoryDao;
import org.yearup.models.Category;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
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
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Category create(Category category) {
        // create a new category
        return null;
    }

    @Override
    public void update(int categoryId, Category category) {
        // update category
    }

    @Override
    public void delete(int categoryId) {
        // delete category
    }

    private Category mapRow(ResultSet row) throws SQLException {
        int categoryId = row.getInt("category_id");
        String name = row.getString("name");
        String description = row.getString("description");

        Category category = new Category();
        category.setCategoryId(categoryId);
        category.setName(name);
        category.setDescription(description);


        return category;
    }

}
