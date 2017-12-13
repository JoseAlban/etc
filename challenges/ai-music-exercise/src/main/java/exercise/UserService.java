package exercise;

import com.google.common.collect.ImmutableMap;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private static final ImmutableMap<Integer, String> USERNAMES = ImmutableMap.of(
            1, "user1",
            2, "user2"
    );

    String getUsername(int userId) {
        return USERNAMES.get(userId);
    }
}
