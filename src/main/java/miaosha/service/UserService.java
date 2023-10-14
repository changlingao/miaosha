package miaosha.service;

import miaosha.dao.UserDao;
import miaosha.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    UserDao userDao;

    public User getById(int id) {
        return userDao.getById(id);
    }

    // 事务：原子性，所有要么发生要么不发生
    @Transactional
    public boolean tx() {
        // no problem
        User user1 = new User();
        user1.setId(2);
        user1.setName("2222");
        userDao.insert(user1);

        // same id
        User user2 = new User();
        user2.setId(1);
        user2.setName("1111");
        userDao.insert(user2);

        return true;
    }

}
