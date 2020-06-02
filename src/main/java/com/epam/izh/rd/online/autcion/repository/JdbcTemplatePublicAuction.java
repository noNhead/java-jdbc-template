package com.epam.izh.rd.online.autcion.repository;

import com.epam.izh.rd.online.autcion.entity.Bid;
import com.epam.izh.rd.online.autcion.entity.Item;
import com.epam.izh.rd.online.autcion.entity.User;
import com.epam.izh.rd.online.autcion.mappers.BidMapper;
import com.epam.izh.rd.online.autcion.mappers.ItemMapper;
import com.epam.izh.rd.online.autcion.mappers.UserMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;

public class JdbcTemplatePublicAuction implements PublicAuction {
    private JdbcTemplate jdbcTemplate;
    private DataSource dataSource;
    private BidMapper bidMapper;
    private ItemMapper itemMapper;
    private UserMapper userMapper;

    public JdbcTemplatePublicAuction(JdbcTemplate jdbcTemplate, DataSource dataSource, BidMapper bidMapper, ItemMapper itemMapper, UserMapper userMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.dataSource = dataSource;
        this.bidMapper = bidMapper;
        this.itemMapper = itemMapper;
        this.userMapper = userMapper;
    }

    @Override
    public List<Bid> getUserBids(long id) {
        String sql = "SELECT * FROM bids WHERE user_id=" + id;
        return jdbcTemplate.query(sql, bidMapper);
    }

    @Override
    public List<Item> getUserItems(long id) {
        String sql = "SELECT * FROM items WHERE user_id=" + id;
        return jdbcTemplate.query(sql, itemMapper);
    }

    @Override
    public Item getItemByName(String name) {
        String sql = "SELECT * FROM items WHERE title LIKE ? ";
        return jdbcTemplate.queryForObject(sql, itemMapper, name);
    }

    @Override
    public Item getItemByDescription(String name) {
        String sql = "SELECT * FROM items WHERE description LIKE ?";
        return jdbcTemplate.queryForObject(sql, itemMapper, name);
    }

    @Override
    public Map<User, Double> getAvgItemCost() {
        Map<User, Double> usersAndAvgCost = new HashMap<>();
        List<User> users = jdbcTemplate.query("SELECT * FROM users", userMapper);
        for (User user : users) {
            String sql = "SELECT avg(start_price) FROM items WHERE user_id=?";
            Double price = jdbcTemplate.queryForObject(sql, Double.class, user.getUserId());
            if (price == null) {
                continue;
            }
            usersAndAvgCost.put(user, price);
        }
        return usersAndAvgCost;
    }

    @Override
    public Map<Item, Bid> getMaxBidsForEveryItem() {
        Map<Item, Bid> maxBidForEveryItem = new HashMap<>();
        List<Item> items = jdbcTemplate.query("SELECT * FROM items", itemMapper);
        for (Item item : items) {
            String sqlGetMaxPrice = "SELECT max(bid_value) FROM bids WHERE item_id=?";
            Double maxPrice = jdbcTemplate.queryForObject(sqlGetMaxPrice, Double.class, item.getItemId());
            if (maxPrice == null) {
                continue;
            }
            String sqlGetBidWithMaxPrice = "SELECT * FROM bids WHERE bid_value=?";
            Bid bidWithMaxPrice = jdbcTemplate.queryForObject(sqlGetBidWithMaxPrice, bidMapper, maxPrice.toString());
            maxBidForEveryItem.put(item, bidWithMaxPrice);
        }
        return maxBidForEveryItem;
    }

    @Override
    public boolean createUser(User user) {
        jdbcTemplate.execute("INSERT INTO users VALUES(" + user.getUserId() + ",'" + user.getBillingAddress() + "','" + user.getFullName() + "','" + user.getLogin() + "','" + user.getPassword() + "')");
        return true;
    }

    @Override
    public boolean createItem(Item item) {
        jdbcTemplate.execute("INSERT INTO items VALUES('" + item.getItemId() + "','" + item.getBidIncrement() + "','" + item.getBuyItNow() + "','" + item.getDescription() + "','" + item.getStartDate() + "','" + item.getStartPrice() + "','" + item.getStopDate() + "','" + item.getTitle() + "','" + item.getUserId() + "')");
        return true;
    }

    @Override
    public boolean createBid(Bid bid) {
        jdbcTemplate.execute("INSERT INTO bids VALUES('" + bid.getBidId() + "','" + bid.getBidDate() + "','" + bid.getBidValue() + "','" + bid.getItemId() + "','" + bid.getUserId() + "')");
        return true;
    }

    @Override
    public boolean deleteUserBids(long id) {
        jdbcTemplate.execute("DELETE FROM bids WHERE user_id=" + id);
        return true;
    }

    @Override
    public boolean doubleItemsStartPrice(long id) {
        jdbcTemplate.execute("UPDATE items SET start_price = start_price * 2 WHERE user_id=" + id);
        return true;
    }
}
